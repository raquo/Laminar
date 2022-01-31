package com.raquo.laminar.modifiers

import com.raquo.airstream.core.{EventStream, Observable, Signal}
import com.raquo.ew.JsMap
import com.raquo.laminar.lifecycle.{InsertContext, MountContext}
import com.raquo.laminar.nodes.{ChildNode, ParentNode, ReactiveElement}
import org.scalajs.dom

import scala.collection.immutable
import scala.scalajs.js

object ChildrenInserter {

  private val emptyChildren = Vector()

  type Child = ChildNode.Base

  type Children = immutable.Seq[Child]

  def apply[El <: ReactiveElement.Base] (
    $children: MountContext[El] => Observable[Children]
  ): Inserter[El] = new Inserter[El](
    insertFn = (ctx, owner) => {
      val mountContext = new MountContext[El](thisNode = ctx.parentNode, owner)
      val childrenSignal = $children(mountContext) match {
        case stream: EventStream[Children @unchecked] => stream.toSignal(emptyChildren)
        case signal: Signal[Children @unchecked] => signal
        case _ => throw new Exception("Unknown kind of observable")
      }
      var lastSeenChildren: js.UndefOr[Children] = js.undefined
      childrenSignal.foreach { newChildren =>
        if (!lastSeenChildren.exists(_ eq newChildren)) { // #Note: auto-distinction
          lastSeenChildren = newChildren
          val newChildrenMap = InsertContext.nodesToMap(newChildren)
          ctx.extraNodeCount = updateChildren(
            prevChildren = ctx.extraNodesMap,
            nextChildren = newChildren,
            nextChildrenMap = newChildrenMap,
            parentNode = ctx.parentNode,
            sentinelNode = ctx.sentinelNode,
            ctx.extraNodeCount
          )
          ctx.extraNodesMap = newChildrenMap
        }
      }(owner)
    }
  )

  /** @return New child node count */
  private def updateChildren(
    prevChildren: JsMap[dom.Node, ChildNode.Base],
    nextChildren: Children,
    nextChildrenMap: JsMap[dom.Node, ChildNode.Base],
    parentNode: ReactiveElement.Base,
    sentinelNode: Child,
    prevChildrenCount: Int
  ): Int = {
    val liveNodeList = parentNode.ref.childNodes
    val sentinelIndex = ParentNode.indexOfChild(parent = parentNode, sentinelNode)

    // Loop variables
    var index = 0
    var currentChildrenCount = prevChildrenCount
    var prevChildRef = liveNodeList(sentinelIndex + 1)

    // @TODO Remove all the debug comments

    //    dom.console.log(">>>>>>>>>>>>>>>>>")
    //    dom.console.log(">>>>>>>>>>>>>>>>>")

    nextChildren.foreach { nextChild => // #TODO Not sure if this is faster than iterating over a js.Map

      // Desired index of `nextChild` in `liveNodeList`
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
        ParentNode.insertChild(parent = parentNode, child = nextChild, atIndex = nextChildNodeIndex)
        prevChildRef = nextChild.ref
        currentChildrenCount += 1
      } else {
        if (nextChild.ref == prevChildRef) {
          //          dom.console.log("NODE MATCHES – " + nextChild.ref.textContent)
          // Child nodes already match – do nothing, go to the next child
        } else {
          //          dom.console.log("NODE DOES NOT MATCH – " + nextChild.ref.textContent + " vs " + prevChildRef.textContent)

          if (!prevChildren.has(nextChild.ref)) {
            // nextChild not found in prevChildren, so it's a new child, so we need to insert it
            // println("> new: inserting " + nextChild.ref.textContent + " at index " + nextChildNodeIndex)
            // @Note: DOM update
            ParentNode.insertChild(parent = parentNode, child = nextChild, atIndex = nextChildNodeIndex)
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
              nextChild.ref != prevChildRef && !containsRef(nextChildrenMap, prevChildRef)
            ) {
              // prevChild should be deleted, so we remove it from the DOM, and try again with the next prevChild
              // but first we save its next sibling, which will become our next `prevChildRef`
              val nextPrevChildRef = prevChildRef.nextSibling //@TODO[Integrity] See warning in https://developer.mozilla.org/en-US/docs/Web/API/Node/nextSibling (should not affect us though)

              val prevChild = prevChildFromRef(prevChildren, prevChildRef)
              // println("> removing " + prevChild.ref.textContent)
              // @Note: DOM update
              ParentNode.removeChild(parent = parentNode, child = prevChild)
              prevChildRef = nextPrevChildRef
              currentChildrenCount -= 1
            }
            if (nextChild.ref != prevChildRef) {
              // nextChild is still not in the right place, so let's move it to the correct index
              // println("> order: inserting " + nextChild.ref.textContent + " at index " + nextChildNodeIndex)
              // @Note: DOM update
              ParentNode.insertChild(parent = parentNode, child = nextChild, atIndex = nextChildNodeIndex)
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
      ParentNode.removeChild(parent = parentNode, child = prevChildFromRef(prevChildren, prevChildRef))
      prevChildRef = nextPrevChildRef
      currentChildrenCount -= 1
    }

    currentChildrenCount
  }

  private def containsRef(nextChildrenMap: JsMap[dom.Node, ChildNode.Base], ref: dom.Node): Boolean = {
    nextChildrenMap.has(ref)
  }

  private def prevChildFromRef(prevChildren: JsMap[dom.Node, ChildNode.Base], ref: dom.Node): Child = {
    prevChildren.get(ref).get // @TODO[Integrity] Throw a meaningful error if not found (that would be unrecoverable inconsistent state)
  }

}
