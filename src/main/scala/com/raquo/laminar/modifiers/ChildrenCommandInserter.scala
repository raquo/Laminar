package com.raquo.laminar.modifiers

import com.raquo.airstream.eventstream.EventStream
import com.raquo.laminar.CollectionCommand
import com.raquo.laminar.lifecycle.{InsertContext, MountContext}
import com.raquo.laminar.modifiers.ChildrenInserter.Child
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement}

import scala.scalajs.js

object ChildrenCommandInserter {

  type ChildrenCommand = CollectionCommand[Child]

  def apply[El <: ReactiveElement.Base] (
    $command: MountContext[El] => EventStream[ChildrenCommand],
    initialInsertContext: Option[InsertContext[El]]
  ): Inserter[El] = new Inserter[El](
    initialInsertContext,
    insertFn = (c, owner) => {
      val mountContext = new MountContext[El](thisNode = c.parentNode, owner)

      $command(mountContext).foreach { command =>
        // @Note we never update c.extraNodes. This is ok because we also never read it.
        val nodeCountDiff = updateList(
          command,
          parentNode = c.parentNode,
          sentinelNode = c.sentinelNode,
          c.extraNodeCount
        )
        c.extraNodeCount += nodeCountDiff
      }(owner)
    }
  )

  def updateList(
    command: ChildrenCommand,
    parentNode: ReactiveElement.Base,
    sentinelNode: ChildNode.Base,
    extraNodeCount: Int
  ): Int = {
    var nodeCountDiff = 0
    command match {
      case CollectionCommand.Append(node) =>
        val sentinelIndex = parentNode.indexOfChild(sentinelNode)
        if (parentNode.insertChild(node, atIndex = sentinelIndex + extraNodeCount + 1)) {
          nodeCountDiff = 1
        }
        nodeCountDiff = 1
      case CollectionCommand.Prepend(node) =>
        val sentinelIndex = parentNode.indexOfChild(sentinelNode)
        if (parentNode.insertChild(node, atIndex = sentinelIndex + 1)) {
          nodeCountDiff = 1
        }
      case CollectionCommand.Insert(node, atIndex) =>
        val sentinelIndex = parentNode.indexOfChild(sentinelNode)
        if (parentNode.insertChild(node, atIndex = sentinelIndex + atIndex + 1)) {
          nodeCountDiff = 1
        }
      case CollectionCommand.Remove(node) =>
        if (parentNode.removeChild(node)) {
          nodeCountDiff = -1
        }
      case CollectionCommand.Move(node, toIndex) =>
        // @TODO same as Insert.
        // @TODO Should we also add a MoveToEnd method?
        val sentinelIndex = parentNode.indexOfChild(sentinelNode)
        if (parentNode.insertChild(node, atIndex = sentinelIndex + toIndex + 1)) {
          nodeCountDiff = 1
        }
      case CollectionCommand.Replace(node, withNode) =>
        parentNode.replaceChild(oldChild = node, newChild = withNode)
      case CollectionCommand.ReplaceAll(newNodes) =>
        val sentinelIndex = parentNode.indexOfChild(sentinelNode)
        if (extraNodeCount == 0) {
          var numInsertedNodes = 0
          newNodes.foreach { newChild =>
            if (parentNode.insertChild(newChild, atIndex = sentinelIndex + 1 + numInsertedNodes)) {
              numInsertedNodes += 1
            }
          }
          nodeCountDiff = numInsertedNodes
        } else {
          val oldNodeCount = extraNodeCount
          val replaced = parentNode.replaceChildren(
            fromIndex = sentinelIndex + 1,
            toIndex = sentinelIndex + extraNodeCount,
            js.Array(newNodes.toSeq: _*) // @TODO[Performance]
          )
          if (replaced) {
            nodeCountDiff = newNodes.size - oldNodeCount
          }
        }
    }
    nodeCountDiff
  }
}

