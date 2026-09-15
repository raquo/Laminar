package com.raquo.laminar.nodes

import com.raquo.airstream.core.AirstreamError
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.RenderableText
import org.scalajs.dom

import scala.scalajs.js.|

class TextNode(initialText: String) extends ChildNode[dom.Text] {

  final override val ref: dom.Text = DomApi.createTextNode(initialText)

  final def text: String = ref.data

  /** Named slots only accept elements, so slotting a raw text node is an error.
    * The node is still inserted, ending up in the component's default slot.
    */
  override private[laminar] def applySlot(
    parent: ParentNode.Base,
    newSlotName: String | Unit
  ): Unit = {
    newSlotName.foreach { name =>
      AirstreamError.sendUnhandledError(new Exception(
        s"Error: You tried to insert a raw text node `${ref.textContent}` into the `${name}` slot of ${DomApi.debugNodeDescription(parent.ref)}>.\n" +
          " - Cause: This is not possible: named slots only accept elements. Your node was inserted into the default slot instead.\n" +
          " - Suggestion: Wrap your text node into `span()`"
      ))
    }
  }
}

object TextNode {

  // #TODO[API] Should this just be the constructor? But then we'd need to carry a reference to `r` in every TextNode...
  def apply[TextLike](initialContent: TextLike)(implicit r: RenderableText[TextLike]): TextNode = {
    new TextNode(r.asString(initialContent))
  }
}
