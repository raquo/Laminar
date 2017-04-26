package com.raquo.laminar.receivers

import com.raquo.dombuilder.modifiers.Modifier
import com.raquo.laminar
import com.raquo.laminar.ChildNode
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.xstream.XStream
import com.raquo.laminar.subscriptions.ListDiff

import scala.collection.mutable
import scala.scalajs.js

class ChildrenReceiver(
  $diff: XStream[ChildrenReceiver.Diff]
) extends Modifier[ReactiveElement] {

  override def applyTo(parentNode: ReactiveElement): Unit = {
    var nodeCount = 0

    val sentinelNode = laminar.commentBuilder.createNode()
    parentNode.appendChild(sentinelNode)

    parentNode.subscribe(
      $diff,
      onNext = (diff: ChildrenReceiver.Diff) => {
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

  def updateList(
    diff: ChildrenReceiver.Diff,
    parentNode: ReactiveElement,
    sentinelNode: ChildNode,
    nodeCount: Int
  ): Int = {
    var nodeCountDiff = 0
    diff match {
      case ListDiff.Append(node) =>
        val sentinelIndex = parentNode.indexOfChild(sentinelNode)
        if (parentNode.insertChild(node, atIndex = sentinelIndex + nodeCount + 1)) {
          nodeCountDiff = 1
        }
        nodeCountDiff = 1
      case ListDiff.Prepend(node) =>
        val sentinelIndex = parentNode.indexOfChild(sentinelNode)
        if (parentNode.insertChild(node, atIndex = sentinelIndex + 1)) {
          nodeCountDiff = 1
        }
      case ListDiff.Insert(node, atIndex) =>
        val sentinelIndex = parentNode.indexOfChild(sentinelNode)
        if (parentNode.insertChild(node, sentinelIndex + atIndex + 1)) {
          nodeCountDiff = 1
        }
      case ListDiff.Remove(node) =>
        if (parentNode.removeChild(node)) {
          nodeCountDiff = -1
        }
      case ListDiff.Replace(node, withNode) =>
        parentNode.replaceChild(oldChild = node, newChild = withNode)
      case ListDiff.ReplaceAll(newNodes) =>
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
            js.Array(newNodes: _*)
          )
          if (replaced) {
            nodeCountDiff = newNodes.length - oldNodeCount
          }
        }
    }
    nodeCountDiff
  }
}

object ChildrenReceiver {

  type Diff = ListDiff[ChildNode, mutable.Buffer]

  def <--($diff: XStream[Diff]): ChildrenReceiver = {
    new ChildrenReceiver($diff)
  }

  // These helpers are solely there to help type inference

  @inline def append(node: ChildNode): ListDiff.Append[ChildNode, mutable.Buffer] = {
    ListDiff.Append(node)
  }

  @inline def prepend(node: ChildNode): ListDiff.Prepend[ChildNode, mutable.Buffer] = {
    ListDiff.Prepend(node)
  }

  @inline def insert(node: ChildNode, atIndex: Int): ListDiff.Insert[ChildNode, mutable.Buffer] = {
    ListDiff.Insert(node, atIndex)
  }

  @inline def remove(node: ChildNode): ListDiff.Remove[ChildNode, mutable.Buffer] = {
    ListDiff.Remove(node)
  }

  @inline def replace(node: ChildNode, withNode: ChildNode): ListDiff.Replace[ChildNode, mutable.Buffer] = {
    ListDiff.Replace(item = node, withItem = withNode)
  }

  // @TODO[API] we really shouldn't be expecting a mutable buffer here.
  @inline def replaceAll(newNodes: mutable.Buffer[ChildNode]): ListDiff.ReplaceAll[ChildNode, mutable.Buffer] = {
    ListDiff.ReplaceAll(newNodes)
  }
}
