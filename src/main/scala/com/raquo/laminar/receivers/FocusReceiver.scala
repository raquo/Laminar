package com.raquo.laminar.receivers

import com.raquo.airstream.core.EventStream
import com.raquo.laminar.modifiers.{Binder, FocusBinder}
import com.raquo.laminar.nodes.ReactiveHtmlElement

object FocusReceiver {

  @inline def <--($isFocused: EventStream[Boolean]): Binder[ReactiveHtmlElement.Base] = {
    FocusBinder($isFocused)
  }
}
