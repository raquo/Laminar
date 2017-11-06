package com.raquo.laminar.setters

import com.raquo.laminar.implicits._
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveComment, ReactiveElement}
import com.raquo.laminar.setters.ChildrenSetter.Children
import com.raquo.xstream.XStream
import org.scalajs.dom

import scala.scalajs.js

class ChildrenSetter(
  $children: XStream[Children]
) extends Modifier[ReactiveElement[dom.Element]] {

  import ChildrenSetter.updateChildren

  override def apply(parentNode: ReactiveElement[dom.Element]): Unit = {
    var nodeCount = 0

    val sentinelNode = new ReactiveComment("")
    parentNode.appendChild(sentinelNode)

    val $childrenDiff = $children.slide2by1(initial = Vector())

    parentNode.subscribe(
      $childrenDiff,
      onNext = (childrenDiff: (Children, Children)) => {
        nodeCount = updateChildren(
          prevChildren = childrenDiff._1,
          nextChildren = childrenDiff._2,
          parentNode = parentNode,
          sentinelNode = sentinelNode,
          nodeCount
        )
      }
    )
  }
}

object ChildrenSetter {

  type Child = ReactiveChildNode[dom.Node]
  type Children = Seq[Child]

  /** @return New child node count */
  protected def updateChildren(
    prevChildren: Children,
    nextChildren: Children,
    parentNode: ReactiveElement[dom.Element],
    sentinelNode: ReactiveChildNode[dom.Node],
    prevChildrenCount: Int
  ): Int = {
    val liveNodeList = parentNode.ref.childNodes
    val sentinelIndex = parentNode.indexOfChild(sentinelNode)

    // Loop variables
    var index = 0
    var currentChildrenCount = prevChildrenCount
    var prevChildRef = liveNodeList(sentinelIndex + 1)

    // @TODO Remove all the debug comments

    dom.console.log(">>>>>>>>>>>>>>>>>")
    dom.console.log(">>>>>>>>>>>>>>>>>")

    nextChildren.foreach { nextChild =>

      /** Desired index of `nextChild` in `liveNodeList` */
      val nextChildNodeIndex = sentinelIndex + index + 1

      dom.console.log("\nevaluating index=" + index + ", nextChildNodeIndex=" + nextChildNodeIndex + ", prevChildRef=" + (if (prevChildRef == js.undefined || prevChildRef == null) "null or undefined" else prevChildRef.textContent))

      // @TODO[Integrity] prevChildRef can be null or even undefined here if we reach the end, under certain circumstances. See what can be done...

      // @TODO[Performance] prevChildren are not needed here, it's fixable right now by just looking at parentNode's maybeChildren

      // @TODO[Performance] this diffing algo is decent, but can still be optimized in a few ways (but we need benchmarking & data for that)
      // @TODO[Performance] We could optimize this for specific `Seq` implementations. For example, foreach is faster than while() on a `List`

      if (currentChildrenCount <= index) {
        // Note: `prevChildRef` is not valid in this branch
        dom.console.log("INSERTING " + nextChild.ref.textContent + " at index-" + index + " (nextChildNodeIndex=" + nextChildNodeIndex + ")")
        // We ran through the whole prevChildren list already, we just need to append all remaining nextChild-s into the DOM
        parentNode.insertChild(nextChild, atIndex = nextChildNodeIndex)
        // Whenever we insert, move or remove items from the DOM, we need to manually update `prevChildRef` to point to the node at the current index
        prevChildRef = nextChild.ref
        currentChildrenCount += 1
      } else {
        if (nextChild.ref == prevChildRef) {
          dom.console.log("NODE MATCHES – " + nextChild.ref.textContent)
          // Child nodes already match – do nothing, go to the next child
        } else {
          dom.console.log("NODE DOES NOT MATCH – " + nextChild.ref.textContent + " vs " + prevChildRef.textContent)

//          dom.console.log(parentNode.ref.childNodes(0), parentNode.ref.childNodes(0).textContent)
//          dom.console.log(parentNode.ref.childNodes(1), parentNode.ref.childNodes(1).textContent)
//          dom.console.log(parentNode.ref.childNodes(2), parentNode.ref.childNodes(2).textContent)
//          dom.console.log(parentNode.ref.childNodes(3), parentNode.ref.childNodes(3).textContent)
//          dom.console.log(parentNode.ref.childNodes(4), parentNode.ref.childNodes(4).textContent)

          if (!prevChildren.contains(nextChild)) {
            // nextChild not found in prevChildren, so it's a new child, so we need to insert it
            parentNode.insertChild(nextChild, atIndex = nextChildNodeIndex)
            prevChildRef = nextChild.ref
            currentChildrenCount += 1
          } else {
            // nextChild is found, but at a different index
            // First, let's check if prevChild should be deleted. This will reduce the amount of moving needed to be done in most cases.
            // Note:
            // - This loop should never go out of bounds on `liveNodeList` because we know that `nextChild.ref` is still in that list somewhere
            // - `nextChild.ref != prevChildNode` is a performance shortcut
            // - In `containsNode` call we only start looking at `index` because we know that all nodes before `index` are already in place.
            while (
              nextChild.ref != prevChildRef
                && !containsRef(nextChildren, prevChildRef, startLookingAtIndex = index)
            ) {
              // prevChild should be deleted, so we remove it from the DOM, and try again with the next prevChild
              // but first we save its next sibling, which will become our next `prevChildRef`
              val nextPrevChildRef = prevChildRef.nextSibling //@TODO[Integrity] See warning in https://developer.mozilla.org/en-US/docs/Web/API/Node/nextSibling (should not affect us though)

              val prevChild = prevChildFromRef(prevChildren, prevChildRef)
              parentNode.removeChild(prevChild)

              currentChildrenCount -= 1
              // Update prevChildNode reference
              prevChildRef = nextPrevChildRef
            }
            if (nextChild.ref != prevChildRef) {
              // nextChild is still not in the right place, so let's move it to the correct index
              parentNode.insertChild(nextChild, atIndex = nextChildNodeIndex) // MOVE, so we DO NOT update currentDomChildrenCount
              prevChildRef = nextChild.ref
            }
          }
        }
      }
      prevChildRef = prevChildRef.nextSibling
      index += 1 // Faster than zipWithIndex
    }

    while (index < currentChildrenCount) {

      dom.console.log("\nDELETING REMAINDER: index=" + index + ", currentChildrenCount = " + currentChildrenCount)
      dom.console.log("prevChildRef: " + prevChildRef.textContent)

      val nextPrevChildRef = prevChildRef.nextSibling
      parentNode.removeChild(prevChildFromRef(prevChildren, prevChildRef))
      prevChildRef = nextPrevChildRef
      currentChildrenCount -= 1
    }

    currentChildrenCount
  }

  protected def containsRef(nextChildren: Children, ref: dom.Node, startLookingAtIndex: Int): Boolean = {
    // @TODO[Performance] This also can be optimized for different `Seq` implementations
    val childrenCount = nextChildren.size
    var index = startLookingAtIndex
    var found = false
    while (!found && index < childrenCount) {
      if (nextChildren(index).ref == ref) {
        found = true
      } else {
        index += 1
      }
    }
    found
  }

  protected def prevChildFromRef(prevChildren: Children, ref: dom.Node): Child = {
    println("> prevChildFromDomNode")
//    dom.console.log(prevChildren(0))
//    dom.console.log(prevChildren(1))
    dom.console.log(ref, ref.textContent)
    prevChildren.find(_.ref == ref).get // @TODO[Integrity] Throw a more meaningful error (that would be unrecoverable inconsistent state)
  }
}
