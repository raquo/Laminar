package com.raquo.laminar.modifiers

import com.raquo.airstream.core.{EventStream, Transaction}
import com.raquo.laminar.CollectionCommand
import com.raquo.laminar.modifiers.ChildrenInserter.Child
import com.raquo.laminar.nodes.{ChildNode, ParentNode, ReactiveElement}

/**
 * Note: this is a low level inserter. It is the fastest one, but due to
 * its rather imperative API, its usefulness is very limited. It's good for
 * simple but voluminous stuff, like appending new log items to a big list,
 * but not much else.
 *
 * Consider using `children <-- observable.split(...)` instead, it has
 * great performance and is much more convenient.
 */
object ChildrenCommandInserter {

  type ChildrenCommand = CollectionCommand[Child]

  def apply[El <: ReactiveElement.Base] (
    $command: EventStream[ChildrenCommand]
  ): Inserter[El] = {
    new Inserter[El](
      preferStrictMode = true,
      insertFn = (ctx, owner) => {
        $command.foreach { command =>
          val nodeCountDiff = updateList(
            command,
            parentNode = ctx.parentNode,
            sentinelNode = ctx.sentinelNode,
            ctx.extraNodeCount
          )
          ctx.extraNodeCount += nodeCountDiff
        }(owner)
      }
    )
  }

  def updateList(
    command: ChildrenCommand,
    parentNode: ReactiveElement.Base,
    sentinelNode: ChildNode.Base,
    extraNodeCount: Int
  ): Int = {
    var nodeCountDiff = 0
    command match {

      case CollectionCommand.Append(node) =>
        val sentinelIndex = ParentNode.indexOfChild(parent = parentNode, sentinelNode)
        if (ParentNode.insertChild(parent = parentNode, child = node, atIndex = sentinelIndex + extraNodeCount + 1)) {
          nodeCountDiff = 1
        }
        nodeCountDiff = 1

      case CollectionCommand.Prepend(node) =>
        val sentinelIndex = ParentNode.indexOfChild(parent = parentNode, sentinelNode)
        if (ParentNode.insertChild(parent = parentNode, child = node, atIndex = sentinelIndex + 1)) {
          nodeCountDiff = 1
        }

      case CollectionCommand.Insert(node, atIndex) =>
        val sentinelIndex = ParentNode.indexOfChild(parent = parentNode, sentinelNode)
        if (ParentNode.insertChild(parent = parentNode, child = node, atIndex = sentinelIndex + atIndex + 1)) {
          nodeCountDiff = 1
        }

      case CollectionCommand.Remove(node) =>
        if (ParentNode.removeChild(parent = parentNode, child = node)) {
          nodeCountDiff = -1
        }

      case CollectionCommand.Move(node, toIndex) =>
        // @TODO same as Insert.
        // @TODO Should we also add a MoveToEnd method?
        val sentinelIndex = ParentNode.indexOfChild(parent = parentNode, sentinelNode)
        if (ParentNode.insertChild(parent = parentNode, child = node, atIndex = sentinelIndex + toIndex + 1)) {
          nodeCountDiff = 1
        }

      case CollectionCommand.Replace(node, withNode) =>
        ParentNode.replaceChild(parent = parentNode, oldChild = node, newChild = withNode)

      case CollectionCommand.ReplaceAll(newNodes) =>
        Transaction.onStart.shared {
          val sentinelIndex = ParentNode.indexOfChild(parent = parentNode, sentinelNode)
          if (extraNodeCount == 0) {
            var numInsertedNodes = 0
            newNodes.foreach { newChild =>
              if (ParentNode.insertChild(parent = parentNode, child = newChild, atIndex = sentinelIndex + 1 + numInsertedNodes)) {
                numInsertedNodes += 1
              }
            }
            nodeCountDiff = numInsertedNodes
          } else {
            val oldNodeCount = extraNodeCount
            val replaced = ParentNode.replaceChildren(
              parent = parentNode,
              fromIndex = sentinelIndex + 1,
              toIndex = sentinelIndex + extraNodeCount,
              newNodes
            )
            if (replaced) {
              nodeCountDiff = newNodes.size - oldNodeCount
            }
          }
        }
    }
    nodeCountDiff
  }
}

