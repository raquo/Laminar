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

    inner.ref.click()

    log shouldBe List("pre-transform", "post-transform", "observer")

    log = Nil

    // --

    pass = false

    inner.ref.click()

    log shouldBe List("pre-transform", "propagated")
    log = Nil

    // --

    inner.ref.click()

    log shouldBe List("pre-transform", "propagated")
    log = Nil

    // --

    pass = true

    inner.ref.click()

    log shouldBe List("pre-transform", "post-transform", "observer")

    log = Nil
  }

  it("filter works with stopImmediatePropagation") {

    // Shadow ScalaTest's `value`
    val value = L.value

    var log: List[String] = Nil
    var pass: Boolean = true

    val inner = span(
      onClick
        .map(_ => log = log :+ "pre-transform")
        .filter(_ => pass)
        .stopImmediatePropagation
        .map(_ => log = log :+ "post-transform") --> (_ => log = log :+ "observer"),
      onClick --> (_ => log = log :+ "other-listener")
    )

    mount(div(
      onClick --> (_ => log = log :+ "parent-listener"),
      inner
    ))

    log shouldBe Nil

    // --

    inner.ref.click()

    log shouldBe List("pre-transform", "post-transform", "observer")

    log = Nil

    // --

    pass = false

    inner.ref.click()

    log shouldBe List("pre-transform", "other-listener", "parent-listener")
    log = Nil

    // --

    inner.ref.click()

    log shouldBe List("pre-transform", "other-listener", "parent-listener")
    log = Nil

    // --

    pass = true

    inner.ref.click()

    log shouldBe List("pre-transform", "post-transform", "observer")

    log = Nil
  }
}
