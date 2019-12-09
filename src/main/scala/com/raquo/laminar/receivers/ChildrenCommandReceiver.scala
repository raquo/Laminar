package com.raquo.laminar.receivers

import com.raquo.airstream.eventstream.EventStream
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.setters.ChildrenCommandSetter
import com.raquo.laminar.setters.ChildrenCommandSetter.ChildrenCommand
import org.scalajs.dom

class ChildrenCommandReceiver(val element: ReactiveElement.Base) extends AnyVal {

  @inline def <--(commandStream: EventStream[ChildrenCommand]): Unit = {
    (ChildrenCommandReceiver <-- commandStream)(element)
  }
}

object ChildrenCommandReceiver {

  def <--(commandStream: EventStream[ChildrenCommand]): ChildrenCommandSetter = {
    new ChildrenCommandSetter(commandStream)
  }
}
