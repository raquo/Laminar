package com.raquo.laminar.fixtures.example.pseudotests

import com.raquo.laminar.api._
import com.raquo.laminar.fixtures.example.components.Toggle
import com.raquo.laminar.nodes.ReactiveElement

object MultiStyleProp {

  def apply(): ReactiveElement.Base = {

    val toggle = Toggle("Big")
    val toggle2 = Toggle("Red")

    val fontSizeSignal = toggle.checkedStream.toSignal(true).map(checked => if (checked) "45px" else "30px")
    val fontColorSignal = toggle2.checkedStream.toSignal(true).map(checked => if (checked) "red" else "lime")

    L.div(
      L.className := "yolo",
      L.h1("MultiStyleProp"),
      toggle.node,
      toggle2.node,
      L.div(
        L.color <-- fontColorSignal,
        L.fontSize <-- fontSizeSignal,
        L.span("HELLO")
      )
    )
  }
}
