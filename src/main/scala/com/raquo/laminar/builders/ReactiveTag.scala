package com.raquo.laminar.builders

import com.raquo.dombuilder.builders.Tag
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveNode}
import org.scalajs.dom

class ReactiveTag(val tagName: String) extends Tag[ReactiveElement, ReactiveNode, dom.Element, dom.Node] {

  override def createNode(): ReactiveElement = {
    new ReactiveElement(tagName)
  }
}
