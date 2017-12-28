package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.observation.{Observable, Observer, Subscription}
import com.raquo.laminar.experimental.airstream.ownership.{Owned, Owner}
import org.scalajs.dom

trait Signal[A] extends Observable[A] with Owned {

  protected[this] var currentValue: A

  // @TODO "children" functionality probably belongs in the Observable trait (yes, really, even despite different memory management for streams)
  // @TODO this is "protected" only for testing, otherwise could be private. Maybe use proper test tools
  /** Note: This is enforced to be a set outside the type system (@TODO: or is it?). #performance */
  protected[this] var children: Seq[ComputedSignal[_]] = Nil

  @inline def now(): A = currentValue

  // @TODO implement this when we have streams
  //  lazy val changes: Stream[A] = ???

  def map[B](project: A => B)(implicit mapSignalOwner: Owner): MapSignal[A, B] = {
    new MapSignal(parent = this, project, mapSignalOwner)
  }

  // @TODO[WTF] Why can't this be simply "protected"? Because specialization?
  private[airstream] def addChild(child: ComputedSignal[_]): Unit = {
    children = children :+ child
  }

  private[airstream] def removeChild(child: ComputedSignal[_]): Unit = {
    val index = children.indexOf(child)
    if (index != -1) {
      val parts = children.splitAt(index)
      parts._1 ++ parts._2
    }
  }

  private[airstream] def removeAllChildren(): Unit = {
    children = Nil
  }

  /** Note: if you want your observer to only get changes, subscribe to .changes stream instead */
  override def addObserver(observer: Observer[A])(implicit subscriptionOwner: Owner): Subscription[A] = {
    val subscription = super.addObserver(observer)
    observer.onNext(currentValue)
    subscription
  }

  private[airstream] def propagate(haltOnNextCombine: Boolean): Unit = {
    dom.console.log(s"> ${this}.Signal.propagate", haltOnNextCombine)
    createObservations()
    children.foreach(_.propagate(haltOnNextCombine = true))
  }

  protected def createObservations(): Unit = {
    dom.console.log(s"> $this.createObservations")
    subscriptions.foreach { subscription =>
      dom.console.log(s">> for observer: $subscription")
      Propagation.addPendingObservation(currentValue, subscription.observer)
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
    removeAllChildren()
    removeAllSubscriptions()
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
