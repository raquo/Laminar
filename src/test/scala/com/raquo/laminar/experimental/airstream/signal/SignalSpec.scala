package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.core.Observer
import com.raquo.laminar.experimental.airstream.eventbus.EventBus
import com.raquo.laminar.experimental.airstream.fixtures.{Calculation, Effect, TestableOwner}
import org.scalatest.{FunSpec, Matchers}

import scala.collection.mutable

class SignalSpec extends FunSpec with Matchers {

  it("SignalFromEvent lazily propagates values to child MapSignal") {

    // @TODO add another mapped dependency on signal and verify that one's evaluations as well (maybe in a separate test?)

    implicit val testOwner: TestableOwner = new TestableOwner

    val effects = mutable.Buffer[Effect[Int]]()
    val calculations = mutable.Buffer[Calculation[Int]]()

    val signalObserver1 = Observer[Int](effects += Effect("signal-obs-1", _))
    val signalObserver2 = Observer[Int](effects += Effect("signal-obs-2", _))

    val bus = new EventBus[Int]
    val signal = bus.events
      .map(Calculation.log("bus", calculations))
      .map(_ * 10)
      .toSignal(initialValue = -1)
      .map(Calculation.log("map-signal", calculations))

    // --

    // Signals are lazy: without an observer, nothing will happen
    bus.writer.onNext(1)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    // When observer is added, it immediately gets the last evaluated current value
    // Note: Because signal had no observer when bus fired value `1`, that event was NOT used to compute new value.
    //       This is expected. If this is undesirable to your case, use State instead of Signal.
    signal.addObserver(signalObserver1)

    calculations shouldEqual mutable.Buffer(
      Calculation("map-signal", -1) // First time current value of this signal was needed, so it is now calculated
    )
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs-1", -1)
    )

    calculations.clear()
    effects.clear()

    // --

    bus.writer.onNext(2)

    calculations shouldEqual mutable.Buffer(
      Calculation("bus", 2),
      Calculation("map-signal", 20)
    )
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs-1", 20)
    )

    calculations.clear()
    effects.clear()

    // --

    // When adding a new observer, it gets the signal's current value.
    // Here the current value has been updated by the previous event, and the signal remembers it.
    signal.addObserver(signalObserver2)

    calculations shouldEqual mutable.Buffer() // Using cached calculation
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs-2", 20)
    )

    effects.clear()

    // --

    bus.writer.onNext(3)

    calculations shouldEqual mutable.Buffer(
      Calculation("bus", 3),
      Calculation("map-signal", 30)
    )
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs-1", 30),
      Effect("signal-obs-2", 30)
    )

    calculations.clear()
    effects.clear()

    // --

    signal.removeObserver(signalObserver1)

    bus.writer.onNext(4)

    calculations shouldEqual mutable.Buffer(
      Calculation("bus", 4),
      Calculation("map-signal", 40)
    )
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs-2", 40)
    )

    calculations.clear()
    effects.clear()

    // --

    signal.removeObserver(signalObserver2)

    bus.writer.onNext(5)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    // Adding the observer again will work exactly the same as adding it initially
    signal.addObserver(signalObserver2)

    calculations shouldEqual mutable.Buffer() // Using cached calculation
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs-2", 40)
    )

    calculations.clear()
    effects.clear()

    // --

    bus.writer.onNext(6)

    calculations shouldEqual mutable.Buffer(
      Calculation("bus", 6),
      Calculation("map-signal", 60)
    )
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs-2", 60)
    )

    calculations.clear()
    effects.clear()
  }

  it("Signal.changes lazily reflects the changes of underlying signal") {

    implicit val testOwner: TestableOwner = new TestableOwner

    val effects = mutable.Buffer[Effect[Int]]()
    val calculations = mutable.Buffer[Calculation[Int]]()

    val signalObserver = Observer[Int](effects += Effect("signal-obs", _))
    val changesObserver = Observer[Int](effects += Effect("changes-obs", _))

    val bus = new EventBus[Int]
    val signal = bus.events
      .map(Calculation.log("bus", calculations))
      .map(_ * 10)
      .toSignal(initialValue = -1)
      .map(Calculation.log("map-signal", calculations))
    val changes = signal.changes.map(Calculation.log("changes", calculations))

    // Verify that .changes returns the same stream every time (lazy val)
    signal.changes shouldBe signal.changes

    // --

    bus.writer.onNext(1)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    changes.addObserver(changesObserver)

    bus.writer.onNext(2)

    calculations shouldEqual mutable.Buffer(
      Calculation("bus", 2),
      Calculation("map-signal", 20),
      Calculation("changes", 20)
    )
    effects shouldEqual mutable.Buffer(
      Effect("changes-obs", 20)
    )

    calculations.clear()
    effects.clear()

    // --

    // Adding observer to signal sends the last evaluated current value to it
    signal.addObserver(signalObserver)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs", 20)
    )

    effects.clear()

    // --

    bus.writer.onNext(3)

    calculations shouldEqual mutable.Buffer(
      Calculation("bus", 3),
      Calculation("map-signal", 30),
      Calculation("changes", 30)
    )
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs", 30),
      Effect("changes-obs", 30)
    )

    calculations.clear()
    effects.clear()

    // --

    changes.removeObserver(changesObserver)

    bus.writer.onNext(4)

    calculations shouldEqual mutable.Buffer(
      Calculation("bus", 4),
      Calculation("map-signal", 40)
    )
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs", 40)
    )

    calculations.clear()
    effects.clear()

    // --

    changes.addObserver(changesObserver)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    changes.removeObserver(changesObserver)
    signal.removeObserver(signalObserver)

    bus.writer.onNext(5)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()
  }

  it("MapSignal.now/onNext combination does not redundantly evaluate project/initialValue") {

    implicit val testOwner: TestableOwner = new TestableOwner

    val effects = mutable.Buffer[Effect[Int]]()
    val calculations = mutable.Buffer[Calculation[Int]]()

    val signalObserver = Observer[Int](effects += Effect("signal-obs", _))

    val bus = new EventBus[Int]
    val signal = bus.events
      .map(Calculation.log("bus", calculations))
      .map(_ * 10)
      .toSignal(initialValue = -1)
      .map(Calculation.log("map-signal", calculations))

    // --

    bus.writer.onNext(1)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    signal.now() shouldBe -1

    calculations shouldEqual mutable.Buffer(
      Calculation("map-signal", -1)
    )
    effects shouldEqual mutable.Buffer()

    calculations.clear()

    // --

    signal.addObserver(signalObserver)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs", -1)
    )

    effects.clear()

    // --

    bus.writer.onNext(2)

    calculations shouldEqual mutable.Buffer(
      Calculation("bus", 2),
      Calculation("map-signal", 20)
    )
    effects shouldEqual mutable.Buffer(
      Effect("signal-obs", 20)
    )

    calculations.clear()
    effects.clear()

    // --

    signal.now()

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()
  }

}
