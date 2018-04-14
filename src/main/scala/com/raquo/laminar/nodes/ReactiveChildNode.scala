package com.raquo.laminar.nodes

import com.raquo.dombuilder.generic.nodes.{ChildNode, ParentNode}
import com.raquo.dombuilder.jsdom.domapi.JsTreeApi
import com.raquo.laminar.DomApi
import org.scalajs.dom

import scala.annotation.tailrec

trait ReactiveChildNode[+Ref <: dom.Node]
  extends ReactiveNode
  with ChildNode[ReactiveNode, Ref, dom.Node] {

  final override val treeApi: JsTreeApi[ReactiveNode] = DomApi.treeApi
}

object ReactiveChildNode {

  type BaseParentNode = ReactiveNode with ParentNode[ReactiveNode, dom.Node, dom.Node]
  //  type BaseChildNode = ReactiveNode with ChildNode[ReactiveNode, dom.Node, dom.Node]

  def isParentMounted(maybeParent: Option[BaseParentNode]): Boolean = {
    maybeParent.exists(parent => isNodeMounted(parent.ref))
  }

  @inline def isNodeMounted(node: dom.Node): Boolean = {
    isDescendantOf(node = node, ancestor = dom.document)
  }

  @tailrec final def isDescendantOf(node: dom.Node, ancestor: dom.Node): Boolean = {
    // @TODO[Performance] Maybe use https://developer.mozilla.org/en-US/docs/Web/API/Node/contains instead (but IE only supports it for Elements)
    node.parentNode match {
      case null => false
      case `ancestor` => true
      case intermediateParent => isDescendantOf(intermediateParent, ancestor)
    }
  }
}
