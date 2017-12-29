package com.raquo.laminar.experimental.airstream.observation

import com.raquo.laminar.experimental.airstream.ownership.Owner
import org.scalajs.dom

import scala.scalajs.js

/** This trait represents a reactive value that can be subscribed to. */
trait Observable[+A] {

  /** Note: Observer can be added more than once to an Observable.
    * If so, it will observe each event as many times as it was added.
    */
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

  /** Note: To completely disconnect an Observer from this Observable,
    * you need to remove it as many times as you added it to this Observable.
    *
    * @return whether observer was removed (`false` if it wasn't subscribed to this observable)
    */
  def removeObserver[B >: A](observer: Observer[B]): Boolean = {
    val index = observers.indexOf(observer)
    val shouldRemove = index != -1
    if (shouldRemove) {
      observers.splice(index, deleteCount = 1)
    }
    shouldRemove
  }

  // @TODO[API] Maybe this method belongs to Signal? It's the only "Owned Observable" that we have, and it seems that this is the only time this is needed.
  protected def removeAllObservers(): Unit = {
    observers.length = 0 // Yes, this does what you didn't think it would
  }

  // @TODO Should Signals use or override this method?
  protected[this] def notifyObservers(nextValue: A): Unit = {
    observers.foreach(_.onNext(nextValue))
  }
}
