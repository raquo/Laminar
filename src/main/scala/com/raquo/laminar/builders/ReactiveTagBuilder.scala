package com.raquo.laminar.builders

import com.raquo.dombuilder.builders.TagBuilder
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveNode}
import org.scalajs.dom

trait ReactiveTagBuilder extends TagBuilder[ReactiveElement, ReactiveNode, dom.Element, dom.Node] {

  override def tag(tagName: String): ReactiveTag = {
    new ReactiveTag(tagName)
  }
}
