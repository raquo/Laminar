package com.raquo.laminar.builders

import com.raquo.dombuilder.generic.syntax.TagSyntax
import com.raquo.domtypes.generic
import com.raquo.laminar.nodes.ReactiveSvgElement
import org.scalajs.dom

class ReactiveSvgTag[+Ref <: dom.svg.Element] (
  override val name: String,
  override val void: Boolean = false
) extends generic.builders.Tag[ReactiveSvgElement[Ref]](name, void)
  with TagSyntax[ReactiveSvgElement[Ref]]
{

  override def build(): ReactiveSvgElement[Ref] = new ReactiveSvgElement(this)
}
