package com.raquo.laminar.lifecycle

import com.raquo.ew.JsMap
import com.raquo.laminar.DomApi
import com.raquo.laminar.nodes.{ChildNode, CommentNode, ParentNode, ReactiveElement}
import org.scalajs.dom

import scala.collection.immutable

// #TODO[Naming] This feels more like InserterState?
//  "Extra nodes" are more like "content nodes"

// @Note only parentNode and sentinelNode are used by all Inserter-s.
//  - Other fields may remain un-updated if they are not needed for a particular use case.

/**
 * InsertContext represents the state of the DOM inside an inserter block like `child <-- ...`,
 * `children <-- ...`, `child.text <-- ...`, etc. The data stored in this context is used
 * by Laminar to efficiently update the DOM, to detect (and recover from) external changes
 * to the DOM, and for other related tasks.
 *
 * InsertContext is a mutable data structure that is created once for each inserter, and the
 * inserter updates it as it processes new data. However, in case of `onMountInsert`, only
 * one context is created, and is then reused for all inserters created inside
 * `onMountInsert`. This allows for intuitive preservation of DOM state if the element is
 * unmounted and mounted again (for example, `onMountInsert(child <-- stream)` will
 * keep the last emitted child in the DOM even if the element is unmounted and re-mounted).
 *
 * #Note: The params that describe `extraNodes` below can get out of sync with the real DOM.
 *
 * This can happen if an child element is removed from the DOM – either externally, or more
 * likely because it was moved from this inserter into another one, and the addition to the
 * other inserter was processed before the removal from this inserter is processed (the
 * order of these operations depends on the propagation order of the observables feeding
 * these two inserters). The Inserter code must account for this and not fail in such cases,
 * and must correct the values accordingly on the next observable update.
 *
 * #Note: The params that describe `extraNodes` below must be kept consistent manually (#Perf)
 *
 *                              Inserter "steals" an element from this one just before the
 *                              observable source of this inserter provides a new list of
 *                              children (with the stolen element removed from the list).
 *
 * @param sentinelNode        - A special invisible comment node that tells Laminar where to
 *                              insert the dynamic children, and where to expect previously
 *                              inserted dynamic children.
 * @param strictMode          - If true, Laminar guarantees that it will keep a dedicated
 *                              sentinel node instead of using the extra node (content node)
 *                              for that purpose. This is needed in order to allow users to
 *                              move an element from one inserter to another, or to externally
 *                              remove some of the elements previously added by an inserter.
 *                              child.text does not need any of that, so for performance it
 *                              does not use strict mode, it replaces the sentinel comment
 *                              node with the subsequent text nodes. Inserters should be able
 *                              to safely switch to their preferred mode when receiving
 *                              context left by the previous inserter in onMountBind.
 * @param extraNodeCount      - Number of child nodes in addition to the sentinel node.
 *                              Warning: can get out of sync with the real DOM!
 * @param extraNodes          - Ordered list of child nodes in addition to the sentinel node.
 *                              Warning: can get out of sync with the real DOM!
 * @param extraNodesMap       - Map of child nodes, for more efficient search
 *                              Warning: can get out of sync with the real DOM!
 */
final class InsertContext[+El <: ReactiveElement.Base](
  val parentNode: El,
  var sentinelNode: ChildNode.Base,
  var strictMode: Boolean,
  var extraNodeCount: Int, // This is separate from `extraNodesMap` for performance #TODO[Performance]: Check if this is still relevant with JsMap
  var extraNodes: immutable.Seq[ChildNode.Base],
  var extraNodesMap: JsMap[dom.Node, ChildNode.Base]
) {

  /**
   * This method converts the InsertContext from loose mode to strict mode.
   * ChildrenInserter and ChildInserter call this when receiving a context from
   * ChildTextInserter. This can happen when switching from `child.text <-- ...`
   * to e.g. `children <-- ...` inside onMountInsert.
   *
   * Prerequisite: context must be in loose mode, and in valid state: no extra nodes allowed.
   */
  def forceSetStrictMode(): Unit = {
    if (strictMode || extraNodeCount != 0) {
      // #Note: if extraNodeCount == 0, it is also assumed (but not tested) that extraNodes and extraNodesMap are empty.
      throw new Exception(s"forceSetStrictMode invoked when not allowed, inside parent = ${DomApi.debugNodeOuterHtml(parentNode.ref)}")
    }
    if (extraNodesMap == null) {
      // In loose mode, extraNodesMap is likely to be null, so we need to initialize it.
      extraNodesMap = new JsMap()
    }
    if (sentinelNode.ref.isInstanceOf[dom.Comment]) {
      // This means there are no content nodes.
      // We assume that all extraNode fields are properly zeroed, so there is nothing to do.
    } else {
      // In loose mode, child content nodes are written to sentinelNode field,
      // so there are no extraNodes.
      // So, if we find a content node in sentinelNode, we need to reclassify
      // it as such for the strict mode, and insert a new sentinel node into the DOM.
      val contentNode = sentinelNode
      val newSentinelNode = new CommentNode("")
      ParentNode.insertChild(
        parent = parentNode,
        child = newSentinelNode,
        atIndex = ParentNode.indexOfChild(
          parent = parentNode,
          child = contentNode
        )
      )

      // Convert loose mode context values to strict mode context values
      sentinelNode = newSentinelNode
      extraNodeCount = 1
      extraNodes = contentNode :: Nil
      extraNodesMap.set(contentNode.ref, contentNode) // we initialized the map above
    }
    strictMode = true
  }

  /** #Note: this does NOT update the context to match the DOM. */
  def removeOldChildNodesFromDOM(after: ChildNode.Base): Unit = {
    var remainingOldExtraNodeCount = extraNodeCount
    while (remainingOldExtraNodeCount > 0) {
      val prevChildRef = after.ref.nextSibling
      if (prevChildRef == null) {
        // We expected more previous children to be in the DOM, but we reached the end of the DOM.
        // Those children must have been removed from the DOM manually, or moved to a different inserter.
        // So, the DOM state is now correct, albeit "for the wrong reasons". All is good. End the loop.
        remainingOldExtraNodeCount = 0
      } else {
        val maybePrevChild = extraNodesMap.get(prevChildRef)
        if (maybePrevChild.isEmpty) {
          // Similar to the prevChildRef == null case above, we've exhausted the DOM,
          // except we stumbled on some unrelated element instead. We only allow external
          // removals from the DOM, not external additions in the middle of dynamic children list,
          // so this unrelated element is good evidence that there are no more old child nodes
          // to be found.
          remainingOldExtraNodeCount = 0
        } else {
          maybePrevChild.foreach { prevChild =>
            // @Note: DOM update
            ParentNode.removeChild(parent = parentNode, child = prevChild)
            remainingOldExtraNodeCount -= 1
          }
        }
      }
    }
  }
}

object InsertContext {

  /** Reserve the spot for when we actually insert real nodes later */
  def reserveSpotContext[El <: ReactiveElement.Base](
    parentNode: El,
    strictMode: Boolean
  ): InsertContext[El] = {
    val sentinelNode = new CommentNode("")

    ParentNode.appendChild(parent = parentNode, child = sentinelNode)

    // #Warning[Fragile] - We avoid instantiating a JsMap in loose mode, for performance.
    //  The JsMap is initialized if/when needed, in forceSetStrictMode.
    new InsertContext[El](
      parentNode = parentNode,
      sentinelNode = sentinelNode,
      strictMode = strictMode,
      extraNodeCount = 0,
      extraNodes = Nil,
      extraNodesMap = if (strictMode) new JsMap() else null
    )
  }

  private[laminar] def nodesToMap(nodes: immutable.Seq[ChildNode.Base]): JsMap[dom.Node, ChildNode.Base] = {
    val acc = new JsMap[dom.Node, ChildNode.Base]()
    nodes.foreach { node =>
      acc.set(node.ref, node)
    }
    acc
  }
}
