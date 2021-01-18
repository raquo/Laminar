package com.raquo.laminar.modifiers

import com.raquo.domtypes.generic.keys.Key
import com.raquo.laminar.nodes.ReactiveElement

/** This class represents a [[Modifier]] that sets a [[Key]] (e.g. an attribute or a style)
  * to a specific value on a [[El]]. [[action]] is what performs this change. */
class KeySetter[K <: Key, V, El <: ReactiveElement.Base] (
  val key: K,
  val value: V,
  val action: (El, K, V) => Unit
) extends Setter[El] {

  @inline override def apply(element: El): Unit = action(element, key, value)
}
