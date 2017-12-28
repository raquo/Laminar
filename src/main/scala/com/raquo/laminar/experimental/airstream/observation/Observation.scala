package com.raquo.laminar.experimental.airstream.observation

/** This class encapsulates the operation of triggering an observer with a specific value.
  *
  * It is needed because [[com.raquo.laminar.experimental.airstream.signal.Signal]] propagation
  * first compiles a list of [[Observation]]s, and triggers them later, after all pending
  * signals have been resolved.
  */
class Observation[A](value: A, observer: Observer[A]) {

  def observe(): Unit = {
    observer.onNext(value)
  }
}
