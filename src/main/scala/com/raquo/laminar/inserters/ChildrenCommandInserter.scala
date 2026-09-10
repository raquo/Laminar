package com.raquo.laminar.inserters

import com.raquo.airstream.core.EventStream
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.RenderableNode
import com.raquo.laminar.nodes.{ChildNode, CommentNode}

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
    initialHooks: js.UndefOr[InserterHooks]
  ): DynamicInserter = {
    new DynamicInserter(
      insertFn = (ctx, owner, hooks) => {
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
          updateList(command, ctx, renderableNode, hooks)
        }(using owner)
      },
      hooks = initialHooks
    )
  }

  private def updateList[Component](
    command: CollectionCommand[Component],
    ctx: InsertContext,
    renderableNode: RenderableNode[Component],
    hooks: InserterHooks | Unit
  ): Unit = {
    def findSentinelIndex(): Int = {
      DomApi.raw.indexOfChild(
        parent = ctx.currentParentNode.ref,
        child = ctx.sentinelNode.ref
      )
    }

    command.map(renderableNode.asNode) match {

      case CollectionCommand.Append(node) =>
        // Insert at the end of our span, right before the trailing sentinel. No index math
        // or node count needed: the trailing sentinel marks the span's end, and a failed
        // insert simply leaves that boundary where it was (see Laminar issue #195).
        DomApi.insertChildBefore(
          parent = ctx.currentParentNode,
          newChild = node,
          referenceChildRef = ctx.trailingSentinelNodeOpt.get.ref,
          hooks
        )
        ctx.contentMap.set(node.ref, node)

      case CollectionCommand.Prepend(node) =>
        DomApi.insertChildAfter(
          parent = ctx.currentParentNode,
          newChild = node,
          referenceChildRef = ctx.sentinelNode.ref,
          hooks
        )
        ctx.contentMap.set(node.ref, node)

      case CollectionCommand.Insert(node, atIndex) =>
        DomApi.insertChildAtIndex(
          parent = ctx.currentParentNode,
          child = node,
          index = findSentinelIndex() + atIndex + 1,
          hooks
        )
        ctx.contentMap.set(node.ref, node)

      case CollectionCommand.Remove(node) =>
        DomApi.removeChild(
          parent = ctx.currentParentNode,
          child = node
        )
        ctx.contentMap.delete(node.ref)

      case CollectionCommand.Replace(oldNode, newNode) =>
        DomApi.replaceChild(
          parent = ctx.currentParentNode,
          oldChild = oldNode,
          newChild = newNode,
          hooks
        )
        ctx.contentMap.delete(oldNode.ref)
        ctx.contentMap.set(newNode.ref, newNode)

      case CollectionCommand.RemoveAll =>
        ctx.removeContentMapNodesFromDom(keepNodeIfPresent = js.undefined)
        ctx.contentMap.clear()

      case CollectionCommand.ReplaceAll(newNodes) =>
        ctx.removeContentMapNodesFromDom(keepNodeIfPresent = js.undefined)
        ctx.contentMap.clear()
        val trailingSentinelRef = ctx.trailingSentinelNodeOpt.get.ref
        newNodes.foreach { node =>
          DomApi.insertChildBefore(
            parent = ctx.currentParentNode,
            newChild = node,
            referenceChildRef = trailingSentinelRef,
            hooks
          )
          ctx.contentMap.set(node.ref, node)
        }
    }
  }
}
