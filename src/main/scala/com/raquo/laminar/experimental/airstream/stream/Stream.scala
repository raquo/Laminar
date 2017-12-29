package com.raquo.laminar.experimental.airstream.stream

import com.raquo.laminar.experimental.airstream.observation.{Observable, Observer, Subscription}
import com.raquo.laminar.experimental.airstream.ownership.Owner

class Stream[+A] extends Observable[A] {

  // @TODO[API] All those onStart/onStop methods could be in their own LazyObservable class

  /** Basic idea: Stream only holds references to those children that have any observers
    * (either directly on themselves, or on any of their descendants). What this achieves:
    * - Stream only propagates its value to children that (directly or not) have observers
    * - Stream calculates its value only once regardless of how many observers / children it has)
    * (so, all streams are "hot" observables)
    * - Stream doesn't hold references to Streams that no one observes, allowing those Streams
    * to be garbage collected if they are otherwise unreachable (which they should become
    * when their subscriptions are killed by their owners)
    */

  def map[B](project: A => B): Stream[B] = {
    new MapStream(this, project)
  }

  def filter(passes: A => Boolean): Stream[A] = {
    new FilterStream(this, passes)
  }

  def flatten[B](implicit ev: A <:< Stream[B]): Stream[B] = ???

  def compose[B](operator: Stream[A] => Stream[B]): Stream[B] = {
    operator(this)
  }

  protected[this] def fire(nextValue: A): Unit = {
    childObservers.foreach(_.onNext(nextValue))
    notifyExternalObservers(nextValue) // @TODO When should this happen? Before or after propagation?
  }

  // @TODO should onStart / onStop be part of Observable? Or are those specifically an APi for a Lazy Observable? Which is what a Stream is, essentially?

  /** This method is fired when this stream gets its first observer,
    * directly or indirectly (via child streams).
    *
    * Before this method is called:
    * - 1) This stream has no direct observers
    * - 2) None of the streams that depend on this stream have observers
    * - 3) Item (2) above is true for all streams depend on this stream
    *
    * This method is called when any of these conditions become false (often together at the same time)
    */
  protected[this] def onStart(): Unit = ()

  /** This method is fired when this stream loses its last observer,
    * including indirect ones (observers of child streams)
    *
    * Before this method is called:
    * - 1) This stream has observers, or
    * - 2) At least one stream that depends on this stream has observers, or
    * - 3) Item (2) above is true for all streams depend on this stream
    *
    * This method is called when any of these conditions become false
    */
  protected[this] def onStop(): Unit = ()

  override def addObserver[B >: A](observer: Observer[B])(implicit subscriptionOwner: Owner): Subscription[B] = {
    val subscription = super.addObserver[B](observer)
    maybeStart()
    subscription
  }

  /** Note: To completely disconnect an Observer from this Observable,
    * you need to remove it as many times as you added it to this Observable.
    *
    * @return whether observer was removed (`false` if it wasn't subscribed to this observable)
    */
  override def removeObserver[B >: A](observer: Observer[B]): Boolean = {
    val removed = super.removeObserver(observer)
    if (removed) {
      maybeStop()
    }
    removed
  }

  /** Child stream calls this to declare that it was started */
  override protected[airstream] def addChildObserver(observer: Observer[A]): Unit = {
    super.addChildObserver(observer)
    maybeStart()
  }

  /** Child stream calls this to declare that it was stopped */
  override protected[airstream] def removeChildObserver(observer: Observer[A]): Boolean = {
    val removed = super.removeChildObserver(observer)
    if (removed) {
      maybeStop()
    }
    removed
  }

  private[this] def maybeStart(): Unit = {
    val isStarting = numAllObservers == 1
    if (isStarting) {
      onStart()
    }
  }

  private[this] def maybeStop(): Unit = {
    val isStopping = numAllObservers == 0
    if (isStopping) {
      onStop()
    }
  }

  private[this] def numAllObservers: Int = {
    externalObservers.length + childObservers.length
  }
}

object Stream {

  def combine[A, B](
    stream1: Stream[A],
    stream2: Stream[B]
  ): Stream[(A, B)] = {
    new CombineStream2(stream1, stream2)
  }
}
