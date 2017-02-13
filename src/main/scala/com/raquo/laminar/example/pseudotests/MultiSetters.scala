package com.raquo.laminar.example.pseudotests

import com.raquo.laminar._
import com.raquo.laminar.example.components.Toggle
import com.raquo.snabbdom.VNode
import com.raquo.snabbdom.tags._
import com.raquo.snabbdom.attrs.href
import com.raquo.snabbdom.props.className
import com.raquo.snabbdom.styles._

object MultiSetters {

  def apply(): VNode = {

    val toggle = Toggle("Big")
    val toggle2 = Toggle("/about")

    val $fontSize = toggle.$checked.map(checked => if (checked) "45px" else "30px")
    val $href = toggle2.$checked.map(checked => if (checked) "http://yolo.com/ABOUT" else "http://yolo.com/")

    div(
      className := "yolo",
      h1("MultiStyleProp"),
      toggle.vnode,
      toggle2.vnode,
      a(
        href <-- $href,
        fontSize <-- $fontSize,
        span("HELLO")
      )
    )
  }
}

