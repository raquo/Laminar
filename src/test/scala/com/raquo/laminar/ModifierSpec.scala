package com.raquo.laminar

import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.TestableOwner
import com.raquo.laminar.utils.UnitSpec

import scala.collection.mutable

class ModifierSpec extends UnitSpec {

  it("meta modifier infers precise type") {

    val testOwner = new TestableOwner

    val checkedBus = new EventBus[Boolean]

    val events = mutable.Buffer[Boolean]()
    checkedBus.events.foreach(events += _)(testOwner)

    val checkbox = input(
      typ := "checkbox",
      checked := true,
      inContext(thisNode => onClick.map(_ => thisNode.ref.checked) --> checkedBus)
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
