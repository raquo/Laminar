package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.Observer
import com.raquo.laminar.experimental.airstream.fixtures.{Effect, TestableOwner}
import com.raquo.laminar.experimental.airstream.ownership.Owner
import org.scalatest.{FunSpec, Matchers}

import scala.collection.mutable

class EventBusSpec extends FunSpec with Matchers {

  it("toObserver.onNext fires observers") {

    implicit val owner: Owner = new TestableOwner

    val bus = new EventBus[Int]
    val effects = mutable.Buffer[Effect[_]]()

    bus.toObserver.onNext(1)

    val subscription0 = bus.foreach(newValue => effects += Effect("obs0", newValue))

    // new observer should not receive any previous events
    effects shouldEqual mutable.Buffer()

    bus.toObserver.onNext(2)

    effects shouldEqual mutable.Buffer(Effect("obs0", 2))
    effects.clear()

    bus.toObserver.onNext(3)
    bus.toObserver.onNext(4)

    effects shouldEqual mutable.Buffer(Effect("obs0", 3), Effect("obs0", 4))
    effects.clear()

    subscription0.kill()

    val obs1 = Observer[Int](newValue => effects += Effect("obs1", newValue))
    val obs2 = Observer[Int](newValue => effects += Effect("obs2", newValue))

    bus.addObserver(obs1)
    bus.addObserver(obs2)
    val subscription3 = bus.foreach(newValue => effects += Effect("obs3", newValue))

    bus.toObserver.onNext(5)

    effects shouldEqual mutable.Buffer(Effect("obs1", 5), Effect("obs2", 5), Effect("obs3", 5))
    effects.clear()

    bus.removeObserver(obs2)

    bus.toObserver.onNext(6)

    effects shouldEqual mutable.Buffer(Effect("obs1", 6), Effect("obs3", 6))
    effects.clear()

    bus.removeObserver(obs1)

    bus.toObserver.onNext(7)
    bus.toObserver.onNext(8)

    effects shouldEqual mutable.Buffer(Effect("obs3", 7), Effect("obs3", 8))
    effects.clear()

    subscription3.kill()

    bus.toObserver.onNext(9)

    effects shouldEqual mutable.Buffer()
  }
}
