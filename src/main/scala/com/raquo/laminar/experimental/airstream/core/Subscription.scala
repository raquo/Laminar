package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.ownership.{Owned, Owner}

/** This should only be instantiated in [[Observable.addObserver]] */
class Subscription[A] private[airstream] (
  val observer: Observer[A],
  val observable: Observable[A],
  override val owner: Owner
) extends Owned {

  // @TODO Consider making this public as an escape hatch, maybe requiring some bogus "I know what I'm doing" param
  /** This method is private because you should not be killing subscriptions manually,
    * you should create an [[Owner]] that does this for you.
    */
  override protected[airstream] def kill(): Unit = {
    // This method should not have any other logic. [[Observable]] is the one
    // responsible for handling all aspects of subscription management.
    observable.removeObserver(this.observer)
  }
}
