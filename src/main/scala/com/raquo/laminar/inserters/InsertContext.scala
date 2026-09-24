package com.raquo.laminar.inserters

import com.raquo.airstream.core.AirstreamError
import com.raquo.ew.{JsArray, JsMap}
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.nodes.{ChildNode, CommentNode, ReactiveElement}
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.|

// #TODO[Naming] This feels more like InserterState?
//  "Extra nodes" are more like "content nodes"

// @Note only parentNode and sentinelNode are used by all Inserter-s.
//  - Other fields may remain un-updated if they are not needed for a particular use case.

/**
  * InsertContext represents the state of the DOM inside an inserter block like `child <-- ...`,
  * `children <-- ...`, `text <-- ...`, etc. The data stored in this context is used
  * by Laminar to efficiently update the DOM, to detect (and recover from) external changes
  * to the DOM, and for other related tasks.
  *
  * InsertContext is a mutable data structure that is created once for each inserter, and the
  * inserter updates it as it processes new data. However, in case of `onMountInsert`, only
  * one context is created, and is then reused for all inserters created inside
  * `onMountInsert`. This allows for intuitive preservation of DOM state if the element is
  * unmounted and mounted again (for example, `onMountInsert(child <-- stream)` will
  * keep the last emitted child in the DOM even if the element is unmounted and re-mounted).
  *
  * #Note: The params that describe `extraNodes` below can get out of sync with the real DOM.
  *
  * This can happen if an child element is removed from the DOM – either externally, or more
  * likely because it was moved from this inserter into another one, and the addition to the
  * other inserter was processed before the removal from this inserter is processed (the
  * order of these operations depends on the propagation order of the observables feeding
  * these two inserters). The Inserter code must account for this and not fail in such cases,
  * and must correct the values accordingly on the next observable update.
  *
  * #Note: The params that describe `extraNodes` below must be kept consistent manually (#Perf)
  *
  *                              Inserter "steals" an element from this one just before the
  *                              observable source of this inserter provides a new list of
  *                              children (with the stolen element removed from the list).
  *
  * @param sentinelNode        - A special invisible comment node that tells Laminar where to
  *                              insert the dynamic children, and where to expect previously
  *                              inserted dynamic children. Content nodes always live AFTER
  *                              this sentinel, which stays put for the life of the context –
  *                              this is what lets users move an element from one inserter to
  *                              another, or externally remove elements an inserter added.
  */
final class InsertContext(
  val sentinelNode: CommentNode,
  initialParentNode: ReactiveElement.Base,
  initialSlotName: String | Unit
) {

  private var _currentParentNode: ReactiveElement.Base = initialParentNode

  /** The element that CURRENTLY hosts this inserter's content.
    *
    * It can change in one scenario: if this [[InsertContext]] was created by
    * a [[NestedGroup]] inside a `children <--` inserter, AND this exact
    * nested group was later moved into ANOTHER `children <--` inserter.
    *
    * In that scenario, we want to keep the nodes in the nested group mounted,
    * and I haven't figured out how to implement that without making parentNode
    * mutable on InserterContext. Maybe in the future I can isolate this
    * mutability to NestedGroup itself, but the shape of `insertFn` is
    * preventing that right now.
    */
  def currentParentNode: ReactiveElement.Base = _currentParentNode

  /** Point this context at a new host element.
    * #Warning: Only a [[NestedGroup]] move should call this – see [[currentParentNode]].
    */
  def setCurrentParentNode(newParentNode: ReactiveElement.Base): Unit = {
    _currentParentNode = newParentNode
  }

  // --

  /** The slot CURRENTLY in effect for content inserted into this context.
    *
    * Like [[currentParentNode]], this is mutable because a [[NestedGroup]]
    * can be moved between `children <--` lists: on such a move the group
    * resolves the destination list's slot against its own and stores the
    * result here, so that the inner inserter's future emissions (which read
    * this value) pick up the destination's slot.
    */
  private var _currentSlotName: String | Unit = initialSlotName

  def currentSlotName: String | Unit = _currentSlotName

  def setCurrentSlotName(newSlotName: String | Unit): Unit = {
    _currentSlotName = newSlotName
  }

  // --

  /** A comment node marking the END of this context's span. Present when EITHER is true:
    *  - [[InserterType.needsTrailingSentinel]] (true for multi-node inserters), OR
    *  - [[_forceTrailingSentinel]] (true for list items under `children <--`)
    */
  private var _trailingSentinelNodeOpt: js.UndefOr[CommentNode] = js.undefined

  def trailingSentinelNodeOpt: js.UndefOr[CommentNode] = _trailingSentinelNodeOpt

  // --

  private var _forceTrailingSentinel: Boolean = false

  // --

  private var _lastInserterType: js.UndefOr[InserterType] = js.undefined

  def lastInserterType: js.UndefOr[InserterType] = _lastInserterType

  // --

  /** Inserters that are rendered into this context.
    * Does not include sentinel node(s).
    *
    * (inserter.stableFirstNode -> inserter)
    */
  var contentMap: JsMap[dom.Node, DiffableInserter] = new JsMap()

  /** Get the inserter in contentMap, or throw if there are multiple inserters there.
    * Precondition: the inserter currently using this context is a single-node inserter.
    */
  def singleContentMapItem: js.UndefOr[DiffableInserter] = {
    val numInserters = contentMap.size
    if (numInserters == 0) {
      js.undefined
    } else if (numInserters == 1) {
      contentMap.entries().next().value._2
    } else {
      throw new Exception(s"singleContentMapItem: Found multiple (${numInserters}) content nodes without trailing sentinel. This is a bug in Laminar.")
    }
  }

  // --

  /** Why this works: any multi-node inserter will have a trailing sentinel node at all times,
    * and singleContentMapItem.lastNode + sentinelNode cover all other possibilities.
    *
    * In case of `singleContentMapItem`, we only use that node if it's actually found in the DOM
    * after the leading sentinel where it belongs. Otherwise, it's been stolen, so we report
    * the leading sentinel as the de-facto last node instead.
    */
  def lastNodeInDom: dom.Node = {
    _trailingSentinelNodeOpt.map(_.ref)
      .orElse {
        singleContentMapItem
          .filter(_.stableFirstNode == sentinelNode.ref.nextSibling)
          .map(_.lastNode)
      }
      .getOrElse(sentinelNode.ref)
  }

  /** Call this if / when this group is rendered as a `children <--` list item. */
  def forceTrailingSentinel(): Unit = {
    _forceTrailingSentinel = true
    ensureTrailingSentinel()
  }

  private def ensureTrailingSentinel(): Unit = {
    if (_trailingSentinelNodeOpt.isEmpty) {
      val trailingSentinel = new CommentNode("")
      DomApi.insertChildAfter(
        parent = currentParentNode,
        newChild = trailingSentinel,
        referenceChildRef = lastNodeInDom,
        slotName = () // comment nodes are never slotted
      )
      _trailingSentinelNodeOpt = trailingSentinel
    }
  }

  /** Removes old content both from contentMap and from the DOM.
    *
    * @param replaceContentMapWithSingleNode
    *            If specified, will ensure that the resulting contentMap has this node.
    *            If specified, will prevent this node from being removed from the DOM
    *            (even if it's nested in a dynamic item), but will NOT add it to the DOM.
    * @param keepItem Items to leave in the DOM – see [[removeContentMapNodesFromDom]].
    */
  def clearPreviousInserterContent(
    replaceContentMapWithSingleNode: js.UndefOr[ChildNode.Base],
    nextInserterType: js.UndefOr[InserterType],
    keepItem: dom.Node => Boolean = InsertContext.keepNoItems
  ): Unit = {
    // Remove from the DOM any old nodes that shouldn't be retained.
    removeContentMapNodesFromDom(
      keepItem = replaceContentMapWithSingleNode.fold(keepItem) { singleNode =>
        ref => (ref eq singleNode.ref) || keepItem(ref)
      }
    )

    // Update the context to match the DOM state
    contentMap.clear()
    replaceContentMapWithSingleNode.foreach { newNode =>
      contentMap.set(newNode.ref, newNode)
    }
    setNextInserterType(nextInserterType)
  }

  def setNextInserterType(nextInserterType: js.UndefOr[InserterType]): Unit = {
    if (nextInserterType.exists(_.needsTrailingSentinel) || _forceTrailingSentinel) {
      ensureTrailingSentinel()
    } else {
      _trailingSentinelNodeOpt.foreach { trailingSentinel =>
        DomApi.removeChild(parent = currentParentNode, child = trailingSentinel)
        _trailingSentinelNodeOpt = js.undefined
      }
    }
    // Update context inserter type
    _lastInserterType = nextInserterType
  }

  /** Walk this context's span forward from [[sentinelNode]], removing every tracked
    * ([[contentMap]]) item from the DOM except those matching `keepItem`.
    *
    * @param keepItem Called with each item's `stableFirstNode`, the item's identity (the same
    *                 key as in [[contentMap]]): the node itself for a plain or slotted node,
    *                 or the leading sentinel for a dynamic inserter.
    *
    * `keepItem` is also applied recursively inside the removed dynamic items: a kept item is
    * left in place under the same parent (a dynamic inserter with its whole span and live
    * subscription), even if the nested group that contained it is torn down. It never loses
    * its parent, so it's not unmounted. This lets the caller re-insert it seamlessly.
    *
    * #Warning: The caller must promptly place every kept item into the DOM where it belongs,
    *  and track it there. A kept item is left where it was, untracked by this context – and
    *  if it came from inside a removed nested group, it's untracked by anyone.
    *
    * Where the walk stops depends on whether we have a [[_trailingSentinelNodeOpt]]:
    *  - With one (a `children <--` or `children.command <--` span), it marks the definite
    *    end of our content, so we walk right up to it and REPORT any node in between that we
    *    don't recognize – we allow external removals from our span, but not external
    *    insertions into it.
    *  - Without one (`child <--` / `text <--`), our span has no end marker, so the first
    *    untracked node is simply the next sibling after our content, and we stop there
    *    quietly (we can't tell an intruder from a legitimate following sibling).
    *
    * #Note: Importantly, this walks only over the tracked nodes that are actually in the DOM.
    *  If some of the tracked nodes have already been moved to a different place, we ignore them –
    *  presumably they are now being tracked by their new host.
    *
    * #Note: this does NOT update [[contentMap]] to match the new DOM.
    */
  def removeContentMapNodesFromDom(
    keepItem: dom.Node => Boolean
  ): Unit = {
    val hasTrailingSentinel = _trailingSentinelNodeOpt.nonEmpty
    var maybeRef = sentinelNode.ref.nextSibling
    var continue = true
    while (continue && maybeRef != null) {
      val childRef = maybeRef
      if (_trailingSentinelNodeOpt.exists(_.ref == childRef)) {
        // Reached the end of our span. Stop.
        continue = false
      } else if (keepItem(childRef)) {
        // An item we're keeping. Leave it in place, step over it (in one go, if it's a nested
        // inserter's span), and keep clearing whatever old content sits after it.
        maybeRef = contentMap.get(childRef).fold(childRef.nextSibling)(_.lastNode.nextSibling)
      } else {
        contentMap.get(childRef).fold {
          if (hasTrailingSentinel) {
            // Our content definitively extends to the trailing sentinel, so this untracked
            // node sitting before it is an unauthorized addition. Report it and step over it
            // (we leave it in place – it isn't ours to remove).
            DomApi.maybeReportDomError(
              s"Found unexpected node not tracked by Laminar: `${DomApi.debugNodeDescription(childRef)}`"
            )
            maybeRef = childRef.nextSibling
          } else {
            // No trailing sentinel: the first untracked node is the next sibling after our
            // span, which is expected. Stop.
            continue = false
          }
        } { inserter =>
          // Capture the continuation BEFORE removal mutates the DOM, and jump by `lastNode`
          // so a multi-node span (e.g. a nested group) is stepped over in one go.
          val nextRef = inserter.lastNode.nextSibling
          // @Note: DOM update
          inserter.removeFromDynamicList(currentParentNode, keepItem)
          maybeRef = nextRef
        }
      }
    }
  }

  /** Walk this context's span forward from [[sentinelNode]] and collect the tracked
    * ([[contentMap]]) inserters currently present in the DOM, in DOM order.
    *
    * This is the read-only twin of [[removeContentMapNodesFromDom]] (same traversal, no DOM
    * changes). It exists because [[contentMap]] is only a lookup index: its iteration order is
    * NOT kept in sync with the DOM (e.g. `children.command <--` Prepend / Insert build the DOM
    * out of insertion order), and it retains entries for nodes that have since left our span
    * (stolen by another host). Anything that must relocate our CURRENT content – notably
    * [[NestedGroup.moveToParent]] – must therefore read the DOM, not the map: exactly the nodes
    * still between our sentinels, in the order they sit there.
    *
    * Departed nodes are absent from the span, so they are skipped – they belong to their new host
    * now. Untracked nodes (external insertions) have no inserter to manage them, so they are
    * stepped over and left in place.
    */
  def currentContentInsertersFromDom: JsArray[DiffableInserter] = {
    val result = JsArray[DiffableInserter]()
    val hasTrailingSentinel = _trailingSentinelNodeOpt.nonEmpty
    var nextNode = sentinelNode.ref.nextSibling
    while (nextNode != null) {
      val node = nextNode
      if (_trailingSentinelNodeOpt.exists(_.ref == node)) {
        // Found trailing sentinel – reached the end of our span. Stop.
        nextNode = null
      } else {
        contentMap.get(node).fold {
          // Found untracked node...
          if (hasTrailingSentinel) {
            // ... before the trailing sentinel: not ours to manage – step over it.
            nextNode = node.nextSibling
          } else {
            // ... Without trailing sentinel, this means we're outside of our span. Stop.
            nextNode = null
          }
        } { inserter =>
          // Step over this inserter's whole span at once (a nested group spans many nodes).
          // Note: `lastNode` does match the DOM here: a content node reports itself (which
          //       we already found in the DOM as `node` above), and a nested dynamic item
          //       always carries a sticky trailing sentinel as a `children <--` list item,
          //       so it never falls back to a (possibly stolen) tracked node that's in
          //       contentMap but not in the DOM.
          result.push(inserter)
          nextNode = inserter.lastNode.nextSibling
        }
      }
    }
    result
  }
}

object InsertContext {

  private[laminar] val keepNoItems: dom.Node => Boolean = _ => false

  /** Reserve the spot for when we actually insert real nodes later */
  def reserveSpotContext(
    parentNode: ReactiveElement.Base,
  ): InsertContext = {
    val sentinelNode = new CommentNode("")

    DomApi.appendChild(
      parent = parentNode,
      child = sentinelNode,
      slotName = ()
    )

    unsafeMakeReservedSpotContext(
      sentinelNode = sentinelNode,
      initialParentNode = parentNode,
      initialSlotName = ()
    )
  }

  /** Reserve the spot for when we actually insert real nodes later.
    *
    * Unsafe: you must make sure yourself that sentinelNode is already
    * a child of parentNode in the real DOM.
    *
    * This method is exposed to help third parties make hydration helpers.
    *
    * #Note: `parentNode` on InsertContext is mutable.
    *  See [[InsertContext.currentParentNode]].
    */
  def unsafeMakeReservedSpotContext(
    sentinelNode: CommentNode,
    initialParentNode: ReactiveElement.Base,
    initialSlotName: String | Unit
  ): InsertContext = {
    new InsertContext(
      sentinelNode = sentinelNode,
      initialParentNode = initialParentNode,
      initialSlotName = initialSlotName
    )
  }

}
