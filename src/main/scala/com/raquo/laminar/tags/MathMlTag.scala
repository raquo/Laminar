package com.raquo.laminar.tags

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.Modifier
import com.raquo.laminar.nodes.ReactiveMathMlElement

class MathMlTag(
  override val name: String,
  override val void: Boolean = false
) extends Tag[ReactiveMathMlElement] {

  def apply(modifiers: Modifier[ReactiveMathMlElement]*): ReactiveMathMlElement = {
    val element = build()
    modifiers.foreach(modifier => modifier(element))
    element
  }

  override def jsTagName: String = name.toUpperCase

  /** Create a Scala DOM Builder element from this Tag */
  protected def build(): ReactiveMathMlElement = new ReactiveMathMlElement(this, DomApi.createMathMlElement(this))
}

