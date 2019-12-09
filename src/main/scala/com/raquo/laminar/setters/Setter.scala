package com.raquo.laminar.setters

import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.Key

/** This class represents a [[Modifier]] that sets a [[Key]] (e.g. an attribute or a style)
  * to a specific value on a [[Node]]. [[action]] is what performs this change. */
class Setter[K <: Key, V, Node] (
  val key: K,
  val value: V,
  val action: (Node, K, V) => Unit
) extends Modifier[Node] {

  @inline override def apply(node: Node): Unit = action(node, key, value)
}
