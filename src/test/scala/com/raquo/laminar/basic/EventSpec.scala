package com.raquo.laminar.basic

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class EventSpec extends UnitSpec {

  it("filter applies to observer and all subsequent transformations") {

    // Shadow ScalaTest's `value`
    val value = L.value

    var log: List[String] = Nil
    var pass: Boolean = true

    val inner = span(
      onClick
        .map(_ => log = log :+ "pre-transform")
        .filter(_ => pass)
        .stopPropagation
        .map(_ => log = log :+ "post-transform") --> (_ => log = log :+ "observer")
    )

    mount(div(
      onClick --> (_ => log = log :+ "propagated"),
      inner
    ))

    log shouldBe Nil

    // --

    simulateClick(inner.ref)

    log shouldBe List("pre-transform", "post-transform", "observer")

    log = Nil

    // --

    pass = false

    simulateClick(inner.ref)

    log shouldBe List("pre-transform", "propagated")
    log = Nil

    // --

    simulateClick(inner.ref)

    log shouldBe List("pre-transform", "propagated")
    log = Nil

    // --

    pass = true

    simulateClick(inner.ref)

    log shouldBe List("pre-transform", "post-transform", "observer")

    log = Nil
  }
}
