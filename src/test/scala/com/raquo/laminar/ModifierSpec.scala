package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.TestableOwner
import com.raquo.laminar.utils.UnitSpec

import scala.collection.mutable
import scala.scalajs.js

class ModifierSpec extends UnitSpec {

  it("ModifierSeq implicit conversion works for both strings and nodes") {

    val strings = List("a", "b")
    val nodes = Vector(span("ya"), article("yo"))
    val jsNodes: scala.collection.Seq[Mod[Element]] = js.Array(span("js")) // JS arrays need some help for now. We could optimize that if needed
    val mixed: Seq[Mod[HtmlElement]] = Vector("c", input())

    mount(div(strings, nodes, jsNodes, mixed))

    expectNode(div like (
      "a", "b",
      span like "ya", article like "yo",
      span like "js",
      "c", input
    ))
  }

  it("inContext modifier infers precise type") {

    val testOwner = new TestableOwner

    val checkedBus = new EventBus[Boolean]

    val events = mutable.Buffer[Boolean]()
    checkedBus.events.foreach(events += _)(testOwner)

    val checkbox = input(
      typ := "checkbox",
      checked := true,
      inContext(thisNode => onClick.mapTo(thisNode.ref.checked) --> checkedBus)
    )

    mount(checkbox)

    events shouldEqual mutable.Buffer()

    // --

    checkbox.ref.click()

    events shouldEqual mutable.Buffer(false)
    events.clear()

    // --

    checkbox.ref.click()

    events shouldEqual mutable.Buffer(true)
    events.clear()

    // --

    checkbox.ref.click()

    events shouldEqual mutable.Buffer(false)
    events.clear()
  }

  it ("onMount infers precise type and provides implicit owner") {

    var value = 0

    val bus = new EventBus[Int]
    val observer = Observer[Int](ev => value = ev)

    val el = div("Hello", onMount(ctx => {
      import ctx.owner
      bus.events.addObserver(observer) // using owner implicitly
    }))

    // --

    bus.writer.onNext(1)

    assert(value == 0) // not subscribed yet

    mount(el)

    bus.writer.onNext(2)

    assert(value == 2)
  }

  it ("onMount and onUnmount work for repeated mounting") {

    var numMounts = 0
    var numUnmounts = 0

    val el = div("Hello", onMount(_ => numMounts += 1), onUnmount(_ => numUnmounts += 1))

    assert(numMounts == 0)
    assert(numUnmounts == 0)

    // --

    mount(el)

    assert(numMounts == 1)
    assert(numUnmounts == 0)

    numMounts = 0

    // --

    unmount()

    assert(numMounts == 0)
    assert(numUnmounts == 1)

    numUnmounts = 0

    // --

    mount(el)

    assert(numMounts == 1)
    assert(numUnmounts == 0)

    numMounts = 0

    // --

    unmount()

    assert(numMounts == 0)
    assert(numUnmounts == 1)

    numUnmounts = 0
  }

}
