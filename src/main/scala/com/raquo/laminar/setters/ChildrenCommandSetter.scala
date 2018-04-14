package com.raquo.laminar.setters

import com.raquo.airstream.eventstream.EventStream
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.DomApi
import com.raquo.laminar.collection.CollectionCommand
import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveComment, ReactiveElement}
import com.raquo.laminar.setters.ChildrenSetter.Child
import org.scalajs.dom

import scala.scalajs.js

class ChildrenCommandSetter(
  $diff: EventStream[ChildrenCommandSetter.ChildrenCommand]
) extends Modifier[ReactiveElement[dom.Element]] {

  import ChildrenCommandSetter.updateList

  override def apply(parentNode: ReactiveElement[dom.Element]): Unit = {
    var nodeCount = 0

    val sentinelNode = new ReactiveComment("")
    parentNode.appendChild(sentinelNode)(DomApi.treeApi)

    parentNode.subscribe(
      $diff,
      (diff: ChildrenCommandSetter.ChildrenCommand) => {
        val nodeCountDiff = updateList(
          diff,
          parentNode = parentNode,
          sentinelNode = sentinelNode,
          nodeCount
        )
        nodeCount += nodeCountDiff
      }
    )
  }
}

object ChildrenCommandSetter {

  type ChildrenCommand = CollectionCommand[Child]

  def updateList(
    command: ChildrenCommandSetter.ChildrenCommand,
    parentNode: ReactiveElement[dom.Element],
    sentinelNode: ReactiveChildNode[dom.Comment],
    nodeCount: Int
  ): Int = {
    var nodeCountDiff = 0
    command match {
      case CollectionCommand.Append(node) =>
        val sentinelIndex = parentNode.indexOfChild(sentinelNode)
        if (parentNode.insertChild(node, atIndex = sentinelIndex + nodeCount + 1)(DomApi.treeApi)) {
          nodeCountDiff = 1
        }
        nodeCountDiff = 1
      case CollectionCommand.Prepend(node) =>
        val sentinelIndex = parentNode.indexOfChild(sentinelNode)
        if (parentNode.insertChild(node, atIndex = sentinelIndex + 1)(DomApi.treeApi)) {
          nodeCountDiff = 1
        }
      case CollectionCommand.Insert(node, atIndex) =>
        val sentinelIndex = parentNode.indexOfChild(sentinelNode)
        if (parentNode.insertChild(node, atIndex = sentinelIndex + atIndex + 1)(DomApi.treeApi)) {
          nodeCountDiff = 1
        }
      case CollectionCommand.Remove(node) =>
        if (parentNode.removeChild(node)(DomApi.treeApi)) {
          nodeCountDiff = -1
        }
      case CollectionCommand.Move(node, toIndex) =>
        // @TODO same as Insert.
        // @TODO Should we also add a MoveToEnd method?
        val sentinelIndex = parentNode.indexOfChild(sentinelNode)
        if (parentNode.insertChild(node, atIndex = sentinelIndex + toIndex + 1)(DomApi.treeApi)) {
          nodeCountDiff = 1
        }
      case CollectionCommand.Replace(node, withNode) =>
        parentNode.replaceChild(oldChild = node, newChild = withNode)(DomApi.treeApi)
      case CollectionCommand.ReplaceAll(newNodes) =>
        val sentinelIndex = parentNode.indexOfChild(sentinelNode)
        if (nodeCount == 0) {
          var numInsertedNodes = 0
          newNodes.foreach { newChild =>
            if (parentNode.insertChild(newChild, atIndex = sentinelIndex + 1 + numInsertedNodes)(DomApi.treeApi)) {
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
          )(DomApi.treeApi)
          if (replaced) {
            nodeCountDiff = newNodes.size - oldNodeCount
          }
        }
    }
    nodeCountDiff
  }
}

