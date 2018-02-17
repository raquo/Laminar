package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.Observer
import com.raquo.laminar.experimental.airstream.eventbus.EventBus
import com.raquo.laminar.experimental.airstream.fixtures.{Calculation, Effect, TestableOwner}
import org.scalatest.{FunSpec, Matchers}

import scala.collection.mutable

class FlattenEventStreamSpec extends FunSpec with Matchers {

  it("mirrors last emitted stream, but only if subscribed") {

    implicit val owner = new TestableOwner

    val calculations = mutable.Buffer[Calculation[Int]]()
    val effects = mutable.Buffer[Effect[Int]]()

    val metaBus = new EventBus[EventStream[Int]]

    // Create 4 test buses and add logging to their streams
    val sourceBuses = (1 to 4).map(_ => new EventBus[Int])
    val sourceStreams = sourceBuses.zipWithIndex.map {
      case (bus, index) => bus.events.map(Calculation.log(s"source-$index", calculations))
    }

    val $latestNumber = metaBus.events.flatten

    val flattenObserver = Observer[Int](effects += Effect("flattened-obs", _))

    val flattenStream = $latestNumber
      .map(Calculation.log("flattened", calculations))

    flattenStream.addObserver(flattenObserver)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    sourceBuses.foreach(_.writer.onNext(-1))

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    metaBus.writer.onNext(sourceStreams(0))

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    sourceBuses(0).writer.onNext(1)

    calculations shouldEqual mutable.Buffer(
      Calculation("source-0", 1),
      Calculation("flattened", 1)
    )
    effects shouldEqual mutable.Buffer(
      Effect("flattened-obs", 1)
    )

    calculations.clear()
    effects.clear()

    // --

    metaBus.writer.onNext(sourceStreams(1))

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    sourceBuses(1).writer.onNext(2)

    calculations shouldEqual mutable.Buffer(
      Calculation("source-1", 2),
      Calculation("flattened", 2)
    )
    effects shouldEqual mutable.Buffer(
      Effect("flattened-obs", 2)
    )

    calculations.clear()
    effects.clear()

    // --

    val sourceStream2Observer = Observer[Int](effects += Effect("source-2-obs", _))

    sourceStreams(2).addObserver(sourceStream2Observer)
    flattenStream.removeObserver(flattenObserver)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    sourceBuses(2).writer.onNext(3)

    calculations shouldEqual mutable.Buffer(
      Calculation("source-2", 3)
    )
    effects shouldEqual mutable.Buffer(
      Effect("source-2-obs", 3)
    )

    calculations.clear()
    effects.clear()

    // --

    flattenStream.addObserver(flattenObserver)

    calculations shouldEqual mutable.Buffer()
    effects shouldEqual mutable.Buffer()

    // --

    // flatten stream does not run because it forgot the stream
    sourceBuses(2).writer.onNext(4)

    calculations shouldEqual mutable.Buffer(
      Calculation("source-2", 4)
    )
    effects shouldEqual mutable.Buffer(
      Effect("source-2-obs", 4)
    )

    calculations.clear()
    effects.clear()
  }

}
