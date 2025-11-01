package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observable
import com.raquo.airstream.ownership.DynamicSubscription
import com.raquo.laminar.keys.{CompositeKey, CompositeValueMapper}
import com.raquo.laminar.nodes.ReactiveElement

/**
  * A modifier that updates a [[CompositeKey]] from a source, e.g. `value <-- valueStream`
  */
class CompositeKeyUpdater[+K <: CompositeKey[K, El], V, -El <: ReactiveElement.Base](
  val key: K,
  val values: Observable[V],
  valueMapper: CompositeValueMapper[V]
) extends Binder[El] { self =>

  override def bind(element: El): DynamicSubscription = {
    ReactiveElement.bindFn(element, values) { value =>
      val currentNormalizedItems = element.compositeValueItems(key, reason = self)
      val nextNormalizedItems = valueMapper.toNormalizedList(value, key.separator)

      val itemsToAdd = nextNormalizedItems.filterNot(currentNormalizedItems.contains)
      val itemsToRemove = currentNormalizedItems.filterNot(nextNormalizedItems.contains)

      element.updateCompositeValue(
        key = key,
        reason = self,
        addItems = itemsToAdd,
        removeItems = itemsToRemove
      )
    }
  }
}
