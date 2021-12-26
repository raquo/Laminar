package com.raquo.laminar.tags

import com.raquo.laminar.DomApi
import com.raquo.laminar.modifiers.Modifier
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

class HtmlTag[+Ref <: dom.html.Element] (
  override val name: String,
  override val void: Boolean = false
) extends Tag[ReactiveHtmlElement[Ref]] {

  def apply(modifiers: Modifier[ReactiveHtmlElement[Ref]]*): ReactiveHtmlElement[Ref] = {
    val element = build()
    modifiers.foreach(modifier => modifier(element))
    element
  }

  /** Create a Scala DOM Builder element from this Tag */
  protected def build(): ReactiveHtmlElement[Ref] = new ReactiveHtmlElement(this, DomApi.createHtmlElement(this))
}
