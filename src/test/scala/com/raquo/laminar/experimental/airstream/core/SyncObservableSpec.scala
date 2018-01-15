package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.eventbus.EventBus
import com.raquo.laminar.experimental.airstream.eventstream.EventStream
import com.raquo.laminar.experimental.airstream.fixtures.{Calculation, Effect, TestableOwner}
import org.scalatest.{FunSpec, Matchers}

import scala.collection.mutable

class SyncObservableSpec extends FunSpec with Matchers {

  it("sync and softSync fix diamond case glitch (combineWith)") {

    implicit val testOwner: TestableOwner = new TestableOwner

    val calculations = mutable.Buffer[Calculation[(Int, Int)]]()
    val effects = mutable.Buffer[Effect[(Int, Int)]]()

    val bus = new EventBus[Int]

    val tens = bus.events.map(_ * 10)
    val hundreds = tens.map(_ * 10)

    val tuples = hundreds.combineWith(tens)
      .map(Calculation.log("tuples", calculations))
    val syncTuples = tuples.sync()
    val softSyncTuples = tuples.softSync()

    tuples
      .foreach(effects += Effect("tuples", _))
    syncTuples
      .map(Calculation.log("syncTuples", calculations))
      .foreach(effects += Effect("syncTuples", _))
    softSyncTuples
      .map(Calculation.log("softSyncTuples", calculations))
      .foreach(effects += Effect("softSyncTuples", _))

    // ---

    // On first event the "tuples" stream appears to have no glitch because
    // combined stream needs events from both streams to emit a tuple.
    bus.writer.onNext(1)

    calculations shouldEqual mutable.Buffer(
      Calculation("tuples", (100, 10)),
      Calculation("syncTuples", (100, 10)),
      Calculation("softSyncTuples", (100, 10))
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("tuples", (100, 10)),
      Effect("syncTuples", (100, 10)),
      Effect("softSyncTuples", (100, 10))
    )
    effects.clear()

    // ---

    // Subsequent events demonstrate the glitch – without sync, an extra event
    // with inconsistent state is fired
    bus.writer.onNext(2)

    calculations shouldEqual mutable.Buffer(
      Calculation("tuples", (200, 10)),
      Calculation("tuples", (200, 20)),
      Calculation("syncTuples", (200, 20)),
      Calculation("softSyncTuples", (200, 20))
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("tuples", (200, 10)), // 200 is updated first due to depth-first propagation
      Effect("tuples", (200, 20)),
      Effect("syncTuples", (200, 20)),
      Effect("softSyncTuples", (200, 20))
    )
    effects.clear()

    // ---

    bus.writer.onNext(3)

    calculations shouldEqual mutable.Buffer(
      Calculation("tuples", (300, 20)),
      Calculation("tuples", (300, 30)),
      Calculation("syncTuples", (300, 30)),
      Calculation("softSyncTuples", (300, 30))
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("tuples", (300, 20)),
      Effect("tuples", (300, 30)),
      Effect("syncTuples", (300, 30)),
      Effect("softSyncTuples", (300, 30))
    )
    effects.clear()
  }

  it("sync and softSync fix merge stream glitch") {

    implicit val testOwner: TestableOwner = new TestableOwner

    val calculations = mutable.Buffer[Calculation[Int]]()
    val effects = mutable.Buffer[Effect[Int]]()

    val bus = new EventBus[Int]
    val unrelatedBus = new EventBus[Int]

    val tens = bus.events.map(_ * 10)
    val hundreds = tens.map(_ * 10)

    val numbers = EventStream
      .merge(tens, hundreds, unrelatedBus.events)
      .map(Calculation.log("numbers", calculations))
    val syncNumbers = numbers.sync()
    val softSyncNumbers = numbers.softSync()

    numbers
      .foreach(effects += Effect("numbers", _))
    syncNumbers
      .map(Calculation.log("syncNumbers", calculations))
      .foreach(effects += Effect("syncNumbers", _))
    softSyncNumbers
      .map(Calculation.log("softSyncNumbers", calculations))
      .foreach(effects += Effect("softSyncNumbers", _))

    // Sync and Soft Sync should propagate only the last event
    // fired by the merged stream within the propagation

    // ---

    bus.writer.onNext(1)

    calculations shouldEqual mutable.Buffer(
      Calculation("numbers", 10),
      Calculation("numbers", 100),
      Calculation("syncNumbers", 100),
      Calculation("softSyncNumbers", 100)
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("numbers", 10),
      Effect("numbers", 100),
      Effect("syncNumbers", 100),
      Effect("softSyncNumbers", 100)
    )
    effects.clear()

    // ---

    // Firing an event on an unrelated bus should behave normally
    unrelatedBus.writer.onNext(-1)

    calculations shouldEqual mutable.Buffer(
      Calculation("numbers", -1),
      Calculation("syncNumbers", -1),
      Calculation("softSyncNumbers", -1)
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("numbers", -1),
      Effect("syncNumbers", -1),
      Effect("softSyncNumbers", -1)
    )
    effects.clear()

    // ---

    bus.writer.onNext(2)

    calculations shouldEqual mutable.Buffer(
      Calculation("numbers", 20),
      Calculation("numbers", 200),
      Calculation("syncNumbers", 200),
      Calculation("softSyncNumbers", 200)
    )
    calculations.clear()

    effects shouldEqual mutable.Buffer(
      Effect("numbers", 20),
      Effect("numbers", 200),
      Effect("syncNumbers", 200),
      Effect("softSyncNumbers", 200)
    )
    effects.clear()
  }

  it("dependent but not deadlocked pending observables resolve correctly") {

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

    streamX
      .foreach(effects += Effect("X", _))
    streamX.sync()
      .map(Calculation.log("sync-X", calculations))
      .foreach(effects += Effect("sync-X", _))
    streamX.softSync()
      .map(Calculation.log("softSync-X", calculations))
      .foreach(effects += Effect("softSync-X", _))

    // ---

    // First event does not propagate because streamTupleAB lacks the second input
    busB.writer.onNext(1)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // ---

    // Second event does not have redundant calculations because D lacked second input until now
    busA.writer.onNext(100)

    calculations shouldEqual mutable.Buffer(
      Calculation("C", 101),
      Calculation("D", 102),
      Calculation("E", 201),
      Calculation("X", 303),
      Calculation("sync-X", 303),
      Calculation("softSync-X", 303)
    )
    effects shouldEqual mutable.Buffer(
      Effect("X", 303),
      Effect("sync-X", 303),
      Effect("softSync-X", 303)
    )
    calculations.clear()
    effects.clear()

    // ---

    // Third event results in redundant calculations and inconsistent effects for unsynced
    // streams, whereas synced and soft-synced streams remain consistent
    busA.writer.onNext(200)

    calculations shouldEqual mutable.Buffer(
      Calculation("C", 201),
      Calculation("D", 202),
      Calculation("X", 403), // Inconsistent: calculated with old E = 201, and new D = 202
      Calculation("E", 301), // Inconsistent: calculated with new C = 201, but old A = 100
      Calculation("X", 503), // Inconsistent: calculated with inconsistent E = 301, and new D = 202
      Calculation("E", 401), // Now consistent: calculated with new C = 201, and new A = 200
      Calculation("X", 603), // Now consistent: calculated with new E = 401, and new D = 202
      Calculation("sync-X", 603), // sync and softSync versions are always consistent
      Calculation("softSync-X", 603)
    )
    effects shouldEqual mutable.Buffer(
      Effect("X", 403), // Inconsistent because not sync-ed (see explanation in calculations above)
      Effect("X", 503), // ^ same
      Effect("X", 603),
      Effect("sync-X", 603), // sync and softSync versions are always consistent
      Effect("softSync-X", 603)
    )
    calculations.clear()
    effects.clear()

    // @TODO Fire another test event to busB for slightly more thorough test
  }

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
