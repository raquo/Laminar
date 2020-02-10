package com.raquo.laminar.receivers

import com.raquo.airstream.eventstream.EventStream
import com.raquo.laminar.modifiers.{FocusSetter, Setter}
import com.raquo.laminar.nodes.ReactiveHtmlElement

object FocusReceiver {

  @inline def <--($isFocused: EventStream[Boolean]): Setter[ReactiveHtmlElement.Base] = {
    FocusSetter($isFocused)
  }
}
