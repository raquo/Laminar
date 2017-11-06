package com.raquo.laminar.syntax

import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.receivers.FocusReceiver
import org.scalajs.dom

/** This only applies to **HTML** elements */
class ReactiveHtmlElementSyntax(val element: ReactiveElement[dom.html.Element]) extends AnyVal {

  @inline def <--[V] (focus: FocusReceiver.type): FocusReceiver = new FocusReceiver(element)
}
