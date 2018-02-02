package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.features.SingleParentObservable
import com.raquo.laminar.experimental.airstream.core.{Observable, Transaction}

import scala.scalajs.js

class DelayEventStream[A](
  override protected val parent: EventStream[A],
  delayMillis: Int
) extends EventStream[A] with SingleParentObservable[A, A] {

  /** Async stream, so reset rank */
  override protected[airstream] val topoRank: Int = 1

  override protected[airstream] def onNext(nextValue: A, transaction: Transaction): Unit = {
    js.timers.setTimeout(delayMillis) {
      println("NEW TRX from DelayStream")
      new Transaction(fire(nextValue, _))
    }
  }
}
