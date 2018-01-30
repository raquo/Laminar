package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.features.SingleParentObservable
import com.raquo.laminar.experimental.airstream.core.{MemoryObservable, Transaction}

class MapSignal[I, O](
  override protected[this] val parent: MemoryObservable[I],
  project: I => O
) extends Signal[O] with SingleParentObservable[I, O] {

  override protected[airstream] val topoRank: Int = parent.topoRank + 1

  override protected[airstream] def onNext(nextParentValue: I, transaction: Transaction): Unit = {
    fire(project(nextParentValue), transaction)
  }

  override protected[this] def initialValue(): O = project(parent.now())
}
