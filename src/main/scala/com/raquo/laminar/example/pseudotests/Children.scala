package com.raquo.laminar.example.pseudotests

import com.raquo.laminar.api._
import com.raquo.laminar.example.components.Toggle
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

object Children {
  def apply(): ReactiveElement[dom.Element] = {
    val toggle = Toggle("Toggle #1")
    val $text = toggle.$checked.map(checked => if (checked) "[X]" else "[O]")
    val $div = $text.map(L.div(_)).toSignal(L.div("INIT"))

    L.div(
      L.className := "yolo",
      L.h1("Children"),
      toggle.node,
      L.child <-- $div
    )
  }
}
