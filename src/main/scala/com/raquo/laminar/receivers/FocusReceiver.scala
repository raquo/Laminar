package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source.EventSource
import com.raquo.laminar.modifiers.{Binder, FocusBinder}
import com.raquo.laminar.nodes.ReactiveHtmlElement

object FocusReceiver {

  @inline def <--(isFocused: EventSource[Boolean]): Binder[ReactiveHtmlElement.Base] = {
    FocusBinder(isFocused.toObservable)
  }
}
