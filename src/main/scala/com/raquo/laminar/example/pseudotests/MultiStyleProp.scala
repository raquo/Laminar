package com.raquo.laminar.example.pseudotests

import com.raquo.laminar.api._
import com.raquo.laminar.example.components.Toggle
import com.raquo.laminar.nodes.ReactiveNode

object MultiStyleProp {

  def apply(): ReactiveNode = {

    val toggle = Toggle("Big")
    val toggle2 = Toggle("Red")

    val $fontSize = toggle.$checked.toSignal(true).map(checked => if (checked) "45px" else "30px")
    val $fontColor = toggle2.$checked.toSignal(true).map(checked => if (checked) "red" else "lime")

    L.div(
      L.className := "yolo",
      L.h1("MultiStyleProp"),
      toggle.node,
      toggle2.node,
      L.div(
        L.color <-- $fontColor,
        L.fontSize <-- $fontSize,
        L.span("HELLO")
      )
    )
  }
}
