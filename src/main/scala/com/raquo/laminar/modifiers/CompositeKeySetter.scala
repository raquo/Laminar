package com.raquo.laminar.modifiers

import com.raquo.laminar.keys.{CompositeKey, Key}
import com.raquo.laminar.nodes.ReactiveElement

/**
  * This is like [[KeySetter]], but for composite attributes like `cls` and `role`.
  *
  * CompositeKeySetter can not be a subtype of KeySetter because we can not implement
  * `val value` – the actual value to be set is dynamic, depending on the current
  * value of the element's composite attribute.
  *
  * Also, if you call `cls := "class2"` after calling `cls := "class1"`, you end up
  * with two classes instead of just "class2", which is different fro [[KeySetter]]
  * semantics.
  *
  * Note: for dynamic subscriptions (<--), we use [[KeyUpdater]] for all keys including
  * composite attributes.
  */
class CompositeKeySetter[K <: Key, -El <: ReactiveElement.Base](
  val key: CompositeKey[K, El],
  val itemsToAdd: List[String],
  val itemsToRemove: List[String]
) extends Setter[El] {

  override def apply(element: El): Unit = {
    if (itemsToAdd.nonEmpty || itemsToRemove.nonEmpty) {
      element.updateCompositeValue(
        key = key,
        reason = null, // @Note using null to avoid keeping a reference to this setter
        addItems = itemsToAdd,
        removeItems = itemsToRemove
      )
    }
  }
}
