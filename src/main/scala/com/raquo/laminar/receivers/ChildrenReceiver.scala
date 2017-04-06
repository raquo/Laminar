package com.raquo.laminar.receivers

import com.raquo.laminar.allTags.comment
import com.raquo.laminar.subscriptions.DynamicNodeList
import com.raquo.laminar.{RNode, RNodeData}
import com.raquo.snabbdom.Modifier
import com.raquo.xstream.XStream

import scala.scalajs.js
import scala.scalajs.js.JavaScriptException

object ChildrenReceiver {

  def <--($diff: XStream[DynamicNodeList.Diff]): Modifier[RNode, RNodeData] = {
    val nodeList = DynamicNodeList($diff)
    <--(nodeList)
  }

  def <--(nodeList: DynamicNodeList): Modifier[RNode, RNodeData] = {
    new Modifier[RNode, RNodeData] {
      override def applyTo(node: RNode): Unit = {
        node.addChild(makeSentinelNode(nodeList))
        node.subscribe(
          nodeList.$nodes,
          onNext = (newChildren: js.Array[RNode], activeNode: RNode) => {
            if (!activeNode.isMounted) {
              next(newChildren, activeNode, nodeList)
            } else {
              val newNode = activeNode.copy()
              next(newChildren, newNode, nodeList)
            }
          }
        )
      }
    }
  }

  private def next(
    newChildren: js.Array[RNode],
    newNode: RNode,
    currentNodeList: DynamicNodeList
  ): RNode = {
    // Children should not be empty because they at the very least contain the bounds nodes
    newNode.maybeChildren.foreach { allChildren =>
      // Kinda ugly, for the sake of performance
      var currentIndex = 0
      var firstOldChildIndex = -1
      var lastOldChildIndex = -1
      allChildren.foreach { child =>
        child.maybeNodeList.foreach { nodeList =>
          if (nodeList == currentNodeList) {
            if (firstOldChildIndex == -1) {
              firstOldChildIndex = currentIndex
            }
            lastOldChildIndex = currentIndex
          }
        }
        currentIndex += 1
      }

      // @TODO[Performance] Should these checks run in FullOptJS?
      if (firstOldChildIndex < 0) {
        throw JavaScriptException(s"ChildrenReceiver did not find previous dynamic children")
      }

      val deleteCount = lastOldChildIndex - firstOldChildIndex + 1
      val newChildrenOrSentinel = if (newChildren.length > 0) {
        newChildren
      } else {
        js.Array(makeSentinelNode(currentNodeList))
      }

      // @TODO[Performance] This _* is probably no good...
      allChildren.splice(
        firstOldChildIndex,
        deleteCount,
        newChildrenOrSentinel: _*
      )
    }
    newNode
  }

  /** Use a sentinel node to reserve a spot in the children array when there are no dynamic children */
  private def makeSentinelNode(nodeList: DynamicNodeList): RNode = {
    val node = comment()
    node.maybeNodeList = nodeList
    node
  }
}
