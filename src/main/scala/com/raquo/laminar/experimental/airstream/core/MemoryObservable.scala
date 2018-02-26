package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.eventstream.{EventStream, MapEventStream}
import com.raquo.laminar.experimental.airstream.ownership.Owner

/** An observable that remembers its current value */
trait MemoryObservable[+A] extends Observable[A] {

  /** We don't want public access to this for lazy MemoryObservables (Signals) because if you called .now() on a Signal,
    * you wouldn't know if you were getting the updated value or not, because that would depend on whether said Signal
    * has observers.
    *
    * Use EventStream.sample and EventStream.withCurrentValueOf if you need this on a Signal.
    */
  protected[airstream] def now(): A

  /** Evaluate initial value of this [[MemoryObservable]].
    * This method should only be called once, when first needed
    */
  protected[this] def initialValue(): A

  /** Update the current value of this [[MemoryObservable]] */
  protected[this] def setCurrentValue(newValue: A): Unit

  def changes: EventStream[A] = new MapEventStream[A, A](parent = this, project = identity)

  /** Note: if you want your observer to only get changes, subscribe to .changes stream instead */
  override def addObserver(observer: Observer[A])(implicit owner: Owner): Subscription = {
    val subscription = super.addObserver(observer)
    observer.onNext(now()) // send current value immediately
    subscription
  }

  override protected[this] def fire(nextValue: A, transaction: Transaction): Unit = {
    // @TODO[API] The reason we might not want this for Signal is because Signal's now() is not guaranteed to be fresh. Not sure if we care.
//    if (nextValue != currentValue) {  // @TODO we want this check here, right? There's another check like this in State
    setCurrentValue(nextValue)
    super.fire(nextValue, transaction)
//    }
  }
}
