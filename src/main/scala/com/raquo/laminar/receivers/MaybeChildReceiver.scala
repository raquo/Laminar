package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.setters.ChildrenSetter.Child
import com.raquo.laminar.setters.MaybeChildSetter
import org.scalajs.dom

class MaybeChildReceiver(element: ReactiveElement[dom.Element]) {

  def <--($node: Observable[Option[Child]]): Unit = {
    (MaybeChildReceiver <-- $node)(element)
  }
}

object MaybeChildReceiver {

  def <--($maybeChildNode: Observable[Option[Child]]): MaybeChildSetter = {
    new MaybeChildSetter($maybeChildNode)
  }
}
