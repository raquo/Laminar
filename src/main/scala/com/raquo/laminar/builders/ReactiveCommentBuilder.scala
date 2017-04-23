package com.raquo.laminar.builders

import com.raquo.dombuilder.builders.NodeBuilder
import com.raquo.laminar.nodes.{ReactiveComment, ReactiveNode}
import org.scalajs.dom

trait ReactiveCommentBuilder extends NodeBuilder[ReactiveComment, ReactiveNode, dom.Comment, dom.Node] {

  override def createNode(): ReactiveComment = {
    new ReactiveComment("")
  }
}
