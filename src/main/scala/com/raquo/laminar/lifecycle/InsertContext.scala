package com.raquo.laminar.lifecycle

import com.raquo.airstream.JsMap
import com.raquo.laminar.nodes.{ChildNode, CommentNode, ParentNode, ReactiveElement}
import org.scalajs.dom

import scala.collection.immutable

// #TODO[Naming] This feels more like InserterState?
//  "Extra nodes" are more like "content nodes"

// @Note only parentNode and sentinelNode are used by all Inserter-s.
//  - Other fields may remain un-updated if they are not needed for a particular implementation.
class InsertContext[+El <: ReactiveElement.Base](
  val parentNode: El,
  var sentinelNode: ChildNode.Base,
  var extraNodeCount: Int, // This is separate from `extraNodes` for performance
  var extraNodes: immutable.Seq[ChildNode.Base] // #TODO We don't need this anymore, we only keep it for compat in 0.14.x
) {

  private[laminar] var extraNodesMap: JsMap[dom.Node, ChildNode.Base] = InsertContext.nodesToMap(extraNodes)
}

object InsertContext {

  /** Reserve the spot for when we actually insert real nodes later */
  def reserveSpotContext[El <: ReactiveElement.Base](parentNode: El): InsertContext[El] = {
    val sentinelNode = new CommentNode("")

    ParentNode.appendChild(parent = parentNode, child = sentinelNode)

    new InsertContext[El](
      parentNode = parentNode,
      sentinelNode = sentinelNode,
      extraNodeCount = 0,
      extraNodes = Nil
    )
  }

  private[laminar] def nodesToMap(nodes: immutable.Seq[ChildNode.Base]): JsMap[dom.Node, ChildNode.Base] = {
    val acc = JsMap.empty[dom.Node, ChildNode.Base]
    nodes.foreach { node =>
      acc.set(node.ref, node)
    }
    acc
  }
}
