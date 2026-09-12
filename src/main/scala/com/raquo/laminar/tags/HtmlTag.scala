package com.raquo.laminar.tags

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.Modifier
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

class HtmlTag[+Ref <: dom.html.Element](
  override val name: String,
  override val void: Boolean = false
) extends Tag[ReactiveHtmlElement[Ref]] {

  def apply(modifiers: Modifier[ReactiveHtmlElement[Ref]]*): ReactiveHtmlElement[Ref] = {
    val element = build()
    modifiers.foreach(modifier => modifier(element))
    element
  }

  override def jsTagName: String = name.toUpperCase

  /** Create a Scala DOM Builder element from this Tag */
  protected def build(): ReactiveHtmlElement[Ref] = new ReactiveHtmlElement(this, DomApi.createHtmlElement(this))
}

object HtmlTag {

  def apply[Ref <: dom.html.Element](
    name: String,
    void: Boolean = false
  ): HtmlTag[Ref] = {
    if (DomApi.isCustomElementTagName(name)) {
      // Note: This is merely a catch-all default configuration:
      //       You should call CustomHtmlTag constructor explicitly.
      new CustomHtmlTag[Ref](name, void)
    } else {
      new HtmlTag[Ref](name, void)
    }
  }
}
