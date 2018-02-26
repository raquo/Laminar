package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.ownership.Owner

/** LazyObservable only starts when it gets its first observer (internal or external),
  * and stops when it loses its last observer (again, internal or external).
  *
  * Stream and Signal are lazy observables. State is not.
  */
trait LazyObservable[+A] extends Observable[A] {

  /** Basic idea: Lazy Observable only holds references to those children that have any observers
    * (either directly on themselves, or on any of their descendants). What this achieves:
    * - Stream only propagates its value to children that (directly or not) have observers
    * - Stream calculates its value only once regardless of how many observers / children it has)
    * (so, all streams are "hot" observables)
    * - Stream doesn't hold references to Streams that no one observes, allowing those Streams
    * to be garbage collected if they are otherwise unreachable (which they should become
    * when their subscriptions are killed by their owners)
    */

  def map[B](project: A => B): LazyObservable[B]

  protected[this] def isStarted: Boolean = numAllObservers > 0

  override def toLazy: this.type = this

  override def addObserver(observer: Observer[A])(implicit owner: Owner): Subscription = {
    val subscription = super.addObserver(observer)
    maybeStart()
    subscription
  }

  override protected[airstream] def removeExternalObserverNow(observer: Observer[A]): Boolean = {
    val removed = super.removeExternalObserverNow(observer)
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
  override protected[airstream] def removeInternalObserverNow(observer: InternalObserver[A]): Boolean = {
    val removed = super.removeInternalObserverNow(observer)
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

