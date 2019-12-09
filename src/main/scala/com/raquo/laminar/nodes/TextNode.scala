package com.raquo.laminar.nodes

import com.raquo.domtypes
import com.raquo.laminar.DomApi
import org.scalajs.dom

class TextNode(initialText: String)
  extends ChildNode[dom.Text]
  with domtypes.generic.nodes.Text {

  final override val ref: dom.Text = DomApi.createTextNode(initialText)

  final override def text: String = ref.data
}
