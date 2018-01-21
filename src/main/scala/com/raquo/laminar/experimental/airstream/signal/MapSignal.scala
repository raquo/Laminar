package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.features.SingleParentObservable
import com.raquo.laminar.experimental.airstream.core.{MemoryObservable, Transaction}

// @TODO This and MapState/MapEventStream could share their logic in a `MapMemoryObservable extends MapObservable`, but I don't think it's worth it to piecemeal like that
class MapSignal[I, O](
  override protected[this] val parent: MemoryObservable[I],
  project: I => O
) extends Signal[O] with SingleParentObservable[I, O] {

  override protected[this] var currentValue: O = project(parent.now())

  override protected[airstream] val topoRank: Int = parent.topoRank + 1

  override protected[airstream] def onNext(nextParentValue: I, transaction: Transaction): Unit = {
    fire(project(nextParentValue), transaction)
  }
}
