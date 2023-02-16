package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observable
import com.raquo.ew.JsMap
import com.raquo.laminar.lifecycle.InsertContext
import com.raquo.laminar.nodes.{ChildNode, ParentNode, ReactiveElement}
import org.scalajs.dom

import scala.collection.immutable
import scala.scalajs.js

object ChildrenInserter {

  @deprecated("`Child` type alias is deprecated. Use ChildNode.Base", "15.0.0-M6")
  type Child = ChildNode.Base

  @deprecated("`Children`type alias is deprecated. Use immutable.Seq[ChildNode.Base]", "15.0.0-M6")
  type Children = immutable.Seq[ChildNode.Base]

  def apply[Component](
    childrenSource: Observable[immutable.Seq[Component]],
    renderableNode: RenderableNode[Component]
  ): Inserter.Base = {
    new Inserter[ReactiveElement.Base](
      preferStrictMode = true,
      insertFn = (ctx, owner) => {
        if (!ctx.strictMode) {
          ctx.forceSetStrictMode()
        }

        var maybeLastSeenChildren: js.UndefOr[immutable.Seq[ChildNode.Base]] = ctx.extraNodes

        childrenSource.foreach { components =>
          // #TODO[Performance] This is not ideal – for CUSTOM renderable components asNodeSeq
          //  will need to map over the seq, creating a new seq of child nodes.
          //  Unfortunately, avoiding this is quite complicated.
          val newChildren = renderableNode.asNodeSeq(components)

          // #TODO[Performance] Consider bringing back this eq check. Benchmark performance cost.
          //  - Without it, we might get worse performance, as when the same list is emitted,
          //    Laminar needs to iterate over the list of elements and check their position in the DOM.
          //    These checks are desirable in rare cases when the list of elements is affected by other
          //    inserters, e.g. when another inserter has removed an item from the list, and later this
          //    inserter re-emits the same list trying to add the item back where it used to be.
          //  - TLDR – we're choosing correctness over performance for now, but both are only a small
          //    difference. Check that performance cost is not too bad with benchmarks.

          // if (!maybeLastSeenChildren.exists(_ eq newChildren)) { // #Note: auto-distinction
            // println(s">> ${$children}.foreach with newChildren = ${newChildren.map(_.ref).map(DomApi.debugNodeOuterHtml)}")
            maybeLastSeenChildren = newChildren
            val newChildrenMap = InsertContext.nodesToMap(newChildren)
            ctx.extraNodeCount = updateChildren(
              prevChildren = ctx.extraNodesMap,
              nextChildren = newChildren,
              nextChildrenMap = newChildrenMap,
              parentNode = ctx.parentNode,
              sentinelNode = ctx.sentinelNode,
              ctx.extraNodeCount
            )
            ctx.extraNodes = newChildren
            ctx.extraNodesMap = newChildrenMap
          // }
        }(owner)
      }
    )
  }

  /** @return New child node count */
  private def updateChildren(
    prevChildren: JsMap[dom.Node, ChildNode.Base],
    nextChildren: immutable.Seq[ChildNode.Base],
    nextChildrenMap: JsMap[dom.Node, ChildNode.Base],
    parentNode: ReactiveElement.Base,
    sentinelNode: ChildNode.Base,
    prevChildrenCount: Int
  ): Int = {
    val liveNodeList = parentNode.ref.childNodes
    val sentinelIndex = ParentNode.indexOfChild(parent = parentNode, sentinelNode)

    // Loop variables
    var index = 0
    var currentChildrenCount = prevChildrenCount
    var prevChildRef = liveNodeList(sentinelIndex + 1)

    // Sorry for all the debug comments, but they really help me figure things out.

    // println(">>>>>>>>>>>>>>>>>")
    // println(s"updateChildren(nextChildren = ${nextChildren.map(_.ref.textContent)})")

    nextChildren.foreach { nextChild => // #TODO Not sure if this is faster than iterating over a js.Map

      // Desired index of `nextChild` in `liveNodeList`
      val nextChildNodeIndex = sentinelIndex + index + 1

      // println("evaluating index=" + index + ", nextChildNodeIndex=" + nextChildNodeIndex + ", prevChildRef=" + (if ((prevChildRef: js.UndefOr[dom.Node]) == js.undefined || prevChildRef == null) "null or undefined" else prevChildRef.textContent))

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
        // println(s"setting prevChildRef=${nextChild.ref.textContent}")
        prevChildRef = nextChild.ref
        currentChildrenCount += 1
      } else {
        if (nextChild.ref == prevChildRef) {
          // println("NODE MATCHES – " + nextChild.ref.textContent)
          // Child nodes already match – do nothing, go to the next child
        } else {
          // println("NODE DOES NOT MATCH")
          // println("NODE DOES NOT MATCH – " + nextChild.ref.textContent + " vs " + prevChildRef.textContent)

          if (!prevChildren.has(nextChild.ref)) {
            // nextChild not found in prevChildren, so it's a new child, so we need to insert it
            // println("> new: inserting " + nextChild.ref.textContent + " at index " + nextChildNodeIndex)
            // @Note: DOM update
            ParentNode.insertChild(parent = parentNode, child = nextChild, atIndex = nextChildNodeIndex)
            // println(s"setting prevChildRef=${nextChild.ref.textContent}")
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
              // println(s"> prevChildRef == ${if (prevChildRef == null) "null!" else prevChildRef.textContent}")
              val nextPrevChildRef = prevChildRef.nextSibling //@TODO[Integrity] See warning in https://developer.mozilla.org/en-US/docs/Web/API/Node/nextSibling (should not affect us though)

              val prevChild = prevChildFromRef(prevChildren, prevChildRef)
              // println("> removing " + prevChild.ref.textContent)
              // @Note: DOM update
              ParentNode.removeChild(parent = parentNode, child = prevChild)
              // println(s"setting prevChildRef=${nextPrevChildRef.textContent}")
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
      // println(s">> prevChildRef == ${if (prevChildRef == null) "null!" else prevChildRef.textContent}")
      // println(s"setting prevChildRef=${if (prevChildRef.nextSibling == null) "null!" else prevChildRef.nextSibling.textContent}")
      if (prevChildRef.nextSibling == null) {
        // This case is unexpected. It can happen when elements are removed from the DOM manually,
        // or when they are moved from one `children <--` list to another via standard Laminar functionality.
        // See issue: https://github.com/raquo/Laminar/issues/120
        //
        // At this point in the code, what we know that:
        // - There are no more elements in the DOM – the `nextSibling` of the last element we looked at / inserted is `null`.
        // - There are no more `nextChildren` – we've just exhausted for foreach loop above
        // Conclusion:
        // - We thought there would be more elements in the DOM, but they were removed (presumably externally),
        //   and they are not found in `nextChildren`. So everything is right, but "for the wrong reasons", sort of.
        // What we need to do:
        // - Update `currentChildrenCount` to the accurate number, since it will be used on the next update.
        // - `prevChildren` map will be discarded after this method runs, so we do NOT need to update that
        currentChildrenCount = index + 1
      } else {
        prevChildRef = prevChildRef.nextSibling
      }
      index += 1 // Faster than zipWithIndex
    }

    // println("reached end of nextChildren")
    while (index < currentChildrenCount) {
      // We ran out of new items before we ran out of current items. Now deleting the remainder of current items.

      // println(s"index=${index}, currentChildrenCount=${currentChildrenCount}")
      // println(s">>> prevChildRef == ${if (prevChildRef == null) "null!" else prevChildRef.textContent}")
      val nextPrevChildRef = prevChildRef.nextSibling
      // Whenever we insert, move or remove items from the DOM, we need to manually update `prevChildRef` to point to the node at the current index
      // @Note: DOM update
      val prevChild = prevChildFromRef(prevChildren, prevChildRef)
      // println(s"> removing(2) ${prevChild.ref.textContent}")
      ParentNode.removeChild(parent = parentNode, child = prevChild)
      // println(s"setting(2) prevChildRef=${if (nextPrevChildRef == null) "null!" else nextPrevChildRef.textContent}")
      prevChildRef = nextPrevChildRef
      currentChildrenCount -= 1
    }

    currentChildrenCount
  }

  private def containsRef(nextChildrenMap: JsMap[dom.Node, ChildNode.Base], ref: dom.Node): Boolean = {
    nextChildrenMap.has(ref)
  }

  private def prevChildFromRef(prevChildren: JsMap[dom.Node, ChildNode.Base], ref: dom.Node): ChildNode.Base = {
    prevChildren.get(ref).getOrElse(throw new Exception(s"prevChildFromRef[children]: not found for ${ref}"))
  }

}
