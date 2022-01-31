package com.raquo.laminar.lifecycle

import com.raquo.ew.JsMap
import com.raquo.laminar.nodes.{ChildNode, CommentNode, ParentNode, ReactiveElement}
import org.scalajs.dom

import scala.collection.immutable

// #TODO[Naming] This feels more like InserterState?
//  "Extra nodes" are more like "content nodes"

// @Note only parentNode and sentinelNode are used by all Inserter-s.
//  - Other fields may remain un-updated if they are not needed for a particular use case.
final class InsertContext[+El <: ReactiveElement.Base](
  val parentNode: El,
  var sentinelNode: ChildNode.Base,
  var extraNodeCount: Int, // This is separate from `extraNodesMap` for performance #TODO[Performance]: Check if this is still relevant with JsMap
  var extraNodesMap: JsMap[dom.Node, ChildNode.Base]
)

object InsertContext {

  /** Reserve the spot for when we actually insert real nodes later */
  def reserveSpotContext[El <: ReactiveElement.Base](parentNode: El): InsertContext[El] = {
    val sentinelNode = new CommentNode("")

    ParentNode.appendChild(parent = parentNode, child = sentinelNode)

    new InsertContext[El](
      parentNode = parentNode,
      sentinelNode = sentinelNode,
      extraNodeCount = 0,
      extraNodesMap = new JsMap()
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
