package com.raquo.laminar.fixtures

import com.raquo.laminar.nodes.Slot

object ButtonElement extends WebComponent("sl-button") {

  object slots {

    val prefix = new Slot("prefix")
  }
}
