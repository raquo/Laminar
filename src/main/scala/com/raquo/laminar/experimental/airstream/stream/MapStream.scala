package com.raquo.laminar.experimental.airstream.stream

import com.raquo.laminar.experimental.airstream.observation.Observer

/** This stream applies a `project` function to events fired by its parent and fires the resulting value */
class MapStream[I, O](
  override protected val parent: Stream[I],
  project: I => O
) extends SingleParentStream[I, O] {

  override protected[this] val inputObserver: Observer[I] = Observer { newParentValue =>
    fire(project(newParentValue))
  }
}
