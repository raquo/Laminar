package com.raquo.laminar.experimental.airstream.state

import com.raquo.laminar.experimental.airstream.core.{MemoryObservable, Transaction}
import com.raquo.laminar.experimental.airstream.ownership.Owned

// @TODO
/** State is an eager, owned observable */
trait State[+A] extends MemoryObservable[A] with Owned {

  protected[this] var currentValue: A = initialValue()

  protected var isDead: Boolean = false

  onStart() // State starts itself, it does not need any dependencies to run

  /** State is evaluated eagerly, so we can have this public */
  @inline override def now(): A = currentValue

  protected[this] def initialValue(): A

  override protected[this] def setCurrentValue(newValue: A): Unit = {
    currentValue = newValue
  }

  /** State propagates only if its value has changed */ // @TODO Should this also apply to MemoryStream-s?
  override protected[this] def fire(nextValue: A, transaction: Transaction): Unit = {
    if (!isDead) { // ??
      if (nextValue != now()) { // @TODO we already have this check in MemoryObservable
        super.fire(nextValue, transaction)
      }
    }
  }

  override protected[this] def onStart(): Unit = {
    isDead = false // @TODO Technically this is not needed
  }

  override protected[this] def onStop(): Unit = {
    isDead = true //
  }

  override private[airstream] def kill(): Unit = {
    onStop() // @TODO
  }
}
