package com.raquo.laminar.nodes

import com.raquo.laminar.DomApi
import org.scalajs.dom

class TextNode(initialText: String) extends ChildNode[dom.Text] {

  final override val ref: dom.Text = DomApi.createTextNode(initialText)

  final def text: String = ref.data
}
