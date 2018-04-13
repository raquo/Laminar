package com.raquo.laminar.example.components

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveNode

object Table {
  def apply(): ReactiveNode = {
    table(
      tr(td(colSpan := 2, "a"), td("b")),
      tr(td("1"), td("2"), td("3"))
    )
  }
}
