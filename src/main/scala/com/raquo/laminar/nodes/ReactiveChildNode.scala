package com.raquo.laminar.nodes

import com.raquo.dombuilder.generic.nodes.ChildNode
import com.raquo.dombuilder.jsdom.domapi.JsTreeApi
import com.raquo.laminar.DomApi
import org.scalajs.dom

trait ReactiveChildNode[+Ref <: dom.Node]
  extends ReactiveNode
  with ChildNode[ReactiveNode, Ref, dom.Node] {

  override val treeApi: JsTreeApi[ReactiveNode] = DomApi.treeApi

  override def clearParent(): Unit = {
    super.clearParent()
    unsubscribeFromAll()
  }
}
