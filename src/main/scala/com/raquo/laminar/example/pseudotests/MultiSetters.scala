package com.raquo.laminar.example.pseudotests

import com.raquo.laminar.bundle._
import com.raquo.laminar.example.components.Toggle
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

object MultiSetters {

  def apply(): ReactiveElement[dom.Element] = {

    val toggle = Toggle("Big")
    val toggle2 = Toggle("/about")

    val $fontSize = toggle.$checked.map(checked => if (checked) "45px" else "30px")
    val $href = toggle2.$checked.map(checked => if (checked) "http://yolo.com/ABOUT" else "http://yolo.com/")

    div(
      className := "yolo",
      h1("MultiStyleProp"),
      toggle.node,
      toggle2.node,
      a(
        href <-- $href,
        fontSize <-- $fontSize,
        span("HELLO")
      )
    )
  }
}

