package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.ownership.{Owned, Owner}

abstract class Subscription private[core] (
  override protected[this] val owner: Owner
) extends Owned {

  init()

  /** Manually unsubscribe */
  override def kill(): Unit = super.kill()
}

object Subscription {

  /** Note: this method exists as the only way to build subscriptions because we don't want to add [[A]]
    * as a type param to [[Subscription]] because it complicates covariance of [[Observable]].
    */
  private[core] def apply[A](
    observer: Observer[A],
    observable: Observable[A],
    owner: Owner
  ): Subscription = {
    new Subscription(owner) {

      override protected[this] def onKilled(): Unit = {
        observable.removeObserver(observer)
      }
    }
  }
}
