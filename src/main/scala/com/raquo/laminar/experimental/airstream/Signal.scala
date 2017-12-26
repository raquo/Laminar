package com.raquo.laminar.experimental.airstream

import org.scalajs.dom

trait Signal[A] {

  protected var currentValue: A

  protected var children: Seq[ComputedSignal[_]] = Nil

  protected var observers: Seq[Observer[A]] = Nil

  def now(): A = currentValue

  //  lazy val changes: Stream[A] = ???

  def map[B](project: A => B)(implicit context: Context): MapSignal[A, B] = {
    new MapSignal(parent = this, project, context)
  }

  def foreach[B >: A](
    action: B => Unit,
    skipInitial: Boolean = false
  )(implicit context: Context): Observer[B] = {
    val observer = new Observer[B](action, context)
    dom.console.log(s"Adding observer: $observer")
    observers = observers :+ observer
    if (!skipInitial) {
      // @TODO[Integrity] should this execute immediately, or should this be appended to the end of pendingObservations if a propagation is currently running?
      observer.onNext(currentValue)
    }
    observer
  }

  // @TODO[WTF] Why can't this be simply "protected"? Because specialization?
  private[airstream] def addChild(child: ComputedSignal[_]): Unit = {
    children = children :+ child
  }

  private[airstream] def propagate(haltOnNextCombine: Boolean): Unit = {
    dom.console.log(s"> ${this}.Signal.propagate", haltOnNextCombine)
    createObservations()
    children.foreach(_.propagate(haltOnNextCombine = true))
  }

  protected def createObservations(): Unit = {
    dom.console.log(s"> $this.createObservations")
    observers.foreach { observer =>
      dom.console.log(s">> for observer: $observer")
      Airstream.addPendingObservation(currentValue, observer)
    }
  }
}

object Signal {

  def combine[A, B](
    signal1: Signal[A],
    signal2: Signal[B]
  )(
    implicit context: Context
  ): CombineSignal2[A, B] = {
    new CombineSignal2(parent1 = signal1, parent2 = signal2, context)
  }

}
