package com.raquo.laminar.receivers

import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.setters.ChildrenCommandSetter
import com.raquo.laminar.setters.ChildrenCommandSetter.ChildrenCommand
import com.raquo.xstream.XStream
import org.scalajs.dom

class ChildrenCommandReceiver(val element: ReactiveElement[dom.Element]) extends AnyVal {

  @inline def <--($command: XStream[ChildrenCommand]): Unit = {
    (ChildrenCommandReceiver <-- $command)(element)
  }
}

object ChildrenCommandReceiver {

  def <--($command: XStream[ChildrenCommand]): ChildrenCommandSetter = {
    new ChildrenCommandSetter($command)
  }
}
