package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.api.StringSeqValueMapper
import com.raquo.laminar.modifiers.{CompositeKeySetter, KeyUpdater, Setter}
import com.raquo.laminar.nodes.ReactiveElement

/** Laminar key specific to a particular set of CompositeAttr values */
@deprecated("""LockedCompositeKey is deprecated. cls.toggle("foo") method is not necessary anymore: use cls("foo"), CompositeKeySetter now supports everything that LockedCompositeKey supported.""", since = "17.0.0-M1")
class LockedCompositeKey[K <: Key, -El <: ReactiveElement.Base](
  val key: CompositeKey[K, El],
  val items: List[String]
) {

  /** If `include` is true, the value(s) will be added, if false, they will not be added */
  @inline def apply(include: Boolean): Setter[El] = {
    :=(include)
  }

  /** If `include` is true, the value(s) will be added, if false, they will not be added */
  def :=(include: Boolean): CompositeKeySetter[K, El] = {
    if (include) {
      key.:=(items: _*)
    } else {
      key.:=()
    }
  }

  /** If the `include` observable emits true, value(s) will be added, if false, they will be removed. */
  def <--(include: Source[Boolean]): KeyUpdater[El, CompositeKey[K, El], List[String]] = {
    key <-- include.toObservable.map(include => if (include) items else Nil)
  }
}
