package com.raquo.laminar.basic

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class HtmlPropSpec extends UnitSpec {

  it("sets props") {

    // Shadow ScalaTest's `value`
    val value = L.value

    mount("input:checkbox", input(typ := "checkbox", checked := true))
    expectNode(input.of(typ is "checkbox", checked is true))
    unmount()

    mount("input:text", input(value := "yolo"))
    expectNode(input.of(value is "yolo"))
    unmount()

    mount("option=true", option(selected := true, value := "12"))
    expectNode(option.of(selected is true, value is "12"))
    unmount()

    mount("option=false", option(selected := false, value := "12"))
    expectNode(option.of(selected is false, value is "12"))
    unmount()

    mount("nested input:text", div("foo", input(value := "yolo")))
    expectNode(div.of("foo", input.of(value is "yolo")))

    unmount()
  }
}
