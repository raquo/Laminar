package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.features.SingleParentSyncObservable
import com.raquo.laminar.experimental.airstream.core.{MemoryObservable, Observer}

// @TODO This and MapState/MapEventStream could share their logic in a `MapMemoryObservable extends MapObservable`, but I don't think it's worth it to piecemeal like that
class MapSignal[I, O](
  override protected[this] val parent: MemoryObservable[I],
  project: I => O
) extends Signal[O] with SingleParentSyncObservable[I, O] {

  override protected[this] var currentValue: O = project(parent.now())

  override protected[this] val inputObserver: Observer[I] = Observer { newParentValue =>
    fire(project(newParentValue))
  }
}
