package com.raquo.laminar.inserters

import com.raquo.airstream.core.Observable
import com.raquo.ew.JsMap
import com.raquo.laminar
import com.raquo.laminar.modifiers.{RenderableInserter, RenderableSeq}
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement}
import org.scalajs.dom

import scala.collection.immutable
import scala.scalajs.js.|

object ChildrenInserter {

  @deprecated("`Child` type alias is deprecated. Use ChildNode.Base", "15.0.0-M6")
  type Child = ChildNode.Base

  @deprecated("`Children`type alias is deprecated. Use immutable.Seq[ChildNode.Base]", "15.0.0-M6")
  type Children = immutable.Seq[ChildNode.Base]

  def apply[Collection[_], Component](
    childrenSource: Observable[Collection[Component]],
    renderableSeq: RenderableSeq[Collection],
    renderableInserter: RenderableInserter[Component],
    initialSlotName: String | Unit
  ): DynamicInserter = {
    new DynamicInserter(
      insertFn = (ctx, owner) => {
        childrenSource.foreach { components =>
          switchToChildren(
            nextItems = renderableSeq.toSeq(components),
            renderable = renderableInserter,
            ctx = ctx,
            slotName = ctx.currentSlotName
          )
        }(using owner)
      },
      slotName = initialSlotName
    )
  }

  def switchToChildren[Component](
    nextItems: laminar.Seq[Component],
    renderable: RenderableInserter[Component],
    ctx: InsertContext,
    slotName: String | Unit
  ): Unit = {
    ctx.setNextInserterType(InserterType.ChildrenType)

    ctx.contentMap = updateChildren(
      nextItems = nextItems,
      renderable = renderable,
      prevContentMap = ctx.contentMap,
      listParentNode = ctx.currentParentNode,
      listSentinelNodeRef = ctx.sentinelNode.ref,
      listTrailingSentinelRef = ctx.trailingSentinelNodeOpt.map(_.ref),
      slotName = slotName
    )
  }

  /** Updates the DOM and returns new contentMap */
  private def updateChildren[Component](
    nextItems: laminar.Seq[Component],
    renderable: RenderableInserter[Component], // avoids the need to create intermediate collection from `nextItems`
    prevContentMap: JsMap[dom.Node, Inserter],
    listParentNode: ReactiveElement.Base, // parent node of the children list
    listSentinelNodeRef: dom.Comment, // sentinel node of the children list
    listTrailingSentinelRef: dom.Comment | Unit, // trailing sentinel marking the end of the list's content
    slotName: String | Unit
  ): JsMap[dom.Node, Inserter] = {

    def isContentEnd(ref: dom.Node): Boolean =
      ref == null || listTrailingSentinelRef.contains(ref)

    // Build an efficiently searchable map of next inserters
    val nextInsertersMap = new JsMap[dom.Node, Inserter]()
    nextItems.foreach { nextItem =>
      val nextInserter = renderable.asInserter(nextItem)
      nextInsertersMap.set(nextInserter.stableFirstNode, nextInserter)
    }

    // Iteration state
    var index: Int = 0
    var currentItemCount: Int = prevContentMap.size
    var afterRef: dom.Node = listSentinelNodeRef // last DOM node of the last placed item
    var prevItemRef: dom.Node = listSentinelNodeRef.nextSibling

    // Map iteration preserves source order without converting the items again.
    nextInsertersMap.forEach { (nextInserter, _) =>
      val foundInserterInPrevMap: Boolean =
        prevContentMap.has(nextInserter.stableFirstNode)

      if (index >= currentItemCount) {
        // Overflow – we've consumed all previous items:
        // Just insert nextInserter at the cursor (or move it there if this inserter it was previously in the list)
        if (foundInserterInPrevMap) {
          // @Note: DOM update
          nextInserter.moveWithinDynamicList(listParentNode, afterRef, slotName)
        } else {
          currentItemCount += 1
          // @Note: DOM update
          nextInserter.addToDynamicList(listParentNode, afterRef, slotName)
        }
      } else {
        if (foundInserterInPrevMap) {
          if (nextInserter.stableFirstNode == prevItemRef) {
            // Item already in the right place – no DOM move needed.
            // -- BUT! --
            // Its target slot may have changed (a different wrapper now slots the
            // same node, or the list's slot changed), so reconcile the slot in place.
            // @Note: DOM update
            nextInserter.applySlot(listParentNode, slotName)
          } else {
            // Item exists, but elsewhere. First remove any items at the cursor that
            // are not in the new list (they are leaving anyway – this often lets us
            // avoid a move), then move this item to the cursor if still needed.
            while (
              nextInserter.stableFirstNode != prevItemRef &&
              !nextInsertersMap.has(prevItemRef) &&
              !isContentEnd(prevItemRef)
            ) {
              val prevInserter = prevInserterFromStableFirstNode(prevContentMap, prevItemRef)
              val nextPrevItemRef = prevInserter.lastNode.nextSibling
              // @Note: DOM update
              prevInserter.removeFromDynamicList(listParentNode)
              prevItemRef = nextPrevItemRef
              currentItemCount -= 1
            }
            if (nextInserter.stableFirstNode != prevItemRef) {
              // Still not in place – this is a MOVE, so we do NOT change the count.
              // @Note: DOM update
              nextInserter.moveWithinDynamicList(listParentNode, afterRef, slotName)
            }
          }
        } else {
          // Brand-new item – insert it at the cursor.
          currentItemCount += 1
          // @Note: DOM update
          nextInserter.addToDynamicList(listParentNode, afterRef, slotName)
        }
      }

      // Note: nextInserter is now guaranteed to be in the right place.

      afterRef = nextInserter.lastNode

      // Advance the cursor past the item we just placed.
      val afterSpan = afterRef.nextSibling
      if (isContentEnd(afterSpan)) {
        // Reached the end of our content unexpectedly early:
        // – found the trailing sentinel, or the end of the DOM
        // - this is due to external removals of nodes from the DOM
        // Correct the count; remaining items will hit the overflow
        // branch above. See the single-node algorithm's note re: issue #120.
        currentItemCount = index + 1
      } else {
        prevItemRef = afterSpan
      }
      index += 1
    }

    // Delete any leftover previous items
    while (index < currentItemCount && !isContentEnd(prevItemRef)) {
      val prevInserter = prevInserterFromStableFirstNode(prevContentMap, prevItemRef)
      val nextPrevItemRef = prevInserter.lastNode.nextSibling
      // @Note: DOM update
      prevInserter.removeFromDynamicList(listParentNode)
      prevItemRef = nextPrevItemRef
      currentItemCount -= 1
    }

    nextInsertersMap
  }

  private def prevInserterFromStableFirstNode(
    prevContentMap: JsMap[dom.Node, Inserter],
    stableFirstNode: dom.Node
  ): Inserter = {
    prevContentMap
      .get(stableFirstNode)
      .getOrElse(
        throw new Exception(s"prevInserterFromRef[children]: not found for ${stableFirstNode}")
      )
  }

}
