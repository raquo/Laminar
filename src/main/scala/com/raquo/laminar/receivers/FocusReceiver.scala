package com.raquo.laminar.receivers

import com.raquo.airstream.eventstream.EventStream
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.setters.FocusSetter
import org.scalajs.dom

class FocusReceiver(val element: ReactiveHtmlElement.Base) extends AnyVal {

  @inline def <--($isFocused: EventStream[Boolean]): Unit = {
    (FocusReceiver <-- $isFocused)(element)
  }
}

object FocusReceiver {

  @inline def <--($isFocused: EventStream[Boolean]): FocusSetter = {
    new FocusSetter($isFocused)
  }
}
