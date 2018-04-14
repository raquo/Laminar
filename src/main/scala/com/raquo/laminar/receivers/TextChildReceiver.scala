package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveText}
import com.raquo.laminar.setters.ChildSetter
import org.scalajs.dom

class TextChildReceiver(element: ReactiveElement[dom.Element]) {

  def <--($node: Observable[String]): Unit = {
    (TextChildReceiver <-- $node)(element)
  }
}

object TextChildReceiver {

  def <--($node: Observable[String]): ChildSetter = {
    new ChildSetter($node.toLazy.map(new ReactiveText(_)))
  }
}
