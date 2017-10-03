package com.raquo.laminar.nodes

import com.raquo.dombuilder.generic
import com.raquo.dombuilder.jsdom.domapi.JsTreeApi
import com.raquo.laminar.DomApi
import org.scalajs.dom

class ReactiveComment(val text: String)
  extends ReactiveNode
  with ReactiveChildNode[dom.Comment]
  with generic.nodes.Comment[ReactiveNode, dom.Comment, dom.Node] {

  override val treeApi: JsTreeApi[ReactiveNode] = DomApi.treeApi

  override val ref: dom.Comment = DomApi.commentApi.createNode

  setText(text)(DomApi.commentApi)
}

