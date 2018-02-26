package com.raquo.laminar.experimental.airstream.state

import com.raquo.laminar.experimental.airstream.features.SingleParentObservable
import com.raquo.laminar.experimental.airstream.core.{MemoryObservable, Transaction}
import com.raquo.laminar.experimental.airstream.ownership.Owner

class MapState[I, O](
  override protected[this] val parent: MemoryObservable[I],
  project: I => O,
  override protected[state] val owner: Owner
) extends State[O] with SingleParentObservable[I, O] {

  override protected[airstream] val topoRank: Int = parent.topoRank + 1

  init()

  onStart()

  override protected[this] def initialValue(): O = project(parent.now())

  override protected[airstream] def onNext(nextParentValue: I, transaction: Transaction): Unit = {
    fire(project(nextParentValue), transaction)
  }
}
