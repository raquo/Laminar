package com.raquo.laminar.experimental.airstream.observation

import com.raquo.laminar.experimental.airstream.ownership.{Owned, Owner}

class Subscription[A](
  val observer: Observer[A],
  val observable: Observable[A],
  override val owner: Owner
) extends Owned {

  observable.addSubscription(this)

  // @TODO Consider making this public as an escape hatch, maybe requiring some bogus "I know what I'm doing" param
  /** This method is private because you should not be killing subscriptions manually,
    * you should create an Owner that does this for you.
    */
  override private[airstream] def kill(): Unit = {
    // This method should not have any other logic. [[Observable]] is the one
    // responsible for handling all aspects of subscription management.
    observable.removeSubscription(this)
  }

}
