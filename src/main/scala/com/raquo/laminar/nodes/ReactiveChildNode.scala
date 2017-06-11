package com.raquo.laminar.nodes

import com.raquo.dombuilder.nodes.ChildNode

trait ReactiveChildNode[+Ref <: DomNode, DomNode] extends ChildNode[ReactiveNode, Ref, DomNode] {
  this: ReactiveNode =>

  override def clearParent(): Unit = {
    super.clearParent()
    unsubscribeFromAll()
  }
}
