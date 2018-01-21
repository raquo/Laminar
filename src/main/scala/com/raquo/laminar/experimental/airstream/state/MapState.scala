package com.raquo.laminar.experimental.airstream.state

import com.raquo.laminar.experimental.airstream.features.SingleParentObservable
import com.raquo.laminar.experimental.airstream.core.{Observer, Transaction}
import com.raquo.laminar.experimental.airstream.ownership.Owner

class MapState[I, O](
  override protected[this] val parent: State[I],
  project: I => O,
  owner: Owner
) extends State[O] with SingleParentObservable[I, O] {

  override protected[this] var currentValue: O = project(parent.now())

  override protected[airstream] val topoRank: Int = parent.topoRank + 1

  override protected[airstream] def onNext(nextParentValue: I, transaction: Transaction): Unit = {
    fire(project(nextParentValue), transaction)
  }

  override protected[this] def registerWithOwner(): Unit = {
    owner.own(this)
  }
}
