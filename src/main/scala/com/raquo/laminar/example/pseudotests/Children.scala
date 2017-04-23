package com.raquo.laminar.example.pseudotests

import com.raquo.laminar.child
import com.raquo.laminar.example.components.Toggle
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.props.className
import com.raquo.laminar.tags.{a, div, h1, span}

object Children {
  def apply(): ReactiveElement = {
    val toggle = Toggle("Toggle #1")
    val $text = toggle.$checked.map(checked => if (checked) "[X]" else "[O]")
    val $div = $text.map(div(_)).startWith(div("INIT"))

    div(
      className := "yolo",
      h1("Children"),
      toggle.node,
      child <-- $div
    )
  }
}
