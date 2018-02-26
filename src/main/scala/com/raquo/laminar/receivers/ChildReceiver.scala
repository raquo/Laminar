package com.raquo.laminar.receivers

import com.raquo.laminar.experimental.airstream.core.Observable
import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveElement}
import com.raquo.laminar.setters.ChildSetter
import org.scalajs.dom

class ChildReceiver(element: ReactiveElement[dom.Element]) {

  def <--($node: Observable[ReactiveChildNode[dom.Node]]): Unit = {
    (ChildReceiver <-- $node)(element)
  }
}

object ChildReceiver {

  val maybe: MaybeChildReceiver.type = MaybeChildReceiver

  val text: TextChildReceiver.type = TextChildReceiver

  def <--($node: Observable[ReactiveChildNode[dom.Node]]): ChildSetter = {
    new ChildSetter($node)
  }
}
