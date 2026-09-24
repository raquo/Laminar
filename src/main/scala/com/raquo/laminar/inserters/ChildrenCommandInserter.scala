package com.raquo.laminar.inserters

import com.raquo.airstream.core.EventStream
import com.raquo.ew.JsSet
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.RenderableNode
import com.raquo.laminar.nodes.{ChildNode, CommentNode}
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.|

/** Note: this is a low level inserter. It is the fastest one in certain cases,
  * but due to its rather imperative API, its usefulness is very limited.
  *
  * It's good for simple operations on voluminous data, like prepending new
  * log items to a big list, but not much else.
  *
  * Consider using `children <-- observable.splitSeq(...)` instead, it has
  * great performance and is much more convenient.
  */
object ChildrenCommandInserter {

  @deprecated("`ChildrenCommand` type alias is deprecated. Use CollectionCommand[Node]", "15.0.0-M5")
  type ChildrenCommand = CollectionCommand[ChildNode.Base]

  def apply[Component](
    commands: EventStream[CollectionCommand[Component]],
    renderableNode: RenderableNode[Component],
    initialSlotName: String | Unit
  ): DynamicInserter = {
    new DynamicInserter(
      insertFn = (ctx, owner) => {
        if (!ctx.lastInserterType.contains(InserterType.ChildrenCommandType)) {
          // Clear content left by a previous non-command inserter.
          // Commands build the context incrementally, so:
          //  - if we did previously build some content using commands, we want to keep it.
          //     - especially relevant for mere re-mounting, which will call this method too.
          //  - if we're given the context previously built by a different type of inserter,
          //    the command inserter is unable to patch it to the desired state,
          //    so clearing it is the only consistent strategy.
          ctx.clearPreviousInserterContent(
            replaceContentMapWithSingleNode = js.undefined,
            nextInserterType = InserterType.ChildrenCommandType
          )
        }
        commands.foreach { command =>
          updateList(command, ctx, renderableNode)
        }(using owner)
      },
      slotName = initialSlotName
    )
  }

  private def updateList[Component](
    command: CollectionCommand[Component],
    ctx: InsertContext,
    renderableNode: RenderableNode[Component]
  ): Unit = {
    command.map(renderableNode.asNode) match {

      case CollectionCommand.Append(node) =>
        // Insert at the end of our span, right before the trailing sentinel. No index math
        // or node count needed: the trailing sentinel marks the span's end, and a failed
        // insert simply leaves that boundary where it was (see Laminar issue #195).
        if (
          DomApi.insertChildBefore(
            parent = ctx.currentParentNode,
            newChild = node,
            referenceChildRef = ctx.trailingSentinelNodeOpt.get.ref,
            slotName = ctx.currentSlotName
          )
        ) {
          // We update `contentMap` conditionally here because unlike other
          // dynamic inserters, in ChildrenCommandInserter we don't rebuild
          // the map on every event, so any phantom records there would
          // persist for longer, possibly indefinitely.
          ctx.contentMap.set(node.ref, node)
        }

      case CollectionCommand.Prepend(node) =>
        if (
          DomApi.insertChildAfter(
            parent = ctx.currentParentNode,
            newChild = node,
            referenceChildRef = ctx.sentinelNode.ref,
            slotName = ctx.currentSlotName
          )
        ) {
          ctx.contentMap.set(node.ref, node)
        }

      case CollectionCommand.Insert(node, atIndex) =>
        // Walk from the start for positive indices, the end for negative; clamp at span boundaries.
        // O(min(abs(index), span size)), independent of other siblings.
        val leadingRef = ctx.sentinelNode.ref
        val trailingRef = ctx.trailingSentinelNodeOpt.get.ref
        // Count toward zero to handle Int.MinValue without overflow.
        var stepsLeft = atIndex
        val referenceRef: dom.Node = {
          if (atIndex >= 0) {
            var ref = leadingRef.nextSibling
            while (stepsLeft > 0 && ref != trailingRef) {
              ref = ref.nextSibling
              stepsLeft -= 1
            }
            ref
          } else {
            // Negative index counts from the end: -1 inserts before the last node
            var ref: dom.Node = trailingRef
            while (stepsLeft < 0 && ref.previousSibling != leadingRef) {
              ref = ref.previousSibling
              stepsLeft += 1
            }
            ref
          }
        }
        if (stepsLeft != 0) {
          val clampedTo = if (atIndex >= 0) "end" else "start"
          DomApi.maybeReportDomError(s"CollectionCommand.Insert(`${DomApi.debugNodeDescription(node.ref)}`, atIndex = ${atIndex}): index out of bounds by ${Math.abs(stepsLeft.toLong)}, clamped to the ${clampedTo} of the span.")
        }
        if (
          DomApi.insertChildBefore(
            parent = ctx.currentParentNode,
            newChild = node,
            referenceChildRef = referenceRef,
            slotName = ctx.currentSlotName
          )
        ) {
          ctx.contentMap.set(node.ref, node)
        }

      case CollectionCommand.Remove(node) =>
        DomApi.removeChild(
          parent = ctx.currentParentNode,
          child = node
        )
        // `removeChild` failed because the `node` isn't there.
        // So, it's safe to .delete from the map regardless of the reason.
        ctx.contentMap.delete(node.ref)

      case CollectionCommand.Replace(oldNode, newNode) =>
        if (
          DomApi.replaceChild(
            parent = ctx.currentParentNode,
            oldChild = oldNode,
            newChild = newNode,
            slotName = ctx.currentSlotName
          )
        ) {
          ctx.contentMap.delete(oldNode.ref)
          ctx.contentMap.set(newNode.ref, newNode)
        } else if (!oldNode.maybeParent.contains(ctx.currentParentNode)) {
          // Replace failed for some reason, AND the old node is not found anymore. So:
          //  - old node must have been stolen or externally moved, and
          //  - new node must have failed insertion
          // In this case, we need to remove oldNode from the contentMap.
          ctx.contentMap.delete(oldNode.ref)
        }

      case CollectionCommand.RemoveAll =>
        ctx.removeContentMapNodesFromDom(keepItem = InsertContext.keepNoItems)
        ctx.contentMap.clear()

      case CollectionCommand.ReplaceAll(newNodes, minimizeDiff) =>
        if (minimizeDiff) {
          replaceAllMinimizingDiff(newNodes, ctx)
        } else {
          replaceAllNaively(newNodes, ctx)
        }
    }
  }

  private def replaceAllNaively(
    newNodes: collection.immutable.Seq[ChildNode.Base],
    ctx: InsertContext
  ): Unit = {
    ctx.removeContentMapNodesFromDom(keepItem = InsertContext.keepNoItems)
    ctx.contentMap.clear()
    val trailingSentinelRef = ctx.trailingSentinelNodeOpt.get.ref
    newNodes.foreach { node =>
      if (
        DomApi.insertChildBefore(
          parent = ctx.currentParentNode,
          newChild = node,
          referenceChildRef = trailingSentinelRef,
          slotName = ctx.currentSlotName
        )
      ) {
        ctx.contentMap.set(node.ref, node)
      }
    }
  }

  /** Keep the nodes that are staying in the list, so that they aren't re-mounted.
    * The old nodes that are leaving are removed (and unmounted) before the new ones mount.
    */
  private def replaceAllMinimizingDiff(
    newNodes: collection.immutable.Seq[ChildNode.Base],
    ctx: InsertContext
  ): Unit = {
    val newNodeRefs = JsSet.empty[dom.Node]
    newNodes.foreach(node => newNodeRefs.add(node.ref))
    ctx.removeContentMapNodesFromDom(keepItem = newNodeRefs.has)
    ctx.contentMap.clear()
    // Place the nodes in order. Kept nodes that are already in the right place stay put.
    var afterRef: dom.Node = ctx.sentinelNode.ref
    newNodes.foreach { node =>
      val isPlaced = if (afterRef.nextSibling eq node.ref) {
        // Re-affirm the slot, just like inserting the node would, so that this reconcile
        // wins over a manual `slot` override, whether or not the node had to move
        // (consistent last-write-wins semantics).
        node.applySlot(ctx.currentParentNode, ctx.currentSlotName)
        true
      } else {
        DomApi.insertChildAfter(
          parent = ctx.currentParentNode,
          newChild = node,
          referenceChildRef = afterRef,
          slotName = ctx.currentSlotName
        )
      }
      if (isPlaced) {
        ctx.contentMap.set(node.ref, node)
        afterRef = node.ref
      }
    }
  }
}
