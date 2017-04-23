package com.raquo.laminar.nodes

import com.raquo.dombuilder.domapi.{CommentApi, NodeApi}
import com.raquo.dombuilder.nodes.Comment
import com.raquo.laminar
import com.raquo.laminar.ChildNode
import org.scalajs.dom

class ReactiveComment(override protected[this] var _text: String)
  extends ReactiveNode
  with Comment[ReactiveNode, dom.Comment, dom.Node]
  with ChildNode
{

  override val commentApi: CommentApi[ReactiveNode, dom.Comment] = laminar.commentApi

  override val nodeApi: NodeApi[ReactiveNode, dom.Node] = laminar.nodeApi
}

