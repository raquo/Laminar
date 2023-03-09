package com.raquo.laminar.modifiers

import com.raquo.laminar.nodes.ReactiveElement

/** This type represents a modifier that sets a "property" of an element.
  *
  * It could be an html attribute, an event prop, or even a custom focus prop,
  * the main constraint is that it should be idempotent, so that applying it
  * several times in a row would produce the same effect as applying it once.
  *
  * That way we can provide it to onMountSet { c => setter } and expect
  * things to work if the element is mounted several times. However, note that
  * [[CompositeKeySetter]] has special behaviour.
  */
trait Setter[-El <: ReactiveElement.Base] extends Modifier[El]

object Setter {

  type Base = Setter[ReactiveElement.Base]

  val empty: Setter.Base = new Setter[ReactiveElement.Base] {}

  def apply[El <: ReactiveElement.Base](fn: El => Unit): Setter[El] = {
    new Setter[El] {
      override def apply(element: El): Unit = fn(element)
    }
  }
}
