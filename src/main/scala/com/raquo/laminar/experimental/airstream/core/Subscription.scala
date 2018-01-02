package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.ownership.{Owned, Owner}

trait Subscription extends Owned

object Subscription {

  /** Subscription should only be instantiated in [[Observable.addObserver]] */
  def apply[A](observer: Observer[A], observable: Observable[A], owner: Owner): Subscription = {
    new Subscription {

      override protected[this] def registerWithOwner(): Unit = {
        owner.own(this)
      }

      // @TODO Consider making this public as an escape hatch, maybe requiring some bogus "I know what I'm doing" param
      /** This method is private because you should not be killing subscriptions manually,
        * you should create an [[Owner]] that does this for you.
        */
      override private[airstream] def kill(): Unit = {
        observable.removeObserver(observer)
      }
    }
  }
}
