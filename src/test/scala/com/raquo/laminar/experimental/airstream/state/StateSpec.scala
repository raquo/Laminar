package com.raquo.laminar.experimental.airstream.state

import com.raquo.laminar.experimental.airstream.core.Observer
import com.raquo.laminar.experimental.airstream.eventbus.EventBus
import com.raquo.laminar.experimental.airstream.fixtures.{Calculation, Effect, TestableOwner}
import org.scalatest.{FunSpec, Matchers}

import scala.collection.mutable

class StateSpec extends FunSpec with Matchers {

  it("MapState propagates eagerly") {

    implicit val owner: TestableOwner = new TestableOwner

    val effects = mutable.Buffer[Effect[Int]]()
    val calculations = mutable.Buffer[Calculation[Int]]()

    val signalObserver = Observer[Int](effects += Effect("signal-obs", _))
    val stateObserver1 = Observer[Int](effects += Effect("state-obs-1", _))
    val stateObserver2 = Observer[Int](effects += Effect("state-obs-2", _))

    val bus = new EventBus[Int]
    val signal = bus.events.toSignal(-1).map(Calculation.log("signal", calculations))

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    val state1 = signal.toState // uses owner here
    val state2 = state1.map(Calculation.log("state2", calculations))
    val state3 = state2.map(_ * 10).map(Calculation.log("state3", calculations))

    calculations shouldEqual mutable.Buffer(
      Calculation("signal", -1),
      Calculation("state2", -1),
      Calculation("state3", -10)
    )
    effects shouldEqual mutable.Buffer()

    calculations.clear()

    // --

    state1.now() shouldEqual -1
    state2.now() shouldEqual -1
    state3.now() shouldEqual -10

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    bus.writer.onNext(1)

    calculations shouldEqual mutable.Buffer(
      Calculation("signal", 1),
      Calculation("state2", 1),
      Calculation("state3", 10)
    )
    effects shouldEqual mutable.Buffer()

    calculations.clear()

    // --

    state1.now() shouldEqual 1
    state2.now() shouldEqual 1
    state3.now() shouldEqual 10

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    state1.addObserver(stateObserver1)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer(
      Effect("state-obs-1", 1)
    )

    effects.clear()

    // --

    bus.writer.onNext(2)

    calculations shouldEqual mutable.Buffer(
      Calculation("signal", 2),
      Calculation("state2", 2),
      Calculation("state3", 20)
    )
    effects shouldEqual mutable.Buffer(
      Effect("state-obs-1", 2)
    )

    calculations.clear()
    effects.clear()

    // --

    state2.addObserver(stateObserver2)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer(
      Effect("state-obs-2", 2)
    )

    effects.clear()

    // --

    bus.writer.onNext(3)

    calculations shouldEqual mutable.Buffer(
      Calculation("signal", 3),
      Calculation("state2", 3),
      Calculation("state3", 30)
    )
    effects shouldEqual mutable.Buffer(
      Effect("state-obs-1", 3),
      Effect("state-obs-2", 3)
    )

    calculations.clear()
    effects.clear()

    // --

    state2.removeObserver(stateObserver2)
    state1.removeObserver(stateObserver1)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    bus.writer.onNext(4)

    calculations shouldEqual mutable.Buffer(
      Calculation("signal", 4),
      Calculation("state2", 4),
      Calculation("state3", 40)
    )
    effects shouldEqual mutable.Buffer()

    calculations.clear()

    // --

    owner.killPossessions()

    bus.writer.onNext(5)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    signal.map(_ * 100).addObserver(signalObserver)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs", 400) // event `5` was not evaluated because no one was listening
    )

    effects.clear()

    // --

    bus.writer.onNext(6)

    calculations shouldEqual mutable.Buffer(
      Calculation("signal", 6)
    )
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs", 600)
    )

    calculations.clear()
    effects.clear()

  }

  it("State.toSignal and Signal.toState") {

    val owner: TestableOwner = new TestableOwner
    val childOwner: TestableOwner = new TestableOwner

    val effects = mutable.Buffer[Effect[Int]]()
    val calculations = mutable.Buffer[Calculation[Int]]()

    val signalObserver1 = Observer[Int](effects += Effect("signal-obs-1", _))
    val signalObserver2 = Observer[Int](effects += Effect("signal-obs-2", _))
    val stateObserver1 = Observer[Int](effects += Effect("state-obs-1", _))
    val stateObserver2 = Observer[Int](effects += Effect("state-obs-2", _))

    val bus = new EventBus[Int]
    val inputSignal = bus.events.toSignal(-1).map(Calculation.log("inputSignal", calculations))

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    val state1 = inputSignal.toState(owner)
      .map(Calculation.log("state1", calculations))
    val state2 = state1
      .map(_ * 10)
      .map(Calculation.log("state2", calculations))

    calculations shouldEqual mutable.Buffer(
      Calculation("inputSignal", -1),
      Calculation("state1", -1),
      Calculation("state2", -10)
    )
    effects shouldEqual mutable.Buffer()

    calculations.clear()

    // --

    val signal1 = state1.toSignal.map(Calculation.log("signal1", calculations))
    val signal2 = state2.toSignal.map(Calculation.log("signal2", calculations))

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    bus.writer.onNext(1)

    calculations shouldEqual mutable.Buffer(
      Calculation("inputSignal", 1),
      Calculation("state1", 1),
      Calculation("state2", 10)
    )
    effects shouldEqual mutable.Buffer()

    calculations.clear()

    // --

    signal1.addObserver(signalObserver1)(owner)

    calculations shouldEqual mutable.Buffer(
      Calculation("signal1", 1)
    )
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs-1", 1)
    )

    calculations.clear()
    effects.clear()

    // --

    bus.writer.onNext(2)

    calculations shouldEqual mutable.Buffer(
      Calculation("inputSignal", 2),
      Calculation("state1", 2),
      Calculation("state2", 20),
      Calculation("signal1", 2)
    )
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs-1", 2)
    )

    calculations.clear()
    effects.clear()

    // --

    val childBus = new EventBus[Int]

    val childState1 = childBus.events
      .toSignal(-1)
      .toState(childOwner)
      .map(Calculation.log("childState1", calculations))

    val childState2 = signal2.toState(childOwner)
      .combineWith(childState1)
      .map2(10 * _ + _)
      .map(Calculation.log("childState2", calculations))

    calculations shouldEqual mutable.Buffer(
      Calculation("childState1", -1),
      Calculation("signal2", 20),
      Calculation("childState2", 20 * 10 + -1)
    )
    effects shouldEqual mutable.Buffer()

    calculations.clear()

    // --

    childBus.writer.onNext(3)

    calculations shouldEqual mutable.Buffer(
      Calculation("childState1", 3),
      Calculation("childState2", 20 * 10 + 3)
    )
    effects shouldEqual mutable.Buffer()

    calculations.clear()

    // --

    bus.writer.onNext(4)

    calculations shouldEqual mutable.Buffer(
      Calculation("inputSignal", 4),
      Calculation("state1", 4),
      Calculation("state2", 40),
      Calculation("signal2", 40),
      Calculation("signal1", 4),
      Calculation("childState2", 40 * 10 + 3)
    )

    effects shouldEqual mutable.Buffer(
      Effect("signal-obs-1", 4)
    )

    calculations.clear()
    effects.clear()

    // --

    childOwner.killPossessions() // this should kills childState1 and childState2

    childState2.now() shouldEqual 40 * 10 + 3

    childBus.writer.onNext(5)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    bus.writer.onNext(6)

    calculations shouldEqual mutable.Buffer(
      Calculation("inputSignal", 6),
      Calculation("state1", 6),
      Calculation("state2", 60),
      Calculation("signal1", 6)
    )

    effects shouldEqual mutable.Buffer(
      Effect("signal-obs-1", 6)
    )

    calculations.clear()
    effects.clear()

  }

  ignore("State.kill") {
    // @TODO[Test] Make sure that calling addInternalObserver oraddobserver after the state has been killed does not somehow re-activate it
  }

  ignore("CombineState") {
    // @TODO[Test]
    // Test ownership of the combined state and hwo it behaves when one of the inputs is killed
    // Make sure killing it properly unsubscribes from parent states
  }

  ignore("State.changes") {
    // @TODO[Test]
  }

}
