package com.raquo.laminar.experimental.airstream.features

import com.raquo.laminar.experimental.airstream.core.{InternalObserver, Observable, Observer}

/** A simple observable that only has one parent. */
trait SingleParentObservable[I, +O] extends Observable[O] with InternalObserver[I] {

  protected[this] val parent: Observable[I] // TODO[Integrity] This type was Stream before. Ok?

  override protected[this] def onStart(): Unit = {
    parent.addInternalObserver(this)
  }

  override protected[this] def onStop(): Unit = {
    parent.removeInternalObserver(this)
  }
}
