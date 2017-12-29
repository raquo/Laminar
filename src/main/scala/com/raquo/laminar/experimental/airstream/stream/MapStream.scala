package com.raquo.laminar.experimental.airstream.stream

/** This stream applies a function to events fired by its parent and fires the resulting value */
class MapStream[I, O](
  override protected val parent: Stream[I],
  project: I => O
) extends SingleParentStream[I, O] {

  override protected val onParentFired: I => Unit = { newParentValue =>
    fire(project(newParentValue))
  }
}
