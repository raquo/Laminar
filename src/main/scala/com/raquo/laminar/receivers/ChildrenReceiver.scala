package com.raquo.laminar.receivers

import com.raquo.laminar.{GroupBoundaryNode, RNode, RNodeData}
import com.raquo.snabbdom.Modifier
import com.raquo.xstream.XStream

import scala.scalajs.js.JavaScriptException

class ChildrenReceiver(groupKey: String) {

  def <--($children: XStream[Iterable[RNode]]): Modifier[RNode, RNodeData] = {
    new Modifier[RNode, RNodeData] {
      override def applyTo(node: RNode): Unit = {
        val openingBound = new GroupBoundaryNode(groupKey, isOpening = true)
        val closingBound = new GroupBoundaryNode(groupKey, isOpening = false)
        node.addChild(openingBound)
        node.addChild(closingBound)

        def next(newChildren: Iterable[RNode], newNode: RNode): RNode = {
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

            // @TODO[Performance] This .toSeq is probably no good
            allChildren.splice(
              openingBoundIndex + 1,
              deleteCount,
              newChildren.toSeq: _*
            )
          }
          newNode
        }

        node.subscribe(
          $children,
          onNext = (newChildren: Iterable[RNode], activeNode: RNode) => {
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
