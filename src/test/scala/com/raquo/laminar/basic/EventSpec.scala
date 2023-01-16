package com.raquo.laminar.basic

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.AirstreamFixtures.Effect
import com.raquo.laminar.utils.UnitSpec

import scala.collection.mutable

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

  it("LockedEventKey - compose") {

    val effects = mutable.Buffer[Effect[Int]]()

    var eventCount = 0

    val observer = Effect.logObserver("obs", effects)

    val childEl = span(
      onClick.preventDefault.compose { events =>
        events.flatMap { _ =>
          eventCount += 1
          effects += Effect("processor", eventCount)
          EventStream.fromValue(eventCount).map(_ * 100)
        }.startWith(-1)
      } --> observer,
    )

    val el = div(childEl)

    assert(eventCount == 0)
    assert(effects.isEmpty)

    // --

    mount(el)

    assert(eventCount == 0)
    assert(effects.toList == List(
      Effect("obs", -1)
    ))
    effects.clear()

    // --

    childEl.ref.click()

    assert(eventCount == 1)
    assert(effects.toList == List(
      Effect("processor", 1),
      Effect("obs", 100)
    ))
    effects.clear()

    // --

    el.ref.click()

    assert(eventCount == 1)
    assert(effects.isEmpty)

    // --

    childEl.ref.click()

    assert(eventCount == 2)
    assert(effects.toList == List(
      Effect("processor", 2),
      Effect("obs", 200)
    ))
  }

  it("LockedEventKey - flatMap") {

    val effects = mutable.Buffer[Effect[Int]]()

    var eventCount = 0

    val observer = Effect.logObserver("obs", effects)

    // #Note that the startWith(-1) here acts on a different observable than in case of `compose`

    // #TODO[IDE] Intellij (2021.3.2) shows a fake error on flatMap usage, at least in Scala 2.
    val childEl = span(
      onClick.preventDefault.flatMap { ev =>
        eventCount += 1
        effects += Effect("processor", eventCount)
        EventStream.fromValue(eventCount).map(_ * 100).startWith(-1)
      } --> observer,
    )

    val el = div(childEl)

    assert(eventCount == 0)
    assert(effects.isEmpty)

    // --

    mount(el)

    assert(eventCount == 0)
    assert(effects.toList == Nil)
    effects.clear()

    // --

    childEl.ref.click()

    assert(eventCount == 1)
    assert(effects.toList == List(
      Effect("processor", 1),
      Effect("obs", -1),
      Effect("obs", 100)
    ))
    effects.clear()

    // --

    el.ref.click()

    assert(eventCount == 1)
    assert(effects.isEmpty)

    // --

    childEl.ref.click()

    assert(eventCount == 2)
    assert(effects.toList == List(
      Effect("processor", 2),
      Effect("obs", -1),
      Effect("obs", 200)
    ))

  }
}
