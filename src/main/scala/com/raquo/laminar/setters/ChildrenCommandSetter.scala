package com.raquo.laminar.setters

import com.raquo.airstream.eventstream.EventStream
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.CollectionCommand
import com.raquo.laminar.nodes.{ChildNode, CommentNode, ReactiveElement}
import com.raquo.laminar.setters.ChildrenCommandSetter.ChildrenCommand
import com.raquo.laminar.setters.ChildrenSetter.Child
import org.scalajs.dom

import scala.scalajs.js

class ChildrenCommandSetter(
  commandStream: EventStream[ChildrenCommand]
) extends Modifier[ReactiveElement.Base] {

  import ChildrenCommandSetter.updateList

  override def apply(parentNode: ReactiveElement.Base): Unit = {
    var nodeCount = 0

    val sentinelNode = new CommentNode("")
    parentNode.appendChild(sentinelNode)

    parentNode.subscribe(commandStream) { command =>
      val nodeCountDiff = updateList(
        command,
        parentNode = parentNode,
        sentinelNode = sentinelNode,
        nodeCount
      )
      nodeCount += nodeCountDiff
    }
  }
}

object ChildrenCommandSetter {

  type ChildrenCommand = CollectionCommand[Child]

  def updateList(
    command: ChildrenCommand,
    parentNode: ReactiveElement.Base,
    sentinelNode: ChildNode[dom.Comment],
    nodeCount: Int
  ): Int = {
    var nodeCountDiff = 0
    command match {
      case CollectionCommand.Append(node) =>
        val sentinelIndex = parentNode.indexOfChild(sentinelNode)
        if (parentNode.insertChild(node, atIndex = sentinelIndex + nodeCount + 1)) {
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
        if (nodeCount == 0) {
          var numInsertedNodes = 0
          newNodes.foreach { newChild =>
            if (parentNode.insertChild(newChild, atIndex = sentinelIndex + 1 + numInsertedNodes)) {
              numInsertedNodes += 1
            }
          }
          nodeCountDiff = numInsertedNodes
        } else {
          val oldNodeCount = nodeCount
          val replaced = parentNode.replaceChildren(
            fromIndex = sentinelIndex + 1,
            toIndex = sentinelIndex + nodeCount,
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

