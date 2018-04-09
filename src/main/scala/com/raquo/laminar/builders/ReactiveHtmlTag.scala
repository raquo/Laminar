package com.raquo.laminar.builders

import com.raquo.dombuilder.generic.syntax.TagSyntax
import com.raquo.domtypes.generic
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

class ReactiveHtmlTag[+Ref <: dom.html.Element] (
  override val name: String,
  override val void: Boolean = false
) extends generic.builders.Tag[ReactiveHtmlElement[Ref]](name, void)
  with TagSyntax[ReactiveHtmlElement[Ref]]
{

  override def build(): ReactiveHtmlElement[Ref] = new ReactiveHtmlElement(this)
}
