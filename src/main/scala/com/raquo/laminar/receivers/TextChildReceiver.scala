package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.nodes.{ReactiveElement, TextNode}
import com.raquo.laminar.setters.ChildSetter

class TextChildReceiver(element: ReactiveElement.Base) {

  def <--($node: Observable[String]): Unit = {
    (TextChildReceiver <-- $node)(element)
  }
}

object TextChildReceiver {

  def <--($node: Observable[String]): ChildSetter = {
    new ChildSetter($node.map(new TextNode(_)))
  }
}
