package com.raquo.laminar.nodes

import com.raquo.dombuilder.jsdom
import org.scalajs.dom

trait ReactiveChildNode[+Ref <: dom.Node]
  extends ReactiveNode
  with jsdom.nodes.ChildNode[ReactiveNode, Ref] {

  override def clearParent(): Unit = {
    super.clearParent()
    unsubscribeFromAll()
  }
}
