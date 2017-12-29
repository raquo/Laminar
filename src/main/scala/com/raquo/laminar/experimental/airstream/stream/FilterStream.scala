package com.raquo.laminar.experimental.airstream.stream

import com.raquo.laminar.experimental.airstream.observation.Observer

/** This stream fires a subset of the events fired by its parent */
class FilterStream[A](
  override protected val parent: Stream[A],
  passes: A => Boolean
) extends SingleParentStream[A, A] {

  override protected[this] val inputObserver: Observer[A] = Observer { newParentValue =>
    if (passes(newParentValue)) {
      fire(newParentValue)
    }
  }
}
