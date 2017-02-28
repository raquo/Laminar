package com.raquo.laminar.example.components

import com.raquo.snabbdom.tags._
import com.raquo.snabbdom.props._
import com.raquo.snabbdom.VNode

object Table {
  def apply(): VNode = {
    table(
      tr(td(colSpan := 2, "a"), td("b")),
      tr(td("1"), td("2"), td("3"))
    )
  }
}
