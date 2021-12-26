package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.api.StringSeqValueMapper
import com.raquo.laminar.modifiers.{Binder, Setter}
import com.raquo.laminar.nodes.ReactiveElement

/** Laminar key specific to a particular set of CompositeAttr values */
class LockedCompositeKey[K, -El <: ReactiveElement.Base](
  val key: CompositeKey[K, El],
  val items: List[String]
) {

  @inline def apply(include: Boolean): Setter[El] = {
    :=(include)
  }

  def :=(include: Boolean): Setter[El] = {
    if (include) {
      key.:=(items: _*)
    } else {
      Setter.noop
    }
  }

  /** If \$include emits true, items will be added, if false, they will be removed. */
  def <--($include: Source[Boolean]): Binder[El] = {
    key <-- $include.toObservable.map(include => if (include) items else Nil)
  }
}
