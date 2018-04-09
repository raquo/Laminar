package com.raquo.laminar.builders

import com.raquo.domtypes.generic
import org.scalajs.dom

trait ReactiveSvgTagBuilder extends generic.builders.TagBuilder[ReactiveSvgTag, dom.svg.Element] {

  override def tag[Ref <: dom.svg.Element](tagName: String, void: Boolean): ReactiveSvgTag[Ref] = {
    new ReactiveSvgTag[Ref](tagName, void)
  }
}
