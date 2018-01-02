package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.state.Var

// @TODO update description since this now has another purpose
/** This class encapsulates the operation of triggering an observer with a specific value.
  *
  * It is needed because [[com.raquo.laminar.experimental.airstream.signal.Signal]] propagation
  * first compiles a list of [[Observation]]s, and triggers them later, after all pending
  * signals have been resolved.
  */
class Observation[A](observer: Observer[A], value: A) {

  def observe(): Unit = {
    observer.onNext(value)
  }
}

object Observation {

  // Implicit defs allow for Var.set(myVar1 -> value1, myVar2 -> value2) syntax

  implicit def tupleToObservation[A](tuple: (Var[A], A)): Observation[A] = {
    new Observation(observer = tuple._1.toObserver, value = tuple._2)
  }

  implicit def tuplesToAssignments[A](tuples: Seq[(Var[A], A)]): Seq[Observation[A]] = {
    tuples.map(tupleToObservation)
  }

}
