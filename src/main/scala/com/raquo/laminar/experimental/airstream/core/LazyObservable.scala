package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.ownership.Owner

/** LazyObservable only starts when it gets its first observer (internal or external),
  * and stops when it loses its last observer (again, internal or external).
  *
  * Stream and Signal are lazy observables. State is not.
  */
trait LazyObservable[+A, S[+_] <: LazyObservable[_, S]] extends Observable[A] {

  /** Basic idea: Lazy Observable only holds references to those children that have any observers
    * (either directly on themselves, or on any of their descendants). What this achieves:
    * - Stream only propagates its value to children that (directly or not) have observers
    * - Stream calculates its value only once regardless of how many observers / children it has)
    * (so, all streams are "hot" observables)
    * - Stream doesn't hold references to Streams that no one observes, allowing those Streams
    * to be garbage collected if they are otherwise unreachable (which they should become
    * when their subscriptions are killed by their owners)
    */

  // @TODO[API] Should LazyObservable have these abstract methods? Event streams and Signals are pretty different beasts.
  // @TODO[API] Probably yes, because some things like <-- don't care much whether the observable has a current value

  def map[B](project: A => B): S[B]

  def compose[B](operator: S[A] => S[B]): S[B]

  def combineWith[AA >: A, B](otherObservable: S[B]): S[(AA, B)]

  protected[this] def isStarted: Boolean = numAllObservers > 0

  override def addObserver(observer: Observer[A])(implicit subscriptionOwner: Owner): Subscription = {
    val subscription = super.addObserver(observer)
    maybeStart()
    subscription
  }

  /** Note: To completely disconnect an Observer from this Observable,
    * you need to remove it as many times as you added it to this Observable.
    *
    * @return whether observer was removed (`false` if it wasn't subscribed to this observable)
    */
  override def removeObserver(observer: Observer[A]): Boolean = {
    val removed = super.removeObserver(observer)
    if (removed) {
      maybeStop()
    }
    removed
  }

  /** Child observable should call this method on this lazy observable when it was started.
    * This lazy observable calls [[onStart]] if this action has given it its first observer (internal or external).
    * See docs for [[Observable.onStart]]
    */
  override protected[airstream] def addInternalObserver(observer: InternalObserver[A]): Unit = {
    super.addInternalObserver(observer)
    maybeStart()
  }

  /** Child observable should call this method on this lazy observable when it was stopped.
    * This lazy observable calls [[onStop]] if this action has removed its last observer (internal or external).
    * See also docs for [[Observable.onStop]]
    */
  override protected[airstream] def removeInternalObserver(observer: InternalObserver[A]): Boolean = {
    val removed = super.removeInternalObserver(observer)
    if (removed) {
      maybeStop()
    }
    removed
  }

  private[this] def maybeStart(): Unit = {
    val isStarting = numAllObservers == 1
    if (isStarting) {
      // We've just added first observer
      onStart()
    }
  }

  private[this] def maybeStop(): Unit = {
    if (!isStarted) {
      // We've just removed last observer
      onStop()
    }
  }

  private[this] def numAllObservers: Int = {
    externalObservers.length + internalObservers.length
  }
}

