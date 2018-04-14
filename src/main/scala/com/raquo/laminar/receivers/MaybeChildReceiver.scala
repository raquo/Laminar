package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveElement}
import com.raquo.laminar.setters.MaybeChildSetter
import org.scalajs.dom

class MaybeChildReceiver(element: ReactiveElement[dom.Element]) {

  import MaybeChildReceiver.MaybeChildNode

  def <--($node: Observable[MaybeChildNode]): Unit = {
    (MaybeChildReceiver <-- $node)(element)
  }
}

object MaybeChildReceiver {

  type MaybeChildNode = Option[ReactiveChildNode[dom.Node]]

  def <--($maybeChildNode: Observable[MaybeChildReceiver.MaybeChildNode]): MaybeChildSetter = {
    new MaybeChildSetter($maybeChildNode)
  }
}
