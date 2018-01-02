package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.features.SingleParentSyncObservable
import com.raquo.laminar.experimental.airstream.core.Observer

/** This stream applies a `project` function to events fired by its parent and fires the resulting value */
class MapEventStream[I, O](
  override protected val parent: EventStream[I],
  project: I => O
) extends EventStream[O] with SingleParentSyncObservable[I, O] {

  override protected[this] val inputObserver: Observer[I] = Observer { newParentValue =>
    fire(project(newParentValue))
  }

  // @TODO add map2 method
}
