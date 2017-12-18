package com.raquo.laminar.receivers

import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveElement}
import com.raquo.laminar.setters.ChildSetter
import com.raquo.xstream.XStream
import org.scalajs.dom

class ChildReceiver(element: ReactiveElement[dom.Element]) {

  def <--($node: XStream[ReactiveChildNode[dom.Node]]): Unit = {
    (ChildReceiver <-- $node)(element)
  }
}

object ChildReceiver {

  val maybe: MaybeChildReceiver.type = MaybeChildReceiver

  val text: TextChildReceiver.type = TextChildReceiver

  def <--($node: XStream[ReactiveChildNode[dom.Node]]): ChildSetter = {
    new ChildSetter($node)
  }
}
