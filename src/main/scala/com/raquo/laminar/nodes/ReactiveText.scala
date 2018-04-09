package com.raquo.laminar.nodes

import com.raquo.dombuilder.generic
import com.raquo.laminar.DomApi
import org.scalajs.dom

class ReactiveText(initialText: String)
  extends ReactiveNode
  with ReactiveChildNode[dom.Text]
  with generic.nodes.Text[ReactiveNode, dom.Text, dom.Node] {

  @inline override def text: String = ref.data

  override val ref: dom.Text = DomApi.textApi.createNode(initialText)
}
