package com.raquo.laminar.fixtures.example.components

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveElement

object Table {
  def apply(): ReactiveElement.Base = {
    table(
      tr(td(colSpan := 2, "a"), td("b")),
      tr(td("1"), td("2"), td("3"))
    )
  }
}
