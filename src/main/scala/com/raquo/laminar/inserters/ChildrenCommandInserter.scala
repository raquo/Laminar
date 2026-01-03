package com.raquo.laminar.inserters

import com.raquo.airstream.core.EventStream
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.RenderableNode
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement}

import scala.scalajs.js
import scala.scalajs.js.|

/** Note: this is a low level inserter. It is the fastest one in certain cases,
  * but due to its rather imperative API, its usefulness is very limited.
  *
  * It's good for simple operations on voluminous data, like prepending new
  * log items to a big list, but not much else.
  *
  * Consider using `children <-- observable.split(...)` instead, it has
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
        ctx.ensureStrictMode()
        commands.foreach { command =>
          val nodeCountDiff = updateList(command, ctx, renderableNode, hooks)
          ctx.extraNodeCount += nodeCountDiff
        }(owner)
      },
      hooks = initialHooks
    )
  }

  def updateList[Component](
    command: CollectionCommand[Component],
    ctx: InsertContext,
    renderableNode: RenderableNode[Component],
    hooks: InserterHooks | Unit
  ): Int = {
    ctx.ensureStrictMode()

    var nodeCountDiff = 0
    def findSentinelIndex(): Int = DomApi.raw.indexOfChild(
      parent = ctx.parentNode.ref,
      child = ctx.sentinelNode.ref
    )

    command match {

      case CollectionCommand.Append(node) =>
        val inserted = DomApi.insertChildAtIndex(
          parent = ctx.parentNode,
          child = renderableNode.asNode(node),
          index = findSentinelIndex() + ctx.extraNodeCount + 1,
          hooks
        )
        if (inserted) {
          nodeCountDiff = 1
        }
        nodeCountDiff = 1

      case CollectionCommand.Prepend(node) =>
        val inserted = DomApi.insertChildAfter(
          parent = ctx.parentNode,
          newChild = renderableNode.asNode(node),
          referenceChild = ctx.sentinelNode,
          hooks
        )
        if (inserted) {
          nodeCountDiff = 1
        }

      case CollectionCommand.Insert(node, atIndex) =>
        val inserted = DomApi.insertChildAtIndex(
          parent = ctx.parentNode,
          child = renderableNode.asNode(node),
          index = findSentinelIndex() + atIndex + 1,
          hooks
        )
        if (inserted) {
          nodeCountDiff = 1
        }

      case CollectionCommand.Remove(node) =>
        val removed = DomApi.removeChild(
          parent = ctx.parentNode,
          child = renderableNode.asNode(node)
        )
        if (removed) {
          nodeCountDiff = -1
        }

      case CollectionCommand.Replace(node, withNode) =>
        DomApi.replaceChild(
          parent = ctx.parentNode,
          oldChild = renderableNode.asNode(node),
          newChild = renderableNode.asNode(withNode),
          hooks
        )
    }

    nodeCountDiff
  }
}
