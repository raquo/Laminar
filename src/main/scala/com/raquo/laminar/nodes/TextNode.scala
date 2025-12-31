package com.raquo.laminar.nodes

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.RenderableText
import org.scalajs.dom

class TextNode(initialText: String) extends ChildNode[dom.Text] {

  final override val ref: dom.Text = DomApi.createTextNode(initialText)

  final def text: String = ref.data
}

object TextNode {

  // #TODO[API] Should this just be the constructor? But then we'd need to carry a reference to `r` in every TextNode...
  def apply[TextLike](initialContent: TextLike)(implicit r: RenderableText[TextLike]): TextNode = {
    new TextNode(r.asString(initialContent))
  }
}
