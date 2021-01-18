package com.raquo.laminar.keys

import com.raquo.airstream.core.Observable
import com.raquo.laminar.api.{StringSeqSeqValueMapper, StringSeqValueMapper}
import com.raquo.laminar.modifiers.{Binder, Setter}
import com.raquo.laminar.nodes.ReactiveElement

/** Laminar key specific to a particular set of CompositeAttr values */
class LockedCompositeKey[Key, -El <: ReactiveElement.Base](
  val key: CompositeKey[Key, El],
  val items: List[String]
) {

  @inline def apply(include: Boolean): Setter[El] = {
    :=(include)
  }

  def :=(include: Boolean): Setter[El] = {
    if (include) {
      key := items
    } else {
      Setter.noop
    }
  }

  /** If $$include emits true, items will be added, if false, they will be removed. */
  def <--($include: Observable[Boolean]): Binder[El] = {
    key <-- $include.map(include => if (include) items else Nil)
  }
}
