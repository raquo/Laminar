package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.core.{LazyObservable, MemoryObservable}
import com.raquo.laminar.experimental.airstream.ownership.Owner
import com.raquo.laminar.experimental.airstream.state.{MapState, State}

import scala.scalajs.js

// @TODO[Integrity] Careful with multiple inheritance & addObserver here
/** Signal is a lazy observable with a current value */
trait Signal[+A] extends MemoryObservable[A] with LazyObservable[A, Signal] {

  protected[this] var maybeLastSeenCurrentValue: js.UndefOr[A] = js.undefined

  def toState(implicit owner: Owner): State[A] = {
    new MapState[A, A](parent = this, project = identity, owner)
  }

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

  implicit def toTuple2Signal[A, B](signal: Signal[(A, B)]): Tuple2Signal[A, B] = {
    new Tuple2Signal(signal)
  }
}
