package com.raquo.laminar.modifiers

import com.raquo.laminar.PlatformSpecific.StringOr
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.keys.{AriaAttr, DerivedStyleProp, HtmlAttr, HtmlProp, SimpleKey, StyleProp, SvgAttr}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement, ReactiveSvgElement}

import scala.scalajs.js.|

/** This class represents a modifier that sets a [[SimpleKey]] (e.g. an attribute or a style)
  * to a specific value on a [[El]]. [[set]] is what performs this change.
  *
  * These modifiers are not only idempotent, but are also expected to be undoable,
  * that is, calling `key := value2` will override `key := value1`. In contrast,
  * that is not the case for `cls := "class"` for example, which *adds* a class
  * instead of *setting* it. Such `cls` modifiers are [[CompositeKeySetter]], which
  * does not extend [[SimpleKeySetter]]. // #TODO the naming of these traits is confusing...
  */
class SimpleKeySetter[ //
  +K <: SimpleKey[K, V, El],
  V,
  -El <: ReactiveElement.Base
](
  val key: K,
  val value: V,
  val set: El => Unit, // #Note: this not having K and V lets us have SimpleKeySetter covariant in K
  val remove: El => Unit // #nc[API] Do we need `remove` on key setter? If not, we can get rid of `apply`.
) extends Setter[El] {

  @inline override def apply(element: El): Unit = set(element)
}

object SimpleKeySetter {

  type OfHtmlProp[V] = SimpleKeySetter[HtmlProp[V], V, ReactiveHtmlElement.Base]

  type OfHtmlAttr[V] = SimpleKeySetter[HtmlAttr[V], V, ReactiveHtmlElement.Base]

  type OfSvgAttr[V] = SimpleKeySetter[SvgAttr[V], V, ReactiveSvgElement.Base]

  type OfAriaAttr[V] = SimpleKeySetter[AriaAttr[V], V, ReactiveElement.Base]

  type OfStyleProp[V] = StyleSetter[V]

  type OfDerivedStyleProp[V] = DerivedStyleSetter[V]

  def apply[K <: SimpleKey[K, V, El], V, El <: ReactiveElement.Base](
    key: K,
    value: V
  )(
    setOrRemoveF: (El, K, V | Null) => Unit
  ): SimpleKeySetter[K, V, El] = {
    new SimpleKeySetter[K, V, El](
      key,
      value,
      el => setOrRemoveF(el, key, value),
      el => setOrRemoveF(el, key, null)
    ) {}
  }

  class StyleSetter[V](
    override val key: StyleProp[V],
    override val value: V | String
  ) extends SimpleKeySetter[StyleProp[V], V | String, ReactiveHtmlElement.Base](
    key,
    value,
    el => DomApi.setHtmlStyle(el, key, value.asInstanceOf[V | String | Null]), // #Safe
    el => DomApi.setHtmlStyle(el, key, null)
  ) {

    lazy val cssValue: String = {
      DomApi.cssValue(value).asInstanceOf[String] // #Safe (non-Null) as long as `value` itself is not null.
    }
  }

  class DerivedStyleSetter[V](
    override val key: DerivedStyleProp[V],
    override val value: V
  ) extends SimpleKeySetter[DerivedStyleProp[V], V, ReactiveHtmlElement.Base](
    key,
    value,
    el => DomApi.setHtmlDerivedStyle(el, key, value),
    el => DomApi.setHtmlDerivedStyle(el, key, null.asInstanceOf[V])
  ) {

    lazy val cssValue: String = key.encode(value)
  }
}
