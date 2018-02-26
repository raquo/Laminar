package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.{Observable, Transaction}
import com.raquo.laminar.experimental.airstream.features.SingleParentObservable

/** This stream applies a `project` function to events fired by its parent and fires the resulting value */
class MapEventStream[I, O](
  override protected val parent: Observable[I],
  project: I => O
) extends EventStream[O] with SingleParentObservable[I, O] {

  override protected[airstream] val topoRank: Int = parent.topoRank + 1

  override protected[airstream] def onNext(nextParentValue: I, transaction: Transaction): Unit = {
    fire(project(nextParentValue), transaction)
  }
}
