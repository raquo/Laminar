package com.raquo.laminar.nodes

import com.raquo.dombuilder.generic
import com.raquo.dombuilder.jsdom.JsCallback
import com.raquo.dombuilder.jsdom.domapi.JsTreeApi
import com.raquo.laminar.DomApi
import org.scalajs.dom

class ReactiveElement[+Ref <: dom.Element](
  override val tagName: String,
  override val void: Boolean
) extends ReactiveNode
  with ReactiveChildNode[Ref]
  with generic.nodes.Element[ReactiveNode, Ref, dom.Node]
  with generic.nodes.EventfulNode[ReactiveNode, Ref, dom.Element, dom.Node, dom.Event, JsCallback]
  with generic.nodes.ParentNode[ReactiveNode, Ref, dom.Node] {

  override val treeApi: JsTreeApi[ReactiveNode] = DomApi.treeApi

  override val ref: Ref = DomApi.elementApi.createNode(this)
}
