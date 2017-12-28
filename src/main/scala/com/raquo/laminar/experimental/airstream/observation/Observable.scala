package com.raquo.laminar.experimental.airstream.observation

import com.raquo.laminar.experimental.airstream.ownership.Owner
import org.scalajs.dom

import scala.scalajs.js

trait Observable[+A] {

  /** Note: This is enforced to be a set outside the type system. #performance */
  protected[this] lazy val observers: js.Array[Observer[A]] = js.Array()

  def foreach[B >: A](action: B => Unit)(implicit subscriptionOwner: Owner): Subscription[B] = {
    val observer = Observer(action)
    addObserver[B](observer)(subscriptionOwner)
  }

  def addObserver[B >: A](observer: Observer[B])(implicit subscriptionOwner: Owner): Subscription[B] = {
    val subscription = new Subscription[B](observer, this, subscriptionOwner)
    observers.push(observer)
    dom.console.log(s"Adding subscription: $subscription")
    subscription
  }

  def removeObserver[B >: A](observer: Observer[B]): Unit = {
    val index = observers.indexOf(observer)
    val alreadyRemoved = index == -1
    if (!alreadyRemoved) {
      observers.splice(index, deleteCount = 1)
    }
  }

  // @TODO[API] Maybe this method belongs to Signal? It's the only "Owned Observable" that we have, and it seems that this is the only time this is needed.
  protected def removeAllObservers(): Unit = {
    observers.length = 0 // Yes, this does what you didn't think it would.
  }
}
