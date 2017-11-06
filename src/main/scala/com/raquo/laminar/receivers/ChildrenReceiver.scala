package com.raquo.laminar.receivers

import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.setters.ChildrenSetter
import com.raquo.laminar.setters.ChildrenSetter.Children
import com.raquo.xstream.XStream
import org.scalajs.dom

class ChildrenReceiver(val element: ReactiveElement[dom.Element]) extends AnyVal {

  @inline def <--($children: XStream[Children]): Unit = {
    (ChildrenReceiver <-- $children) apply element
  }
}

object ChildrenReceiver {

  val command: ChildrenCommandReceiver.type = ChildrenCommandReceiver

  def <--($children: XStream[Children]): ChildrenSetter = {
    new ChildrenSetter($children)
  }
}
