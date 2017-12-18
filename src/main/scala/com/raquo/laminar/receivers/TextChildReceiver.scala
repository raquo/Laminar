package com.raquo.laminar.receivers

import com.raquo.laminar.nodes.{ReactiveElement, ReactiveText}
import com.raquo.laminar.setters.ChildSetter
import com.raquo.xstream.XStream
import org.scalajs.dom

class TextChildReceiver(element: ReactiveElement[dom.Element]) {

  def <--($node: XStream[String]): Unit = {
    (TextChildReceiver <-- $node)(element)
  }
}

object TextChildReceiver {

  def <--($node: XStream[String]): ChildSetter = {
    new ChildSetter($node.map(new ReactiveText(_)))
  }
}
