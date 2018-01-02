package com.raquo.laminar.experimental.airstream.features

import com.raquo.laminar.experimental.airstream.core.{Observable, Observer}

import scala.scalajs.js

/** This observable combines two parent observables into an observable of tuples of their values.
  * This observable is not a Subject, it works like any other stream.
  */
trait CombineObservable2[A, B] extends Observable[(A, B)] {

  protected[this] val parent1: Observable[A]
  protected[this] val parent2: Observable[B]

  protected[this] val parent1Observer: Observer[A]
  protected[this] val parent2Observer: Observer[B]

  override protected[airstream] def syncDependsOn(
    otherObservable: Observable[_],
    seenObservables: js.Array[Observable[_]]
  ): Boolean = {
    if (otherObservable == parent1 || otherObservable == parent2) {
      true
    } else {
      seenObservables.push(this)
      parent1.syncDependsOn(otherObservable, seenObservables) && parent2.syncDependsOn(otherObservable, seenObservables)
    }
  }

  // @TODO This is almost the same as CombineStream2
  override protected[this] def onStart(): Unit = {
    parent1.addInternalObserver(parent1Observer)
    parent2.addInternalObserver(parent2Observer)
  }

  // @TODO This is almost the same as CombineStream2
  override protected[this] def onStop(): Unit = {
    parent1.removeInternalObserver(parent1Observer)
    parent2.removeInternalObserver(parent2Observer)
  }
}
