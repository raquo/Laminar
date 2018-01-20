package com.raquo.laminar.experimental.airstream.state

import com.raquo.laminar.experimental.airstream.core.{MemoryObservable, Transaction}
import com.raquo.laminar.experimental.airstream.ownership.Owned

// @TODO
/** State is an eager, owned observable */
trait State[+A] extends MemoryObservable[A] with Owned {

  protected var isDead: Boolean = false

  onStart() // State starts itself, it does not need any dependencies to run

  /** State propagates only if its value has changed */ // @TODO Should this also apply to MemoryStream-s?
  override protected[this] def fire(nextValue: A, transaction: Transaction): Unit = {
    if (!isDead) { // ??
      if (nextValue != currentValue) {
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
