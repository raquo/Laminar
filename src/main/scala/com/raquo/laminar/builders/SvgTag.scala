package com.raquo.laminar.builders

import com.raquo.domtypes.generic
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.nodes.ReactiveSvgElement
import org.scalajs.dom

class SvgTag[+Ref <: dom.svg.Element] (
  override val name: String,
  override val void: Boolean = false
) extends generic.builders.Tag[ReactiveSvgElement[Ref]](name, void) {

  def apply(modifiers: Modifier[ReactiveSvgElement[Ref]]*): ReactiveSvgElement[Ref] = {
    val element = build()
    modifiers.foreach(modifier => modifier(element))
    element
  }

  /** Create a Scala DOM Builder element from this Tag */
  def build(): ReactiveSvgElement[Ref] = new ReactiveSvgElement(this)
}
