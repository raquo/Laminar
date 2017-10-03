package com.raquo.laminar.nodes

import com.raquo.dombuilder.generic
import com.raquo.dombuilder.jsdom.domapi.JsTreeApi
import com.raquo.laminar.DomApi
import org.scalajs.dom

class ReactiveText(val text: String )
  extends ReactiveNode
  with ReactiveChildNode[dom.Text]
  with generic.nodes.Text[ReactiveNode, dom.Text, dom.Node] {

  override val treeApi: JsTreeApi[ReactiveNode] = DomApi.treeApi

  override val ref: dom.Text = DomApi.textApi.createNode

  setText(text)(DomApi.textApi)
}
