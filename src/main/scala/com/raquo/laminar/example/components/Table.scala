package com.raquo.laminar.example.components

import com.raquo.laminar.RNode
import com.raquo.laminar.tags._
import com.raquo.laminar.props._

object Table {
  def apply(): RNode = {
    table(
      tr(td(colSpan := 2, "a"), td("b")),
      tr(td("1"), td("2"), td("3"))
    )
  }
}
