package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.features.SingleParentSyncObservable
import com.raquo.laminar.experimental.airstream.core.Observer

/** This stream fires a subset of the events fired by its parent */
class FilterEventStream[A](
  override protected val parent: EventStream[A],
  passes: A => Boolean
) extends EventStream[A] with SingleParentSyncObservable[A, A] {

  override protected[this] val inputObserver: Observer[A] = Observer { newParentValue =>
    if (passes(newParentValue)) {
      fire(newParentValue)
    }
  }
}
