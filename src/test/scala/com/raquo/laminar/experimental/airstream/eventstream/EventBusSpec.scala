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

    source1.kill()

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

}
