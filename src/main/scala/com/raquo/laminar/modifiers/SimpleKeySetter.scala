package com.raquo.laminar.modifiers

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.keys.{AriaAttr, DerivedStyleProp, GlobalAttr, HtmlAttr, HtmlProp, MathMlAttr, SimpleKey, StyleProp, SvgAttr}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement, ReactiveMathMlElement, ReactiveSvgElement}

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
  +K <: SimpleKey[K, _, El],
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

  type HtmlPropSetter[V] = SimpleKeySetter[HtmlProp[V], V, ReactiveHtmlElement.Base]

  type HtmlAttrSetter[V] = SimpleKeySetter[HtmlAttr[V], V, ReactiveHtmlElement.Base]

  type SvgAttrSetter[V] = SimpleKeySetter[SvgAttr[V], V, ReactiveSvgElement.Base]

  type MathMlAttrSetter[V] = SimpleKeySetter[MathMlAttr[V], V, ReactiveMathMlElement]

  type GlobalAttrSetter[V] = SimpleKeySetter[GlobalAttr[V], V, ReactiveElement.Base]

  type AriaAttrSetter[V] = SimpleKeySetter[AriaAttr[V], V, ReactiveElement.Base]

  type StylePropSetter[V, ThisV] = StyleSetter[V, ThisV]

  type DerivedStylePropSetter[V, ThisV <: V] = DerivedStyleSetter[V, ThisV]

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

  // #Note – constructor is private, because we expect ThisV <: V,
  //  however this is exceptionally hard to properly encode in types
  //  in Scala 2, because our `V`-s are union types like Int | String,
  //  and in Scala 2 those unions are not real, they are implemented
  //  in userland by Scala.js, and their subtyping is faked via
  //  implicit conversions, which do not work on type relationships,
  //  they only work on values, i.e. they don't help us fix compiler
  //  errors caused by passing bad types in type params.
  // #Note – this bit of unsafety allows us to create StyleSetter-s
  //  with a specific type of their value...
  // #nc ^^^ Why do we actually need that? We got rid of String | String,
  //  so what do we stand to gain? Double-check, because ThisV being separate
  //  is causing us to use weird types and overloads, which is more problematic
  // #nc BUT before then, implement comprehensive syntax tests for styles,
  //  focusing on issues with unions.
  class StyleSetter[V, ThisV] private[laminar] (
    override val key: StyleProp[V],
    override val value: ThisV
  ) extends SimpleKeySetter[StyleProp[V], ThisV, ReactiveElement.Base](
    key,
    value,
    el => DomApi.setStyle(el, key, DomApi.cssValue(value)),
    el => DomApi.setStyle(el, key, null)
  ) {

    lazy val cssValue: String = {
      DomApi.cssValue(value).asInstanceOf[String] // #Safe (non-Null) as long as `value` itself is not null.
    }
  }

  // #Note – this doesn't really need ThisV, except to satisfy SimpleKey := type constraints (which exist for StyleSetter)
  class DerivedStyleSetter[V, ThisV <: V](
    override val key: DerivedStyleProp[V],
    override val value: ThisV
  ) extends SimpleKeySetter[DerivedStyleProp[V], ThisV, ReactiveElement.Base](
    key,
    value,
    el => DomApi.setDerivedStyle(el, key, value),
    el => DomApi.setDerivedStyle(el, key, null.asInstanceOf[ThisV])
  ) {

    lazy val cssValue: String = key.encode(value)
  }
}
