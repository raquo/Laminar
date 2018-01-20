package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.features.SingleParentSyncObservable
import com.raquo.laminar.experimental.airstream.core.{Observer, Transaction}

/** This stream applies a `project` function to events fired by its parent and fires the resulting value */
class MapEventStream[I, O](
  override protected val parent: EventStream[I],
  project: I => O
) extends EventStream[O] with SingleParentSyncObservable[I, O] {

  override protected[airstream] def onNext(nextParentValue: I, transaction: Transaction): Unit = {
    fire(project(nextParentValue), transaction)
  }
}
