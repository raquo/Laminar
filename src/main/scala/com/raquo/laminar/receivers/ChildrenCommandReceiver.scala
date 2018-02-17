package com.raquo.laminar.receivers

import com.raquo.laminar.experimental.airstream.eventstream.EventStream
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.setters.ChildrenCommandSetter
import com.raquo.laminar.setters.ChildrenCommandSetter.ChildrenCommand
import org.scalajs.dom

class ChildrenCommandReceiver(val element: ReactiveElement[dom.Element]) extends AnyVal {

  @inline def <--($command: EventStream[ChildrenCommand]): Unit = {
    (ChildrenCommandReceiver <-- $command)(element)
  }
}

object ChildrenCommandReceiver {

  def <--($command: EventStream[ChildrenCommand]): ChildrenCommandSetter = {
    new ChildrenCommandSetter($command)
  }
}
