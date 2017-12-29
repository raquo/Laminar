package com.raquo.laminar.experimental.airstream.stream

/** Represents a simple stream that only has one parent.
  * The starting/stopping logic is pretty standard for this case.
  */
trait SingleParentStream[I, O] extends Stream[O] {

  protected val onParentFired: I => Unit

  protected val parent: Stream[I]

  override protected def onStart(): Unit = {
    parent.onChildStarted(onParentFired)
  }

  override protected def onStop(): Unit = {
    parent.onChildStopped(onParentFired)
  }
}
