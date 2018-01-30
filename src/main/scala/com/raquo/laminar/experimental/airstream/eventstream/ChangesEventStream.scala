package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.{MemoryObservable, Transaction}
import com.raquo.laminar.experimental.airstream.features.SingleParentObservable

// @TODO[Performance] we could reuse MapEventStream for this if we relax its parent signature. Will slightly reduce LOC / bundle size
class ChangesEventStream[A](
  override protected[this] val parent: MemoryObservable[A]
) extends EventStream[A] with SingleParentObservable[A, A] {

  override protected[airstream] val topoRank: Int = parent.topoRank + 1

  override protected[airstream] def onNext(nextValue: A, transaction: Transaction): Unit = {
    fire(nextValue, transaction)
  }
}
