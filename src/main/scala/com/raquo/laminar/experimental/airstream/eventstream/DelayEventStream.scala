package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.features.SingleParentObservable
import com.raquo.laminar.experimental.airstream.core.{Observable, Observer}

import scala.scalajs.js
import scala.scalajs.js.timers.setTimeout

class DelayEventStream[A](
  override protected val parent: EventStream[A],
  delayMillis: Int
) extends EventStream[A] with SingleParentObservable[A, A] {

  override protected[this] val inputObserver: Observer[A] = Observer { newParentValue =>
    setTimeout(interval = delayMillis)(fire(newParentValue))
  }

  /** Async stream does not depend on anything synchronously. */
  override protected[airstream] def syncDependsOn(otherObservable: Observable[_], seenObservables: js.Array[Observable[_]]): Boolean = {
    seenObservables.push(this)
    false
  }
}
