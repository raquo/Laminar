package com.raquo.laminar.modifiers

import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.nodes.ReactiveElement

/** This type represents a modifier that sets a "property" of an element.
  *
  * It could be an html attribute, an event prop, or even a custom focus prop,
  * the main constraint is that it should be idempotent, so that applying it
  * several times in a row would produce the same effect as applying it once.
  *
  * That way we can provide it to onMountSet { c => setter } and expect
  * things to work if the element is mounted several times.
  */
trait Setter[-El <: ReactiveElement.Base] extends Modifier[El]

object Setter {

  // @TODO[API] do we need this?
  val noop: Setter[ReactiveElement.Base] = Setter(_ => ())

  def apply[El <: ReactiveElement.Base](fn: El => Unit): Setter[El] = {
    new Setter[El] {
      override def apply(element: El): Unit = fn(element)
    }
  }
}
