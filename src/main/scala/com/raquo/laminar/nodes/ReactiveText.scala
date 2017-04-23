package com.raquo.laminar.nodes

import com.raquo.dombuilder.domapi.{NodeApi, TextApi}
import com.raquo.dombuilder.nodes.Text
import com.raquo.laminar
import com.raquo.laminar.ChildNode
import org.scalajs.dom

class ReactiveText(override protected[this] var _text: String)
  extends ReactiveNode
  with Text[ReactiveNode, dom.Text, dom.Node]
  with ChildNode
{
  override val nodeApi: NodeApi[ReactiveNode, dom.Node] = laminar.nodeApi

  override val textNodeApi: TextApi[ReactiveNode, dom.Text] = laminar.textNodeApi
}

