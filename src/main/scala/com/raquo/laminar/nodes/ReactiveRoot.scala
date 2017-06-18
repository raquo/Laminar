package com.raquo.laminar.nodes

import com.raquo.dombuilder.generic
import com.raquo.dombuilder.jsdom
import org.scalajs.dom

class ReactiveRoot (
  override val container: dom.Element,
  override val child: ReactiveNode with jsdom.nodes.ChildNode[ReactiveNode, dom.Element]
)
  extends ReactiveNode
  with jsdom.nodes.ParentNode[ReactiveNode, dom.Element]
  with generic.nodes.Root[ReactiveNode, ReactiveNode with jsdom.nodes.ChildNode[ReactiveNode, dom.Element], dom.Element, dom.Node]
