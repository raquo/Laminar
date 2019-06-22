package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.setters.ChildSetter
import com.raquo.laminar.setters.ChildrenSetter.Child
import org.scalajs.dom

class ChildReceiver(element: ReactiveElement[dom.Element]) {

  def <--($node: Observable[Child]): Unit = {
    (ChildReceiver <-- $node)(element)
  }
}

object ChildReceiver {

  val maybe: MaybeChildReceiver.type = MaybeChildReceiver

  val text: TextChildReceiver.type = TextChildReceiver

  def <--($node: Observable[Child]): ChildSetter = {
    new ChildSetter($node)
  }
}
