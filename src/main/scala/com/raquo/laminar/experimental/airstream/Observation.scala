package com.raquo.laminar.experimental.airstream

/** Observation encapsulates the operation of triggering an observer with a specific value */
class Observation[A](value: A, observer: Observer[A]) {

  def observe(): Unit = {
    observer.onNext(value)
  }
}
