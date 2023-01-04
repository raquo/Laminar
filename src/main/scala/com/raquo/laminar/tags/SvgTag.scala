package com.raquo.laminar.tags

import com.raquo.laminar.DomApi
import com.raquo.laminar.modifiers.Modifier
import com.raquo.laminar.nodes.ReactiveSvgElement
import org.scalajs.dom

class SvgTag[+Ref <: dom.svg.Element] (
  override val name: String,
  override val void: Boolean = false
) extends Tag[ReactiveSvgElement[Ref]] {

  def apply(modifiers: Modifier[ReactiveSvgElement[Ref]]*): ReactiveSvgElement[Ref] = {
    val element = build()
    modifiers.foreach(modifier => modifier(element))
    element
  }

  /** Create a Scala DOM Builder element from this Tag */
  def build(): ReactiveSvgElement[Ref] = new ReactiveSvgElement(this, DomApi.createSvgElement(this))
}
