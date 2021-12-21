package com.raquo.laminar.builders

import com.raquo.domtypes.generic
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.DomApi
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

class HtmlTag[+Ref <: dom.html.Element] (
  override val name: String,
  override val void: Boolean = false
) extends generic.builders.Tag[ReactiveHtmlElement[Ref]](name, void) {

  def apply(modifiers: Modifier[ReactiveHtmlElement[Ref]]*): ReactiveHtmlElement[Ref] = {
    val element = build()
    modifiers.foreach(modifier => modifier(element))
    element
  }

  /** Create a Scala DOM Builder element from this Tag */
  protected def build(): ReactiveHtmlElement[Ref] = new ReactiveHtmlElement(this, DomApi.createHtmlElement(this))
}
