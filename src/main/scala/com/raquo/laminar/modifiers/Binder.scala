package com.raquo.laminar.modifiers

import com.raquo.airstream.ownership.DynamicSubscription
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.nodes.ReactiveElement

/** Binder is a Modifier that creates a subscription when invoked.
  * - Note, this Modifier is NOT idempotent:
  *   Calling it N times will create and bind N subscriptions
  * - `onMountBind` can take care of this, see the docs
  */
trait Binder[-El <: ReactiveElement.Base] extends Modifier[El] {

  /** This method is used by onMountBind to cancel this subscription on unmount */
  def bind(element: El): DynamicSubscription

  override def apply(element: El): Unit = {
    bind(element)
  }
}

object Binder {

  def apply[El <: ReactiveElement.Base](fn: El => DynamicSubscription): Binder[El] = {
    new Binder[El] {
      override def bind(element: El): DynamicSubscription = fn(element)
    }
  }

  def withSelf[El <: ReactiveElement.Base](fn: (El, Binder[El]) => DynamicSubscription) = {
    new Binder[El] {
      override def bind(element: El): DynamicSubscription = fn(element, this)
    }
  }
}
