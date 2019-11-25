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

  it("meta modifier infers precise type") {

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

    simulateClick(checkbox.ref)

    events shouldEqual mutable.Buffer(false)
    events.clear()

    // --

    simulateClick(checkbox.ref)

    events shouldEqual mutable.Buffer(true)
    events.clear()

    // --

    simulateClick(checkbox.ref)

    events shouldEqual mutable.Buffer(false)
    events.clear()
  }

}
