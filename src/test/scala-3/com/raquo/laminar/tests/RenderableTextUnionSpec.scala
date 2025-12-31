package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.modifiers.RenderableText
import com.raquo.laminar.utils.UnitSpec

import scala.scalajs.js.|

class RenderableTextUnionSpec extends UnitSpec {

  it("simple text mod") {
    expectNode(
      div(if (true) 1 else "none").ref,
      div of "1"
    )

    // --

    val textLike1 = if (false) 1 else "none"

    expectNode(
      div(textLike1).ref,
      div of "none"
    )

    // --

    val textLike2: Boolean | Char = if (true) 'c' else false

    expectNode(
      div(textLike2).ref,
      div of "c"
    )

    // --

    val textLike3: Boolean | Char = if (false) 'c' else false

    expectNode(
      div(textLike3).ref,
      div of "false"
    )
  }

  it("text <-- stringOrInt") {
    val bus = new EventBus[Boolean]

    val el = div(
      "Hello",
      text <-- bus.events.map(if (_) "str" else 5)
    )

    mount(el)

    expectNode(
      div of (
        "Hello",
        sentinel
      )
    )

    // --

    bus.emit(true)

    expectNode(
      div of (
        "Hello",
        "str"
      )
    )

    // --

    bus.emit(false)

    expectNode(
      div of (
        "Hello",
        "5"
      )
    )
  }
}

