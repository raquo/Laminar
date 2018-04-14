package com.raquo.laminar.example.pseudotests

import com.raquo.airstream.eventstream.EventStream
import com.raquo.laminar.api._
import com.raquo.laminar.example.components.Toggle
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

object NestedStyleProp {

  def render($color: EventStream[String]): ReactiveElement[dom.Element] = {
    L.div(
      L.color <-- $color,
      L.span("HELLO"),
      L.child <-- $color.map(color => L.span(color))
    )
  }

  def apply(): ReactiveElement[dom.Element] = {

    val toggle = Toggle("Big")
    val toggle2 = Toggle("Red")

    val $fontSize = toggle
      .$checked
//      .startWith(true)
      .map(checked => if (checked) "45px" else "30px")
    val $fontColor = toggle2
      .$checked
//      .startWith(true)
      .map(checked => if (checked) "red" else "lime")

    L.div(
      L.className := "yolo",
      L.h1("MultiStyleProp"),
//      toggle.vnode,
      toggle2.node,
      L.div(
//        color <-- $fontColor,
//        fontSize <-- $fontSize,
        render($fontColor)
      )
    )
  }
}
