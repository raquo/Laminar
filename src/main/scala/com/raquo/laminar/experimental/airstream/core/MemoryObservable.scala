package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.ownership.Owner

/** An observable that remembers its current value at all times. */
trait MemoryObservable[+A] extends Observable[A] {

  protected[this] var currentValue: A

  @inline def now(): A = currentValue

  // @TODO implement this
  //  lazy val changes: Stream[A] = ???


  override protected[this] def fire(nextValue: A): Unit = {
    // @TODO Should this fire if value didn't actually change?
    currentValue = nextValue
    super.fire(nextValue)
  }

  /** Note: if you want your observer to only get changes, subscribe to .changes stream instead */
  override def addObserver[B >: A](observer: Observer[B])(implicit subscriptionOwner: Owner): Subscription[B] = {
    val subscription = super.addObserver[B](observer)
    observer.onNext(currentValue) // @TODO now or later? Call notifyObserver instead or something?
    subscription
  }

  /** Note: if you want your observer to only get changes, subscribe to .changes stream instead */
  override protected[airstream] def addInternalObserver(observer: Observer[A]): Unit = {
    super.addInternalObserver(observer)
    observer.onNext(currentValue)
  }
}
