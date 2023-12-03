package com.raquo.laminar.modifiers

import com.raquo.airstream.core.EventStream
import com.raquo.laminar.nodes.{ChildNode, ParentNode, ReactiveElement}
import com.raquo.laminar.{CollectionCommand, DomApi}

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

  def apply[Component, El <: ReactiveElement.Base] (
    commands: EventStream[CollectionCommand[Component]],
    renderableNode: RenderableNode[Component]
  ): DynamicInserter[El] = {
    new DynamicInserter[El](
      preferStrictMode = true,
      insertFn = (ctx, owner) => {
        commands.foreach { command =>
          val nodeCountDiff = updateList(
            command,
            parentNode = ctx.parentNode,
            sentinelNode = ctx.sentinelNode,
            ctx.extraNodeCount,
            renderableNode
          )
          ctx.extraNodeCount += nodeCountDiff
        }(owner)
      }
    )
  }

  def updateList[Component](
    command: CollectionCommand[Component],
    parentNode: ReactiveElement.Base,
    sentinelNode: ChildNode.Base,
    extraNodeCount: Int,
    renderableNode: RenderableNode[Component]
  ): Int = {
    var nodeCountDiff = 0
    def findSentinelIndex(): Int = DomApi.indexOfChild(
      parent = parentNode.ref,
      child = sentinelNode.ref
    )

    command match {

      case CollectionCommand.Append(node) =>
        val inserted = ParentNode.insertChildAtIndex(
          parent = parentNode,
          child = renderableNode.asNode(node),
          index = findSentinelIndex() + extraNodeCount + 1)
        if (inserted) {
          nodeCountDiff = 1
        }
        nodeCountDiff = 1

      case CollectionCommand.Prepend(node) =>
        val inserted = ParentNode.insertChildAfter(
          parent = parentNode,
          newChild = renderableNode.asNode(node),
          referenceChild = sentinelNode
        )
        if (inserted) {
          nodeCountDiff = 1
        }

      case CollectionCommand.Insert(node, atIndex) =>
        val inserted = ParentNode.insertChildAtIndex(
          parent = parentNode,
          child = renderableNode.asNode(node),
          index = findSentinelIndex() + atIndex + 1
        )
        if (inserted) {
          nodeCountDiff = 1
        }

      case CollectionCommand.Remove(node) =>
        val removed = ParentNode.removeChild(
          parent = parentNode,
          child = renderableNode.asNode(node)
        )
        if (removed) {
          nodeCountDiff = -1
        }

      case CollectionCommand.Replace(node, withNode) =>
        ParentNode.replaceChild(
          parent = parentNode,
          oldChild = renderableNode.asNode(node),
          newChild = renderableNode.asNode(withNode)
        )
    }

    nodeCountDiff
  }
}

