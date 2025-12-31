package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class ChildTextOptionReceiverSpec extends UnitSpec {

  it("text.maybe <-- with stream") {

    val bus = new EventBus[Option[String]]

    val el = div(
      "Hello",
      text.maybe <-- bus
    )

    mount(el)

    expectNode(
      div of (
        "Hello",
        sentinel
      )
    )

    // --

    bus.emit(Some("a"))

    expectNode(
      div of (
        "Hello",
        "a"
      )
    )

    // --

    bus.emit(Some("b"))

    expectNode(
      div of (
        "Hello",
        "b"
      )
    )

    // --

    bus.emit(None)

    expectNode(
      div of (
        "Hello",
        sentinel
      )
    )

    // --

    bus.emit(Some("c"))

    expectNode(
      div of (
        "Hello",
        "c"
      )
    )
  }

  it("multiple text <-- with stream") {

    val bus1 = new EventBus[Option[String]]
    val bus2 = new EventBus[Option[String]]

    val el = div(
      "Hello",
      text.maybe <-- bus1,
      text.maybe <-- bus2,
    )

    mount(el)

    expectNode(
      div of (
        "Hello",
        sentinel,
        sentinel
      )
    )

    // --

    bus1.emit(Some("a"))

    expectNode(
      div of (
        "Hello",
        "a",
        sentinel
      )
    )

    // --

    bus1.emit(Some("b"))

    expectNode(
      div of (
        "Hello",
        "b",
        sentinel
      )
    )

    // --

    bus2.emit(Some("1"))

    expectNode(
      div of (
        "Hello",
        "b",
        "1"
      )
    )

    // --

    bus2.emit(Some("2"))

    expectNode(
      div of (
        "Hello",
        "b",
        "2"
      )
    )

    // --

    EventBus.emit(
      bus2 -> Some("3"),
      bus1 -> Some("c")
    )

    expectNode(
      div of (
        "Hello",
        "c",
        "3"
      )
    )

    // --

    bus2.emit(None)

    expectNode(
      div of (
        "Hello",
        "c",
        sentinel
      )
    )

    // --

    EventBus.emit(
      bus1 -> None,
      bus2 -> Some("4")
    )

    bus1.emit(None)

    expectNode(
      div of (
        "Hello",
        sentinel,
        "4"
      )
    )
  }

}
