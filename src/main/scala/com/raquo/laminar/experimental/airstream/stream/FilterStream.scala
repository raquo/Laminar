package com.raquo.laminar.experimental.airstream.stream

/** This stream fires a subset of the events fired by its parent */
class FilterStream[A](
  override protected val parent: Stream[A],
  passes: A => Boolean
) extends SingleParentStream[A, A] {

  override protected val onParentFired: A => Unit = { newParentValue =>
    if (passes(newParentValue)) {
      fire(newParentValue)
    }
  }
}
