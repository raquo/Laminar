package com.raquo.laminar.receivers

import com.raquo.laminar.subscriptions.DynamicNodeList
import com.raquo.laminar.{GroupBoundaryNode, RNode, RNodeData}
import com.raquo.snabbdom.Modifier
import com.raquo.xstream.XStream

import scala.scalajs.js
import scala.scalajs.js.JavaScriptException

class ChildrenReceiver(groupKey: String) {

  def <--($diff: XStream[DynamicNodeList.Diff]): Modifier[RNode, RNodeData] = {
    val nodeList = DynamicNodeList($diff)
    <--(nodeList)
  }

  def <--(nodeList: DynamicNodeList): Modifier[RNode, RNodeData] = {
    new Modifier[RNode, RNodeData] {
      override def applyTo(node: RNode): Unit = {
        // @TODO[Performance] This opening/closing bounds is not needed at this point.
        // Now that we have a reference to DynamicNodeList in each node that is part of it
        // We could go through parent's children finding the index of the first and the last
        // node with this refeence, and then just splice the array like we do now. The benefit
        // of this would be that we won't need groupKey any more.
        val openingBound = new GroupBoundaryNode(groupKey, isOpening = true)
        val closingBound = new GroupBoundaryNode(groupKey, isOpening = false)
        node.addChild(openingBound)
        node.addChild(closingBound)

        def next(newChildren: js.Array[RNode], newNode: RNode): RNode = {
          // Children should not be empty because they at the very least contain the bounds nodes
          newNode.maybeChildren.foreach { allChildren =>
            // Kinda ugly, for the sake of performance
            var currentIndex = 0
            var openingBoundIndex = -1
            var closingBoundIndex = -1
            allChildren.foreach { child =>
              child match {
                case boundary: GroupBoundaryNode if boundary.groupKey == groupKey =>
                  if (boundary.isOpening) {
                    openingBoundIndex = currentIndex
                  } else {
                    closingBoundIndex = currentIndex
                  }
                case _ => ()
              }
              currentIndex += 1
            }

            // @TODO[Performance] Should these checks run in FullOptJS?
            if (openingBoundIndex < 0) {
              throw JavaScriptException(s"ChildrenReceiver($groupKey): opening bound not found")
            }
            if (closingBoundIndex < 0) {
              throw JavaScriptException(s"ChildrenReceiver($groupKey): closing bound not found")
            }
            if (closingBoundIndex > allChildren.length) {
              throw JavaScriptException(s"ChildrenReceiver($groupKey): closing bound index out of range")
            }

            val deleteCount = closingBoundIndex - openingBoundIndex - 1
            if (deleteCount < 0) {
              throw JavaScriptException("ChildrenReceiver: deleteCount can not be less than zero")
            }

            // @TODO add a console warning about missing key?

            // @TODO prepend key with groupKey?

            // @TODO[Performance] This _* is probably no good...
            allChildren.splice(
              openingBoundIndex + 1,
              deleteCount,
              newChildren: _*
            )
          }
          newNode
        }

        node.subscribe(
          nodeList.$nodes,
          onNext = (newChildren: js.Array[RNode], activeNode: RNode) => {
            if (!activeNode.isMounted) {
              next(newChildren, activeNode)
            } else {
              val newNode = activeNode.copy()
              next(newChildren, newNode)
            }
          }
        )
      }
    }
  }
}
