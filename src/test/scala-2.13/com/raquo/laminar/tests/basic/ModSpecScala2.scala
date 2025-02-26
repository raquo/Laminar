package com.raquo.laminar.tests.basic

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class ModSpecScala2 extends UnitSpec {

  it("when keyword") {

    var evaluatedUsed = false
    var evaluatedUnused = false

    val el = div(
      when(true) {
        evaluatedUsed = true
        title("foo")
      },
      when(false) {
        evaluatedUnused = true
        title("bar")
      },
      when(true)(
        height.px(100)
      ),
      when(true) {
        List(minAttr("10"), maxAttr("20"))
      },
      when(true)(div("hello")),
      when(true) {
        onMountInsert(_ =>
          "world"
        )
      },
      when(true) {
        text <-- Val("text")
      }
    )

    mount(el)

    expectNode(div.of(
      title is "foo",
      height is "100px",
      minAttr is "10",
      maxAttr is "20",
      div.of("hello"),
      "world",
      "text"
    ))

    assertEquals(evaluatedUsed, true)
    assertEquals(evaluatedUnused, false)
  }
}
