package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.core.{LazyObservable, MemoryObservable, Observer, Subscription}
import com.raquo.laminar.experimental.airstream.eventstream.{ChangesEventStream, EventStream}
import com.raquo.laminar.experimental.airstream.ownership.Owner

import scala.scalajs.js

// @TODO[Integrity] Check inheritance order, could be important (add Observer is defined in both MO and LO traits)
/** Signal is a lazy observable with a current value */
trait Signal[+A] extends MemoryObservable[A] with LazyObservable[A, Signal] {

  protected[this] var maybeLastSeenCurrentValue: js.UndefOr[A] = js.undefined

  protected[this] def initialValue(): A

  // @TODO[API] Move out into MemoryObservable?
  lazy val changes: EventStream[A] = new ChangesEventStream[A](parent = this)

  override def map[B](project: A => B): Signal[B] = {
    new MapSignal(parent = this, project)
  }

  override def compose[B](operator: Signal[A] => Signal[B]): Signal[B] = {
    operator(this)
  }

  override def combineWith[AA >: A, B](otherSignal: Signal[B]): CombineSignal2[AA, B, (AA, B)] = {
    new CombineSignal2(
      parent1 = this,
      parent2 = otherSignal,
      combinator = (_, _)
    )
  }

  /** Note: if you want your observer to only get changes, subscribe to .changes stream instead */
  override def addObserver(observer: Observer[A])(implicit subscriptionOwner: Owner): Subscription = {
    val subscription = super.addObserver(observer)
    observer.onNext(now())
    subscription
  }

  /** Initial value is only evaluated if/when needed (when there are observers) */
  override protected[airstream] def now(): A = {
    maybeLastSeenCurrentValue.getOrElse {
      val currentValue = initialValue()
      setCurrentValue(currentValue)
      currentValue
    }
  }

  override protected[this] def setCurrentValue(newValue: A): Unit = {
    maybeLastSeenCurrentValue = js.defined(newValue)
  }
}

// @TODO all.map/etc should also return a MemoryStream... But how? CanBuildFrom..?
object Signal {

//  def combine[A, B](
//    signal1: Signal[A],
//    signal2: Signal[B]
//  )(
//    implicit combineSignalOwner: Owner
//  ): CombineSignal2[A, B] = {
//    new CombineSignal2(parent1 = signal1, parent2 = signal2, combineSignalOwner)
//  }

}
