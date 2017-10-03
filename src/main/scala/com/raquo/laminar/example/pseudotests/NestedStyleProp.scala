package com.raquo.laminar.example.pseudotests

import com.raquo.laminar.child
import com.raquo.laminar.example.components.Toggle
import com.raquo.laminar.implicits._
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.props._
import com.raquo.laminar.styles._
import com.raquo.laminar.tags._
import com.raquo.xstream.XStream
import org.scalajs.dom

object NestedStyleProp {

  def render($color: XStream[String]): ReactiveElement[dom.Element] = {
    div(
      color <-- $color,
      span("HELLO"),
      child <-- $color.map(color => span(color))
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

    div(
      className := "yolo",
      h1("MultiStyleProp"),
//      toggle.vnode,
      toggle2.node,
      div(
//        color <-- $fontColor,
//        fontSize <-- $fontSize,
        render($fontColor)
      )
    )
  }
}
