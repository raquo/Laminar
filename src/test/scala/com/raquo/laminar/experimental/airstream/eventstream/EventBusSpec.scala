package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.Observer
import com.raquo.laminar.experimental.airstream.eventbus.EventBus
import com.raquo.laminar.experimental.airstream.fixtures.{Calculation, Effect, TestableOwner}
import com.raquo.laminar.experimental.airstream.ownership.Owner
import org.scalatest.{FunSpec, Matchers}

import scala.collection.mutable

class EventBusSpec extends FunSpec with Matchers {

  it("writer.onNext fires observers") {

    implicit val owner: Owner = new TestableOwner

    val bus = new EventBus[Int]
    val effects = mutable.Buffer[Effect[_]]()

    bus.writer.onNext(1)

    val subscription0 = bus.events.foreach(newValue => effects += Effect("obs0", newValue))

    // new observer should not receive any previous events
    effects shouldEqual mutable.Buffer()

    bus.writer.onNext(2)

    effects shouldEqual mutable.Buffer(Effect("obs0", 2))
    effects.clear()

    bus.writer.onNext(3)
    bus.writer.onNext(4)

    effects shouldEqual mutable.Buffer(Effect("obs0", 3), Effect("obs0", 4))
    effects.clear()

    subscription0.kill()

    val obs1 = Observer[Int](newValue => effects += Effect("obs1", newValue))
    val obs2 = Observer[Int](newValue => effects += Effect("obs2", newValue))

    bus.events.addObserver(obs1)
    bus.events.addObserver(obs2)
    val subscription3 = bus.events.foreach(newValue => effects += Effect("obs3", newValue))

    bus.writer.onNext(5)

    effects shouldEqual mutable.Buffer(Effect("obs1", 5), Effect("obs2", 5), Effect("obs3", 5))
    effects.clear()

    bus.events.removeObserver(obs2)

    bus.writer.onNext(6)

    effects shouldEqual mutable.Buffer(Effect("obs1", 6), Effect("obs3", 6))
    effects.clear()

    bus.events.removeObserver(obs1)

    bus.writer.onNext(7)
    bus.writer.onNext(8)

    effects shouldEqual mutable.Buffer(Effect("obs3", 7), Effect("obs3", 8))
    effects.clear()

    subscription3.kill()

    bus.writer.onNext(9)

    effects shouldEqual mutable.Buffer()
  }

  it("writer.addSource/removeSource fires observers") {

    val testOwner: TestableOwner = new TestableOwner
    val owner1: TestableOwner = new TestableOwner
    val owner2: TestableOwner = new TestableOwner

    val testBus = new EventBus[Int]
    val bus1 = new EventBus[Int]
    val bus2 = new EventBus[Int]

    val sourceStream1 = bus1.events.map(_ * 10)
    val sourceStream2 = bus2.events.map(_ * 100)

    val effects = mutable.Buffer[Effect[_]]()

    testBus.events.foreach(newValue => effects += Effect("obs0", newValue))(testOwner)

    bus1.writer.onNext(1)

    effects shouldEqual mutable.Buffer()

    // ---

    val source1 = testBus.writer.addSource(sourceStream1)(owner1)
    bus1.writer.onNext(2)
    bus2.writer.onNext(2)

    effects shouldEqual mutable.Buffer(Effect("obs0", 20))
    effects.clear()

    // ---

    testBus.writer.addSource(sourceStream2)(owner2)
    bus1.writer.onNext(3)
    bus2.writer.onNext(3)

    effects shouldEqual mutable.Buffer(Effect("obs0", 30), Effect("obs0", 300))
    effects.clear()

    // ---

    source1.removeSource()

    bus1.writer.onNext(4)
    bus2.writer.onNext(4)

    effects shouldEqual mutable.Buffer(Effect("obs0", 400))
    effects.clear()

    // --

    owner2.killPossessions()

    bus1.writer.onNext(5)
    bus2.writer.onNext(5)

    effects shouldEqual mutable.Buffer()
  }

  it("xxx") {

    println(">>>>>")
    println(">>>>>")
    println(">>>>>")

    implicit val testOwner: TestableOwner = new TestableOwner

    val calculations = mutable.Buffer[Calculation[Int]]()
    val effects = mutable.Buffer[Effect[Int]]()

    val busA = new EventBus[Int]
    val busB = new EventBus[Int]

    // A = C | B.map(_ * 10)
    // B = C | A.filter(_ <= 30).map(_ + 1)
    // D = A + B

    val streamA = busA.events
      .map(Calculation.log("A", calculations))
    val streamB = busB.events
      .map(Calculation.log("B", calculations))
    val streamAx10 = streamA.map(_ * 10)
      .map(Calculation.log("A x 10", calculations))
    val streamBx10 = streamB.map(_ * 10)
      .map(Calculation.log("B x 10", calculations))

    busA.writer.addSource(streamAx10.filter(_ <= 20))
//    busA.writer.addSource(streamBx10)
//    busB.writer.addSource(streamA.filter(_ <= 100).map(_ + 1))

//    val streamE = EventStream.merge(streamA, streamB)
//      .map(Calculation.log("E", calculations))

    val streamF = streamA.map(identity)
      .map(Calculation.log("F", calculations))

//    val streamD = streamA.combineWith(streamBx10).map2((x, y) => {
//      println(x, y)
//      x + y
//    })
//      .map(Calculation.log("D", calculations))

    streamA
      .foreach(effects += Effect("A", _))

//    streamB
//      .foreach(effects += Effect("B", _))
//    streamD
//      .foreach(effects += Effect("D", _))
//    streamE
//      .foreach(effects += Effect("E", _))
    streamF
      .foreach(effects += Effect("F", _))

    busA.writer.onNext(1)

    calculations shouldEqual mutable.Buffer(
//      Calculation("A", 1),
//      Calculation("B", 2),
//      Calculation("A", 20),
//      Calculation("B", 21),
//      Calculation("A", 210)
    )
    effects shouldEqual mutable.Buffer(
//      Effect("D", 231),
//      Effect("D", 212)
    )
    calculations.clear()
    effects.clear()
  }
}
