package com.raquo.laminar.basic

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class ModSpec extends UnitSpec {

  it("when keyword") {

    val el = div(
      when(true) {
        title("foo")
      },
      when(false) {
        title("bar")
      },
      when(true)(
        height.px(100),
        width.px(200)
      ),
      when(true)(div("hello")),
      when(true) {
        text <-- Val("text")
      }
    )

    mount(el)

    expectNode(div.of(
      title is "foo",
      height is "100px",
      width is "200px",
      div.of("hello"),
      "text"
    ))
  }
}
