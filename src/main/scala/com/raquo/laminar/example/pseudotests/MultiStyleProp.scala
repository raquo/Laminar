package com.raquo.laminar.example.pseudotests

import com.raquo.laminar._
import com.raquo.laminar.example.components.Toggle
import com.raquo.snabbdom.VNode
import com.raquo.snabbdom.tags._
import com.raquo.snabbdom.props._
import com.raquo.snabbdom.styles._

object MultiStyleProp {

  def apply(): VNode = {

    val toggle = Toggle("Big")
    val toggle2 = Toggle("Red")

    val $fontSize = toggle.$checked.startWith(true).map(checked => if (checked) "45px" else "30px")
    val $fontColor = toggle2.$checked.startWith(true).map(checked => if (checked) "red" else "lime")

    div(
      className := "yolo",
      h1("MultiStyleProp"),
      toggle.vnode,
      toggle2.vnode,
      div(
        color <-- $fontColor,
        fontSize <-- $fontSize,
        span("HELLO")
      )
    )
  }
}
