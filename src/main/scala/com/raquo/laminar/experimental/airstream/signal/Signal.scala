package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.observation.{Observable, Observer, Subscription}
import com.raquo.laminar.experimental.airstream.ownership.{Owned, Owner}
import com.raquo.laminar.experimental.airstream.propagation.Parent
import org.scalajs.dom

trait Signal[A] extends Observable[A] with Parent[ComputedSignal[_]] with Owned {

  protected[this] var currentValue: A

  @inline def now(): A = currentValue

  // @TODO implement this when we have streams
  //  lazy val changes: Stream[A] = ???

  def map[B](project: A => B)(implicit mapSignalOwner: Owner): MapSignal[A, B] = {
    new MapSignal(parent = this, project, mapSignalOwner)
  }

  /** Note: if you want your observer to only get changes, subscribe to .changes stream instead */
  override def addObserver[B >: A](observer: Observer[B])(implicit subscriptionOwner: Owner): Subscription[B] = {
    val subscription = super.addObserver[B](observer)
    observer.onNext(currentValue)
    subscription
  }

  private[airstream] def propagate(haltOnNextCombine: Boolean): Unit = {
    dom.console.log(s"> ${this}.Signal.propagate", haltOnNextCombine)
    createObservations()
    linkedChildren.foreach(_.propagate(haltOnNextCombine = true))
  }

  protected def createObservations(): Unit = {
    dom.console.log(s"> $this.createObservations")
    observers.foreach { observer =>
      dom.console.log(s">> for observer: $observer")
      Propagation.addPendingObservation(currentValue, observer)
    }
  }

  /** When a Signal is killed, future updates to it are not expected,
    * and if they do accidentally happen, they must not affect anything
    * outside of the signal.
    *
    * We remove children to:
    * - prevent propagation of accidental updates after the signal was killed
    * - remove references to children for GC purposes (strictly speaking, that is extraneous)
    *
    * We remove subscriptions to:
    * - prevent side effects from firing
    */
  override private[airstream] def kill(): Unit = {
    unlinkAllChildren()
    removeAllObservers()
  }
}

object Signal {

  def combine[A, B](
    signal1: Signal[A],
    signal2: Signal[B]
  )(
    implicit combineSignalOwner: Owner
  ): CombineSignal2[A, B] = {
    new CombineSignal2(parent1 = signal1, parent2 = signal2, combineSignalOwner)
  }

}
