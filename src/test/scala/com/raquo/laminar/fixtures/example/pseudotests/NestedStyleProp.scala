package com.raquo.laminar.fixtures.example.pseudotests

import com.raquo.airstream.core.EventStream
import com.raquo.laminar.api._
import com.raquo.laminar.fixtures.example.components.Toggle
import com.raquo.laminar.nodes.ReactiveElement

object NestedStyleProp {

  def render(colorStream: EventStream[String]): ReactiveElement.Base = {
    L.div(
      L.color <-- colorStream,
      L.span("HELLO"),
      L.child <-- colorStream.map(color => L.span(color))
    )
  }

  def apply(): ReactiveElement.Base = {

    val toggle = Toggle("Big")
    val toggle2 = Toggle("Red")

    val fontSizeStream = toggle
      .checkedStream
//      .startWith(true)
      .map(checked => if (checked) "45px" else "30px")
    val fontColorStream = toggle2
      .checkedStream
//      .startWith(true)
      .map(checked => if (checked) "red" else "lime")

    L.div(
      L.className := "yolo",
      L.h1("MultiStyleProp"),
//      toggle.vnode,
      toggle2.node,
      L.div(
//        color <-- fontColorStream,
//        fontSize <-- fontSizeStream,
        render(fontColorStream)
      )
    )
  }
}
