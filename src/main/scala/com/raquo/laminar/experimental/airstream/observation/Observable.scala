package com.raquo.laminar.experimental.airstream.observation

import com.raquo.laminar.experimental.airstream.ownership.Owner
import org.scalajs.dom

import scala.scalajs.js

trait Observable[A] {

  /** Note: This is enforced to be a set outside the type system. #performance */
  protected lazy val subscriptions: js.Array[Subscription[A]] = js.Array()

  def foreach(action: A => Unit)(implicit subscriptionOwner: Owner): Subscription[A] = {
    val observer = new Observer(action)
    addObserver(observer)(subscriptionOwner)
  }

  def addObserver(observer: Observer[A])(implicit subscriptionOwner: Owner): Subscription[A] = {
    val subscription = new Subscription(observer, this, subscriptionOwner)
    dom.console.log(s"Adding subscription: $subscription")
    subscription
  }

  /** This method is called when a [[Subscription]] is created. Don't call it manually. */
  private[observation] def addSubscription(subscription: Subscription[A]): Unit = {
    subscriptions.push(subscription)
  }

  /** This method is called when a [[Subscription]] is killed. Don't call it manually. */
  private[observation] def removeSubscription(subscription: Subscription[A]): Unit = {
    val index = subscriptions.indexOf(subscription)
    val alreadyRemoved = index == -1 // Just in case someone calls .kill() more than once, which should never happen
    if (!alreadyRemoved) {
      subscriptions.splice(index, deleteCount = 1)
    }
  }

  // @TODO[API] Maybe this method belongs to Signal? It's the only "Owned Observable" that we have, and it seems that this is the only time this is needed.
  protected def removeAllSubscriptions(): Unit = {
    subscriptions.length = 0 // Yes, this does what you didn't think it would.
  }
}
