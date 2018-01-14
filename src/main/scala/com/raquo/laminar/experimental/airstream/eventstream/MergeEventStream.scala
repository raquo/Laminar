package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.{Observable, Observer}

import scala.scalajs.js

// @TODO[API] Provide EventStream.toSignal(initialValue), Signal.changes and other integration methods
// @TODO[API] to make this API indirectly applicable to Signals and State

/** Stream that emit events from all of its parents.
  *
  * This feature exists only for EventStream-s because merging MemoryObservable-s
  * (Signal and State) does not make sense, conceptually.
  */
class MergeEventStream[A](
  parents: Seq[Observable[A]]
) extends EventStream[A] {

  protected[this] val inputObserver: Observer[A] = Observer { newParentValue =>
    fire(newParentValue)
  }

  override protected[airstream] def syncDependsOn(
    otherObservable: Observable[_],
    seenObservables: js.Array[Observable[_]]
  ): Boolean = {
    parents.exists { parent =>
      seenObservables.push(parent)
      if (parent == otherObservable) {
        true
      } else {
        parent.syncDependsOn(otherObservable, seenObservables)
      }
    }
  }

  override protected[this] def onStart(): Unit = {
    parents.foreach(_.addInternalObserver(inputObserver))
  }

  override protected[this] def onStop(): Unit = {
    parents.foreach(_.removeInternalObserver(inputObserver))
  }
}
