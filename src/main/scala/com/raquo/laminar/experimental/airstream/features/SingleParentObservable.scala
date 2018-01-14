package com.raquo.laminar.experimental.airstream.features

import com.raquo.laminar.experimental.airstream.core.{Observable, Observer}

/** A simple observable that only has one parent. */
trait SingleParentObservable[I, +O] extends Observable[O] {

  /** An internal observer that we subscribe to the parent observable
    * when this stream starts, and unsubscribe when this stream stops.
    *
    * This part is customized in each concrete subclass of this trait.
    */
  protected[this] val inputObserver: Observer[I]

  protected[this] val parent: Observable[I] // TODO[Integrity] This type was Stream before. Ok?

  override protected[this] def onStart(): Unit = {
    parent.addInternalObserver(inputObserver)
  }

  override protected[this] def onStop(): Unit = {
    parent.removeInternalObserver(inputObserver)
  }
}
