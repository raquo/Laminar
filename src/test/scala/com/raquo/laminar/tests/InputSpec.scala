package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class InputSpec extends UnitSpec {

  it("checkbox + stream") {

    val checkedBus = EventBus[Boolean]()

    val el = input(
      typ("checkbox"),
      checked <-- checkedBus
    )

    mount(el)

    expectNode(
      input of (
        typ is "checkbox",
        checked is false
      )
    )

    checkedBus.emit(true)

    expectNode(
      input of (
        typ is "checkbox",
        checked is true
      )
    )

    checkedBus.emit(false)

    expectNode(
      input of (
        typ is "checkbox",
        checked is false
      )
    )
  }

  it("checkbox + defaultChecked + stream") {

    val checkedBus = EventBus[Boolean]()

    val el = input(
      typ("checkbox"),
      defaultChecked(true),
      checked <-- checkedBus
    )

    mount(el)

    expectNode(
      input of (
        typ is "checkbox",
        checked is true
      )
    )

    checkedBus.emit(false)

    expectNode(
      input of (
        typ is "checkbox",
        checked is false
      )
    )

    checkedBus.emit(true)

    expectNode(
      input of (
        typ is "checkbox",
        checked is true
      )
    )
  }

  it("checkbox + defaultChecked + signal") {

    val checkedVar = Var[Boolean](false)

    val el = input(
      typ("checkbox"),
      defaultChecked(true),
      checked <-- checkedVar.signal.map(identity) // mapping to get a regular lazy signal to avoid any potential Var-specific behaviour
    )

    mount(el)

    expectNode(
      input of (
        typ is "checkbox",
        checked is false // Signal has initial value, so it overrides defaultChecked
      )
    )

    checkedVar.set(true)

    expectNode(
      input of (
        typ is "checkbox",
        checked is true
      )
    )

    checkedVar.set(false)

    expectNode(
      input of (
        typ is "checkbox",
        checked is false
      )
    )

  }

  it("checkbox + controlled + var") {

    val checkedVar = Var[Boolean](true)

    val el = input(
      typ("checkbox"),
      controlled(
        checked <-- checkedVar,
        onClick.mapToChecked --> checkedVar
      )
    )

    mount(el)

    expectNode(
      input of (
        typ is "checkbox",
        checked is true
      )
    )

    checkedVar.set(false)

    expectNode(
      input of (
        typ is "checkbox",
        checked is false
      )
    )

    checkedVar.set(true)

    expectNode(
      input of (
        typ is "checkbox",
        checked is true
      )
    )

  }
}
