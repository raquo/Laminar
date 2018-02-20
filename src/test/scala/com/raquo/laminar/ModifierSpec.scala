package com.raquo.laminar

import com.raquo.laminar.utils.UnitSpec
import com.raquo.laminar.bundle._
import com.raquo.laminar.experimental.airstream.eventbus.EventBus
import com.raquo.laminar.experimental.airstream.fixtures.TestableOwner

import scala.collection.mutable

class ModifierSpec extends UnitSpec {

  it("meta modifier infers precise type") {

    val testOwner = new TestableOwner

    val checkedBus = new EventBus[Boolean]

    val events = mutable.Buffer[Boolean]()
    checkedBus.events.foreach(events += _)

    val checkbox = input(
      typ := "checkbox",
      checked := true,
      thisNode => onClick().map(_ => thisNode.ref.checked) --> checkedBus
    )

    mount(checkbox)

    events shouldEqual mutable.Buffer()

    // @TODO[Test] Well at least this compiles. Event simulation is broken, it seems.

//    simulateClick(checkbox.ref)
//
//    events shouldEqual mutable.Buffer(false)
//    events.clear()
//
//    simulateClick(checkbox.ref)
//
//    events shouldEqual mutable.Buffer(true)
//    events.clear()
//
//    simulateClick(checkbox.ref)
//
//    events shouldEqual mutable.Buffer(false)
//    events.clear()
  }

}
