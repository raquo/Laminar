package com.raquo.laminar.modifiers

import com.raquo.laminar.keys.{AriaAttr, HtmlAttr, HtmlProp, Key, StyleProp, SvgAttr}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement, ReactiveSvgElement}

/** This class represents a modifier that sets a [[Key]] (e.g. an attribute or a style)
  * to a specific value on a [[El]]. [[action]] is what performs this change.
  *
  * These modifiers are not only idempotent, but are also expected to be undoable,
  * that is, calling `key := value2` will override `key := value1`. In contrast,
  * that is not the case for `cls := "class"` for example, which *adds* a class
  * instead of *setting* it. Such `cls` modifiers are [[CompositeKeySetter]], which
  * does not extend [[KeySetter]]. // #TODO the naming of these traits is confusing...
  */
class KeySetter[K <: Key, V, -El <: ReactiveElement.Base] (
  val key: K,
  val value: V,
  val action: (El, K, V) => Unit
) extends Setter[El] {

  @inline override def apply(element: El): Unit = action(element, key, value)
}

object KeySetter {

  type PropSetter[V, DomV] = KeySetter[HtmlProp[V, DomV], V, ReactiveHtmlElement.Base]

  type HtmlAttrSetter[V] = KeySetter[HtmlAttr[V], V, ReactiveHtmlElement.Base]

  type SvgAttrSetter[V] = KeySetter[SvgAttr[V], V, ReactiveSvgElement.Base]

  type AriaAttrSetter[V] = KeySetter[AriaAttr[V], V, ReactiveElement.Base]

  type StyleSetter = KeySetter[StyleProp[_], String, ReactiveHtmlElement.Base]

  // See also: CompositeKeySetter type (used for composite attributes like `cls` or `role`),
  // which is separate from KeySetter because it is a bit too dynamic to have `val value`.
}
