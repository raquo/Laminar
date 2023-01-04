package com.raquo.laminar.fixtures.example.pseudotests

import com.raquo.laminar.api._
import com.raquo.laminar.fixtures.example.components.Toggle
import com.raquo.laminar.nodes.ReactiveElement

object Children {

  def apply(): ReactiveElement.Base = {
    val toggle = Toggle("Toggle #1")
    val textStream = toggle.checkedStream.map(checked => if (checked) "[X]" else "[O]")
    val divSignal = textStream.map(L.div(_)).toSignal(L.div("INIT"))

    L.div(
      L.className := "yolo",
      L.h1("Children"),
      toggle.node,
      L.child <-- divSignal
    )
  }
}
