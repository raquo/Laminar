package com.raquo.laminar.builders

import com.raquo.domtypes.generic
import org.scalajs.dom

trait ReactiveHtmlTagBuilder extends generic.builders.TagBuilder[ReactiveHtmlTag, dom.html.Element] {

  override def tag[Ref <: dom.html.Element](tagName: String, void: Boolean): ReactiveHtmlTag[Ref] = {
    new ReactiveHtmlTag[Ref](tagName, void)
  }
}
