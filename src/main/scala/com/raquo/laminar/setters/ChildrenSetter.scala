package com.raquo.laminar.setters

import com.raquo.airstream.core.Observable
import com.raquo.airstream.eventstream.EventStream
import com.raquo.airstream.signal.Signal
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.DomApi
import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveComment, ReactiveElement}
import com.raquo.laminar.setters.ChildrenSetter.Children
import org.scalajs.dom

import scala.collection.immutable

class ChildrenSetter(
  childrenObservable: Observable[Children]
) extends Modifier[ReactiveElement[dom.Element]] {

  import ChildrenSetter.{emptyChildren, updateChildren}

  override def apply(parentNode: ReactiveElement[dom.Element]): Unit = {
    var nodeCount = 0

    val sentinelNode = new ReactiveComment("")
    parentNode.appendChild(sentinelNode)(DomApi.treeApi)

    val childrenSignal = childrenObservable match {
      case stream: EventStream[Children @unchecked] => stream.toSignal(emptyChildren)
      case signal: Signal[Children @unchecked] => signal
    }

    val childrenDiffSignal = childrenSignal
      .fold[(Children, Children)](initial => (emptyChildren, initial))((diff, nextChildren) => (diff._2, nextChildren))

    parentNode.subscribe(childrenDiffSignal) { childrenDiff =>
      nodeCount = updateChildren(
        prevChildren = childrenDiff._1,
        nextChildren = childrenDiff._2,
        parentNode = parentNode,
        sentinelNode = sentinelNode,
        nodeCount
      )
    }
  }
}

object ChildrenSetter {

  private val emptyChildren = Vector()

  type Child = ReactiveChildNode[dom.Node]

  type Children = immutable.Seq[Child]

  /** @return New child node count */
  protected def updateChildren(
    prevChildren: Children,
    nextChildren: Children,
    parentNode: ReactiveElement[dom.Element],
    sentinelNode: Child,
    prevChildrenCount: Int
  ): Int = {
    val liveNodeList = parentNode.ref.childNodes
    val sentinelIndex = parentNode.indexOfChild(sentinelNode)

    // Loop variables
    var index = 0
    var currentChildrenCount = prevChildrenCount
    var prevChildRef = liveNodeList(sentinelIndex + 1)

    // @TODO Remove all the debug comments

    //    dom.console.log(">>>>>>>>>>>>>>>>>")
    //    dom.console.log(">>>>>>>>>>>>>>>>>")

    nextChildren.foreach { nextChild =>

      /** Desired index of `nextChild` in `liveNodeList` */
      val nextChildNodeIndex = sentinelIndex + index + 1

      //      dom.console.log("\nevaluating index=" + index + ", nextChildNodeIndex=" + nextChildNodeIndex + ", prevChildRef=" + (if (prevChildRef == js.undefined || prevChildRef == null) "null or undefined" else prevChildRef.textContent))

      // @TODO[Integrity] prevChildRef can be null or even undefined here if we reach the end, under certain circumstances. See what can be done...

      // @TODO[Performance] prevChildren are not needed here, it's fixable right now by just looking at parentNode's maybeChildren

      // @TODO[Performance] this diffing algo is decent, but can still be optimized in a few ways (but we need benchmarking & data for that)
      // @TODO[Performance] We could optimize this for specific `Seq` implementations. For example, foreach is faster than while() on a `List`

      // @Note: Whenever we insert, move or remove items from the DOM, we need to manually update `prevChildRef` to point to the node at the current index

      if (currentChildrenCount <= index) {
        // We ran through the whole prevChildren list already, we just need to append all remaining nextChild-s into the DOM
        // Note: `prevChildRef` is not valid in this branch
        // println("> overflow: inserting " + nextChild.ref.textContent + " at index " + nextChildNodeIndex)
        // @Note: DOM update
        parentNode.insertChild(nextChild, atIndex = nextChildNodeIndex)(DomApi.treeApi)
        prevChildRef = nextChild.ref
        currentChildrenCount += 1
      } else {
        if (nextChild.ref == prevChildRef) {
          //          dom.console.log("NODE MATCHES – " + nextChild.ref.textContent)
          // Child nodes already match – do nothing, go to the next child
        } else {
          //          dom.console.log("NODE DOES NOT MATCH – " + nextChild.ref.textContent + " vs " + prevChildRef.textContent)

          if (!prevChildren.contains(nextChild)) {
            // nextChild not found in prevChildren, so it's a new child, so we need to insert it
            // println("> new: inserting " + nextChild.ref.textContent + " at index " + nextChildNodeIndex)
            // @Note: DOM update
            parentNode.insertChild(nextChild, atIndex = nextChildNodeIndex)(DomApi.treeApi)
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
              // println("> removing " + prevChild.ref.textContent)
              // @Note: DOM update
              parentNode.removeChild(prevChild)(DomApi.treeApi)
              prevChildRef = nextPrevChildRef
              currentChildrenCount -= 1
            }
            if (nextChild.ref != prevChildRef) {
              // nextChild is still not in the right place, so let's move it to the correct index
              // println("> order: inserting " + nextChild.ref.textContent + " at index " + nextChildNodeIndex)
              // @Note: DOM update
              parentNode.insertChild(nextChild, atIndex = nextChildNodeIndex)(DomApi.treeApi)
              prevChildRef = nextChild.ref
              // This is a MOVE, so we DO NOT update currentDomChildrenCount here.
            }
          }
        }
      }
      prevChildRef = prevChildRef.nextSibling
      index += 1 // Faster than zipWithIndex
    }

    while (index < currentChildrenCount) {
      // We ran out of new items before we ran out of current items. Now deleting the remainder of current items.

      val nextPrevChildRef = prevChildRef.nextSibling
      // Whenever we insert, move or remove items from the DOM, we need to manually update `prevChildRef` to point to the node at the current index
      // @Note: DOM update
      parentNode.removeChild(prevChildFromRef(prevChildren, prevChildRef))(DomApi.treeApi)
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

  // @TODO[Performance] This method should not exist, I think. See how it's used, we should just have removeChildByRef method in Laminar or SDB.
  protected def prevChildFromRef(prevChildren: Children, ref: dom.Node): Child = {
    //    println("> prevChildFromDomNode")
    //    dom.console.log(prevChildren(0))
    //    dom.console.log(prevChildren(1))
    //    dom.console.log(ref, ref.textContent)
    prevChildren.find(_.ref == ref).get // @TODO[Integrity] Throw a more meaningful error (that would be unrecoverable inconsistent state)
  }
}
