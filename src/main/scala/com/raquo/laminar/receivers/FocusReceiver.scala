package com.raquo.laminar.receivers

import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.setters.FocusSetter
import com.raquo.xstream.XStream
import org.scalajs.dom

class FocusReceiver(val element: ReactiveElement[dom.html.Element]) extends AnyVal {

  @inline def <--($isFocused: XStream[Boolean]): Unit = {
    (FocusReceiver <-- $isFocused)(element)
  }
}

object FocusReceiver {

  @inline def <--($isFocused: XStream[Boolean]): FocusSetter = {
    new FocusSetter($isFocused)
  }
}
