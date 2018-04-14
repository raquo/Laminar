package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.setters.FocusSetter
import org.scalajs.dom

class FocusReceiver(val element: ReactiveHtmlElement[dom.html.Element]) extends AnyVal {

  @inline def <--($isFocused: Observable[Boolean]): Unit = {
    (FocusReceiver <-- $isFocused)(element)
  }
}

object FocusReceiver {

  @inline def <--($isFocused: Observable[Boolean]): FocusSetter = {
    new FocusSetter($isFocused)
  }
}
