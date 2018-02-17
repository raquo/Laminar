package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.eventstream.{EventStream, FlattenEventStream}
import com.raquo.laminar.experimental.airstream.ownership.{Owned, Owner}
import com.raquo.laminar.experimental.airstream.state.State
import com.raquo.laminar.experimental.airstream.util.GlobalCounter
import org.scalajs.dom

import scala.scalajs.js

/** This trait represents a reactive value that can be subscribed to. */
trait Observable[+A] {

  // @TODO remove id? Why did I even add it?
  val id: Int = Observable.nextId()

  protected[airstream] val topoRank: Int

  /** Note: Observer can be added more than once to an Observable.
    * If so, it will observe each event as many times as it was added.
    */
  protected[this] lazy val externalObservers: js.Array[Observer[A]] = js.Array()

  /** Note: This is enforced to be a Set outside of the type system #performance */
  protected[this] val internalObservers: js.Array[InternalObserver[A]] = js.Array()

  def flatten[B](implicit evidence: Observable[A] <:< Observable[EventStream[B]]): EventStream[B] = {
    new FlattenEventStream[B](parent = evidence(this))
  }

  /** Get a lazy observable that emits the same values as this one (assuming it has observers, as usual)
    *
    * This is useful when you want to map over an Observable of an unknown type.
    *
    * Note: [[Observable]] itself has no "map" method because mapping over [[State]]
    * without expecting / accounting for State's eagerness can result in memory leaks. See README.
    */
  def toLazy: LazyObservable[A]

  def foreach(onNext: A => Unit)(implicit owner: Owner): Subscription = {
    val observer = Observer(onNext)
    addObserver(observer)(owner)
  }

  /** And an external observer */
  def addObserver(observer: Observer[A])(implicit owner: Owner): Subscription = {
    val subscription = Subscription(observer, this, owner)
    externalObservers.push(observer)
    dom.console.log(s"Adding subscription: $subscription")
    subscription
  }

  /** Schedule an external observer to be removed in the next transaction.
    * This will still happen synchronously, but will not interfere with
    * iteration over the observables' lists of observers during the current
    * transaction.
    *
    * Note: To completely disconnect an Observer from this Observable,
    * you need to remove it as many times as you added it to this Observable.
    */
  @inline def removeObserver(observer: Observer[A]): Unit = {
    Transaction.removeExternalObserver(this, observer)
  }

  // @TODO Why does simple "protected" not work? Specialization?

  /** Child stream calls this to declare that it was started */
  protected[airstream] def addInternalObserver(observer: InternalObserver[A]): Unit = {
    internalObservers.push(observer)
  }

//  @inline protected[airstream] def removeInternalObserver(observer: InternalObserver[A]): Unit = {
//    Transaction.removeInternalObserver(observable = this, observer)
//  }

  protected[airstream] def removeInternalObserverNow(observer: InternalObserver[A]): Boolean = {
    val index = internalObservers.indexOf(observer)
    val shouldRemove = index != -1
    if (shouldRemove) {
      internalObservers.splice(index, deleteCount = 1)
    }
    shouldRemove
  }

  /** @return whether observer was removed (`false` if it wasn't subscribed to this observable) */
  protected[airstream] def removeExternalObserverNow(observer: Observer[A]): Boolean = {
    val index = externalObservers.indexOf(observer)
    val shouldRemove = index != -1
    if (shouldRemove) {
      externalObservers.splice(index, deleteCount = 1)
    }
    shouldRemove
  }

  // @TODO These two methods need updated description because I'm getting them to work for non-lazy observables as well

  /** This method is fired when this observable starts working (listening for parent events and/or firing its own events)
    *
    * The above semantic is universal, but different observables fit [[onStart]] differently in their lifecycle:
    * - [[State]] is an eager observable, it calls [[onStart]] on initialization.
    * - [[LazyObservable]] (Stream or Signal) calls [[onStart]] when it gets its first observer (internal or external)
    *
    * So, a [[State]] observable calls [[onStart]] only once in its existence, whereas a [[LazyObservable]] calls it
    * potentially multiple times, the second time being after it has stopped (see [[onStop]]).
    */
  @inline protected[this] def onStart(): Unit = ()

  /** This method is fired when this observable stops working (listening for parent events and/or firing its own events)
    *
    * The above semantic is universal, but different observables fit [[onStop]] differently in their lifecycle:
    * - [[State]] is an eager, [[Owned]] observable. It calls [[onStop]] when its [[Owner]] decides to [[State.kill]] it
    * - [[LazyObservable]] (Stream or Signal) calls [[onStop]] when it loses its last observer (internal or external)
    *
    * So, a [[State]] observable calls [[onStop]] only once in its existence. After that, the observable is disabled and
    * will never start working again. On the other hand, a [[LazyObservable]] calls [[onStop]] potentially multiple
    * times, the second time being after it has started again (see [[onStart]]).
    */
  @inline protected[this] def onStop(): Unit = ()

  // @TODO[API] It is rather curious/unintuitive that firing external observers first seems to make more sense. Think about it some more.
  protected[this] def fire(nextValue: A, transaction: Transaction): Unit = {
    // Note: Removal of observers is always scheduled for a new transaction, so the iteration here is safe

    var index = 0
    while (index < externalObservers.length) {
      externalObservers(index).onNext(nextValue)
      index += 1
    }

    index = 0
    while (index < internalObservers.length) {
      internalObservers(index).onNext(nextValue, transaction)
      index += 1
    }
  }
}

object Observable extends GlobalCounter
