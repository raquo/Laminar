package com.raquo.laminar.experimental.airstream.features

import com.raquo.laminar.experimental.airstream.core.Observable

import scala.scalajs.js

// @TODO "Sync" is confusing here, now that we have SyncObservable
trait SingleParentSyncObservable[I, +O] extends SingleParentObservable[I, O] {

  // @TODO Seems like a good default, but only for sync streams. Keep it here or move somewhere else?
  /** This only works for synchronous streams*/
  override protected[airstream] def syncDependsOn(
    otherObservable: Observable[_],
    seenObservables: js.Array[Observable[_]]
  ): Boolean = {
    if (otherObservable == parent) {
      true
    } else {
      seenObservables.push(this)
      parent.syncDependsOn(otherObservable, seenObservables)
    }
  }
}
