package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.eventbus.EventBus
import com.raquo.laminar.experimental.airstream.eventstream.EventStream
import com.raquo.laminar.experimental.airstream.fixtures.{Calculation, Effect, TestableOwner}
import org.scalatest.{FunSpec, Matchers}

import scala.collection.mutable

/** A collection of tests that ensure that there are no FRP glitches */
class GlitchSpec extends FunSpec with Matchers {

  it("diamond case has no glitch (combineWith)") {

    implicit val testOwner: TestableOwner = new TestableOwner

    val calculations = mutable.Buffer[Calculation[(Int, Int)]]()
    val effects = mutable.Buffer[Effect[(Int, Int)]]()

    val bus = new EventBus[Int]

    val tens = bus.events.map(_ * 10)
    val hundreds = tens.map(_ * 10)

    val tuples = hundreds.combineWith(tens)
      .map(Calculation.log("tuples", calculations))

    tuples.foreach(effects += Effect("tuples", _))

    // ---

    bus.writer.onNext(1)

    calculations shouldEqual mutable.Buffer(
      Calculation("tuples", (100, 10))
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("tuples", (100, 10))
    )
    effects.clear()

    // ---

    bus.writer.onNext(2)

    calculations shouldEqual mutable.Buffer(
      Calculation("tuples", (200, 20))
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("tuples", (200, 20))
    )
    effects.clear()

    // ---

    bus.writer.onNext(3)

    calculations shouldEqual mutable.Buffer(
      Calculation("tuples", (300, 30))
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("tuples", (300, 30))
    )
    effects.clear()
  }

  it("diamond case has no glitch (withCurrentValueOf)") {
    implicit val testOwner: TestableOwner = new TestableOwner

    val calculations = mutable.Buffer[Calculation[(Int, Int)]]()
    val effects = mutable.Buffer[Effect[(Int, Int)]]()

    val bus = new EventBus[Int]

    val tens = bus.events.map(_ * 10)
    val hundreds = tens.map(_ * 10).toSignal(0)

    val tuples = tens.withCurrentValueOf(hundreds)
      .map(Calculation.log("tuples", calculations))

    tuples.foreach(effects += Effect("tuples", _))

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // ---

    bus.writer.onNext(1)

    calculations shouldEqual mutable.Buffer(
      Calculation("tuples", (10, 100))
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("tuples", (10, 100))
    )
    effects.clear()

    // ---

    bus.writer.onNext(2)

    calculations shouldEqual mutable.Buffer(
      Calculation("tuples", (20, 200))
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("tuples", (20, 200))
    )
    effects.clear()

    // ---

    bus.writer.onNext(3)

    calculations shouldEqual mutable.Buffer(
      Calculation("tuples", (30, 300))
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("tuples", (30, 300))
    )
    effects.clear()
  }

  // I don't really consider doubling of events a glitch in this case.
  // Merge operator does not transform the values of its inputs
  // so there is usually no inconsistent state.
  // @TODO[API] see if there is a use case for a merge-like operator that only returns the last event
  it("diamond case with a merge produces events in correct order") {

    implicit val testOwner: TestableOwner = new TestableOwner

    val calculations = mutable.Buffer[Calculation[Int]]()
    val effects = mutable.Buffer[Effect[Int]]()

    val bus = new EventBus[Int]
    val unrelatedBus = new EventBus[Int]

    val tens = bus.events.map(identity).map(_ * 10)
    val hundreds = tens.map(_ * 10)
    val thousands = hundreds.map(_ * 10)

    val numbers = EventStream
      .merge(tens, thousands, hundreds, unrelatedBus.events)
      .map(Calculation.log("numbers", calculations))

    numbers
      .foreach(effects += Effect("numbers", _))

    // ---

    bus.writer.onNext(1)

    calculations shouldEqual mutable.Buffer(
      Calculation("numbers", 10),
      Calculation("numbers", 100),
      Calculation("numbers", 1000)
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("numbers", 10),
      Effect("numbers", 100),
      Effect("numbers", 1000)
    )
    effects.clear()

    // ---

    // Firing an event on an unrelated bus should behave normally
    unrelatedBus.writer.onNext(-1)

    calculations shouldEqual mutable.Buffer(
      Calculation("numbers", -1)
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("numbers", -1)
    )
    effects.clear()

    // ---

    bus.writer.onNext(2)

    calculations shouldEqual mutable.Buffer(
      Calculation("numbers", 20),
      Calculation("numbers", 200),
      Calculation("numbers", 2000)
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("numbers", 20),
      Effect("numbers", 200),
      Effect("numbers", 2000)
    )
    effects.clear()
  }

  it("Multi-level pending observables resolve in correct order") {

    implicit val testOwner: TestableOwner = new TestableOwner

    val calculations = mutable.Buffer[Calculation[Int]]()
    val effects = mutable.Buffer[Effect[Int]]()

    val busA = new EventBus[Int]
    val busB = new EventBus[Int]

    // A, B – independent
    // C = A + B
    // D = C + B
    // E = C + A
    // X = D + E

    val streamTupleAB = busA.events.combineWith(busB.events)
    val streamC = streamTupleAB.map2(_ + _).map(Calculation.log("C", calculations))
    val streamD = busB.events.combineWith(streamC).map2(_ + _).map(Calculation.log("D", calculations))
    val streamE = busA.events.combineWith(streamC).map2(_ + _).map(Calculation.log("E", calculations))

    val streamX = streamD.combineWith(streamE).map2(_ + _)
      .map(Calculation.log("X", calculations))

    streamX.foreach(effects += Effect("X", _))

    // ---

    // First event does not propagate because streamTupleAB lacks the second input
    busB.writer.onNext(1)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // ---

    busA.writer.onNext(100)

    calculations shouldEqual mutable.Buffer(
      Calculation("C", 101),
      Calculation("D", 102),
      Calculation("E", 201),
      Calculation("X", 303)
    )
    effects shouldEqual mutable.Buffer(
      Effect("X", 303)
    )
    calculations.clear()
    effects.clear()

    // ---

    busA.writer.onNext(200)

    calculations shouldEqual mutable.Buffer(
      Calculation("C", 201),
      Calculation("E", 401), // @TODO[Integrity] This order is acceptable, but why is E evaluated before D?
      Calculation("D", 202),
      Calculation("X", 603)
    )
    effects shouldEqual mutable.Buffer(
      Effect("X", 603)
    )
    calculations.clear()
    effects.clear()

    // @TODO Fire another test event to busB for slightly more thorough test
  }

  // @TODO NEED AN EVENT BUS TEST SIMILAR TO MERGE TEST ABOVE

  // @TODO NEED TO TEST NEW CYCLICAL LOGIC (FIX seenObservables first)
  ignore("deadlocked pending observables resolve by firing a soft synced observable") {

    // @TODO Unsynced version produces unexpected results. Figure that out first before proceeding. Something probably wrong in EventBus

    implicit val testOwner: TestableOwner = new TestableOwner

    val calculations = mutable.Buffer[Calculation[Int]]()
    val effects = mutable.Buffer[Effect[Int]]()

    val busA = new EventBus[Int]
    val busB = new EventBus[Int]
    val busC = new EventBus[Int]

    // A = C | B.map(_ * 10)
    // B = C | A.filter(_ <= 30).map(_ + 1)
    // D = A + B

    val streamA = busA.events
      .map(Calculation.log("A", calculations))
    val streamB = busB.events
      .map(Calculation.log("B", calculations))
    val streamC = busC.events
      .map(Calculation.log("C", calculations))

    busA.writer.addSource(streamC)
    busB.writer.addSource(streamC)
    busA.writer.addSource(streamB.map(_ * 10).map(Calculation.log("B x 10", calculations)))
    busB.writer.addSource(streamA.filter(_ <= 100).map(_ + 1))

    val streamD = streamA.combineWith(streamB).map2((x, y) => {
      println(x, y)
      x + y
    })
      //.map2(_ + _)
      .map(Calculation.log("D", calculations))

    streamB
      .foreach(effects += Effect("B", _))
    streamD
      .foreach(effects += Effect("D", _))

    busA.writer.onNext(1)

    calculations shouldEqual mutable.Buffer(
      Calculation("A", 1),
      Calculation("B", 2),
      Calculation("A", 20),
      Calculation("B", 21),
      Calculation("A", 210)
    )
    effects shouldEqual mutable.Buffer(
      Effect("D", 231),
      Effect("D", 212)
    )
    calculations.clear()
    effects.clear()


    // >> create two event buses that depend on each other's streams
    // >> add some filter to make sure the loop terminates
    // >> fire event on one bus, expect certain events on the output stream,
    //    as well as different events on the soft-sync-ed output stream
  }

}
