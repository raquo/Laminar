package com.raquo.laminar.nodes

import com.raquo.laminar.receivers.FocusReceiver
import org.scalajs.dom

/** This only applies to **HTML** elements */
class ReactiveHtmlElement(val element: ReactiveElement[dom.html.Element]) extends AnyVal {

  @inline def <--[V] (focus: FocusReceiver.type): FocusReceiver = new FocusReceiver(element)
}
