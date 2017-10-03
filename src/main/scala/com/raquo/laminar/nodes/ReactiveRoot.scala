package com.raquo.laminar.nodes

import com.raquo.dombuilder.generic
import com.raquo.laminar.DomApi
import org.scalajs.dom

class ReactiveRoot(
  override val container: dom.Element,
  override val child: ReactiveChildNode[dom.Element]
) extends ReactiveNode
  with generic.nodes.Root[ReactiveNode, dom.Element, dom.Node] {

  appendChild(child)(DomApi.treeApi)
}
