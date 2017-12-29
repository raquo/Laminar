package com.raquo.laminar.experimental.airstream.stream

import com.raquo.laminar.experimental.airstream.observation.Observer

/** Represents a simple stream that only has one parent.
  * The starting/stopping logic is pretty standard for this case.
  */
trait SingleParentStream[I, O] extends Stream[O] {

  /** An internal observer that we subscribe to the parent observable
    * when this stream starts, and unsubscribe when this stream stops.
    *
    * This part is customized in each concrete subclass of this trait.
    */
  protected[this] val inputObserver: Observer[I]

  protected[this] val parent: Stream[I]

  override protected[this] def onStart(): Unit = {
    parent.addChildObserver(inputObserver)
  }

  override protected[this] def onStop(): Unit = {
    parent.removeChildObserver(inputObserver)
  }
}
