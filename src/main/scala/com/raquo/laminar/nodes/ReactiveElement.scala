package com.raquo.laminar.nodes

import com.raquo.dombuilder.domapi.{ElementApi, EventApi, NodeApi, TreeApi}
import com.raquo.dombuilder.nodes.{Element, EventfulNode}
import com.raquo.laminar
import com.raquo.laminar.{ChildNode, ParentNode}
import org.scalajs.dom

import scala.scalajs.js

class ReactiveElement(val tagName: String)
  extends ReactiveNode
  with Element[ReactiveNode, dom.Element, dom.Node]
  with ParentNode
  with ChildNode
  with EventfulNode[ReactiveNode, dom.Element, dom.Node, dom.Event, js.Function1]
{

  override val elementApi: ElementApi[ReactiveNode, dom.Element] = laminar.elementApi

  override val eventApi: EventApi[ReactiveNode, dom.Node, dom.Event, js.Function1] = laminar.eventApi

  override val nodeApi: NodeApi[ReactiveNode, dom.Node] = laminar.nodeApi

  override val treeApi: TreeApi[ReactiveNode, dom.Node] = laminar.treeApi
}
