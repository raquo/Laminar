package com.raquo.laminar.experimental.airstream.state

import com.raquo.laminar.experimental.airstream.features.SingleParentSyncObservable
import com.raquo.laminar.experimental.airstream.core.Observer
import com.raquo.laminar.experimental.airstream.ownership.Owner

class MapState[I, O](
  override protected[this] val parent: State[I],
  project: I => O,
  owner: Owner
) extends State[O] with SingleParentSyncObservable[I, O] {

  override protected[this] var currentValue: O = project(parent.now())

  override protected[this] val inputObserver: Observer[I] = Observer { newParentValue =>
    fire(project(newParentValue))
  }

  override protected[this] def registerWithOwner(): Unit = {
    owner.own(this)
  }
}
