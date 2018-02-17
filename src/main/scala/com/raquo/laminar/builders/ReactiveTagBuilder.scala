package com.raquo.laminar.builders

import com.raquo.domtypes.generic
import org.scalajs.dom

trait ReactiveTagBuilder extends generic.builders.TagBuilder[ReactiveTag, dom.Element] {

  override def tag[Ref <: dom.Element](tagName: String, void: Boolean): ReactiveTag[Ref] = {
    new ReactiveTag[Ref](tagName, void)
  }
}
