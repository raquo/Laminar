package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.core.Transaction
import com.raquo.laminar.experimental.airstream.eventstream.EventStream
import com.raquo.laminar.experimental.airstream.features.SingleParentObservable

class SignalFromEventStream[A](
  override protected[this] val parent: EventStream[A],
  override protected[this] val initialValue: A
) extends Signal[A] with SingleParentObservable[A, A] {

  override protected[airstream] val topoRank: Int = parent.topoRank + 1

  override protected[airstream] def onNext(nextValue: A, transaction: Transaction): Unit = {
    fire(nextValue, transaction)
  }
}
