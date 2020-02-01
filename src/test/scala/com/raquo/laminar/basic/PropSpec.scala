package com.raquo.laminar.basic

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class PropSpec extends UnitSpec {

  it("sets props") {

    // Shadow ScalaTest's `value`
    val value = L.value

    mount("input:checkbox", input(typ := "checkbox", checked := true))
    expectNode(input like (typ is "checkbox", checked is true))
    unmount()

    mount("input:text", input(value := "yolo"))
    expectNode(input like(value is "yolo"))
    unmount()

    mount("option=true", option(selected := true, value := "12"))
    expectNode(option like(selected is true, value is "12"))
    unmount()

    mount("option=false", option(selected := false, value := "12"))
    expectNode(option like(selected is false, value is "12"))
    unmount()

    mount("nested input:text", div("foo", input(value := "yolo")))
    expectNode(div like ("foo", input like(value is "yolo")))

    unmount()
  }
}
