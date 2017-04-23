package com.raquo.laminar.builders

import com.raquo.dombuilder.builders.NodeBuilder
import com.raquo.laminar.nodes.{ReactiveComment, ReactiveNode, ReactiveText}
import org.scalajs.dom

trait ReactiveTextBuilder extends NodeBuilder[ReactiveText, ReactiveNode, dom.Text, dom.Node] {

  override def createNode(): ReactiveText = {
    new ReactiveText("")
  }

  def createNode(text: String): ReactiveText = {
    new ReactiveText(text)
  }
}
