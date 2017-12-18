package com.raquo.laminar.example.components

import com.raquo.laminar.bundle._
import com.raquo.laminar.nodes.ReactiveNode
import com.raquo.xstream.XStream

class Task (
  val $checked: XStream[Boolean],
  val node: ReactiveNode
)

object Task {
  def apply(name: String): Task = {
    val toggle = Toggle("")

    val node = div(
      toggle.node,
      name,
      br(),
      br()
    )

    new Task(toggle.$checked, node)
  }
}
