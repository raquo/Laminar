package com.raquo.laminar.fixtures.example.pseudotests

import com.raquo.laminar.api._
import com.raquo.laminar.fixtures.example.components.Toggle
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

object MultiSetters {

  def apply(): ReactiveElement.Base = {

    val toggle = Toggle("Big")
    val toggle2 = Toggle("/about")

    val fontSizeStream = toggle.checkedStream.map(checked => if (checked) "45px" else "30px")
    val hrefStream = toggle2.checkedStream.map(checked => if (checked) "http://yolo.com/ABOUT" else "http://yolo.com/")

    L.div(
      L.className := "yolo",
      L.h1("MultiStyleProp"),
      toggle.node,
      toggle2.node,
      L.a(
        L.href <-- hrefStream,
        L.fontSize <-- fontSizeStream,
        L.span("HELLO")
      )
    )
  }
}

