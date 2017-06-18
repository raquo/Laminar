package com.raquo.laminar.builders

import com.raquo.dombuilder.generic
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

class ReactiveTag[+Ref <: dom.Element] (
  override val tagName: String,
  override val void: Boolean = false
) extends generic.builders.Tag[ReactiveElement[Ref]] {

  override def build(): ReactiveElement[Ref] = {
    new ReactiveElement[Ref](tagName, void)
  }
}
