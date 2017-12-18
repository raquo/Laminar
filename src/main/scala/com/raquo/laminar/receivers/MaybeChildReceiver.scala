package com.raquo.laminar.receivers

import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveElement}
import com.raquo.laminar.setters.MaybeChildSetter
import com.raquo.xstream.XStream
import org.scalajs.dom

class MaybeChildReceiver(element: ReactiveElement[dom.Element]) {

  import MaybeChildReceiver.MaybeChildNode

  def <--($node: XStream[MaybeChildNode]): Unit = {
    (MaybeChildReceiver <-- $node)(element)
  }
}

object MaybeChildReceiver {

  type MaybeChildNode = Option[ReactiveChildNode[dom.Node]]

  def <--($maybeChildNode: XStream[MaybeChildReceiver.MaybeChildNode]): MaybeChildSetter = {
    new MaybeChildSetter($maybeChildNode)
  }
}
