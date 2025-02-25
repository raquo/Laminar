package com.raquo.laminar.modifiers

import com.raquo.laminar.domapi.KeyDomApi.DerivedStylePropDomApi
import com.raquo.laminar.keys.{DerivedStyleProp, SimpleKey}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}

/** This class represents a modifier that sets a [[SimpleKey]] (e.g. an attribute or a style)
  * to a specific value on a [[El]]. [[set]] is what performs this change.
  *
  * These modifiers are not only idempotent, but are also expected to be undoable,
  * that is, calling `key := value2` will override `key := value1`. In contrast,
  * that is not the case for `cls := "class"` for example, which *adds* a class
  * instead of *setting* it. Such `cls` modifiers are [[CompositeKeySetter]], which
  * does not extend [[SimpleKeySetter]]. // #TODO the naming of these traits is confusing...
  */
trait SimpleKeySetter[V, DomV, -El <: ReactiveElement.Base]
extends Setter[El] {

  val key: SimpleKey[V, DomV, El]

  val value: V

  // #nc implement this to get rid of cssValue? Or is it too confusing?
  // def domValue: DomV

  def set(el: El): Unit

  def remove(el: El): Unit

  @inline override def apply(element: El): Unit = set(element)
}

object SimpleKeySetter {

  // #TODO[Naming,Org] This used to be a more specific type,
  //  but now it's just a type alias that does not involve StyleProp-s
  //  in principle, it shares this type with e.g. HtmlAttr-s.
  type StyleSetter[V] = SimpleKeySetter[V, String, ReactiveHtmlElement.Base]

  // type Of[V, DomV, -El <: ReactiveElement.Base] = SimpleKeySetter[V, DomV, El]

  // #nc is this salvageable?
  // def apply[V, DomV, El <: ReactiveElement.Base, K[_] <: SimpleKey[?, DomV, El]](
  //   domApi: KeyDomApi[K, El],
  //   _key: K[V],
  //   _value: V
  // ): SimpleKeySetter[V, DomV, El] = {
  //   new SimpleKeySetter[V, DomV, El] {
  //     override val key: K[V] = _key
  //     override val value: V = _value
  //
  //     override def set(el: El): Unit = {
  //       domApi.set[V](el, _key, _value)
  //     }
  //
  //     override def unset(el: El): Unit = {
  //       domApi.remove[V](el, _key)
  //     }
  //   }
  // }

  // class StyleSetter[V](
  //   override val key: StyleProp[V],
  //   override val value: V
  // ) extends SimpleKeySetter[V, String, ReactiveHtmlElement.Base] {
  //
  //   // #nc I don't think we need this anymore
  //   // #TODO[API] Is this a long term solution?
  //   lazy val cssValue: String = DomApi.cssValue(value)
  //
  //   override def set(el: Base): Unit = {
  //     DomApi.setHtmlStyle(el, key, value)
  //   }
  //
  //   override def unset(el: Base): Unit = {
  //     DomApi.removeHtmlStyle(el, key)
  //   }
  // }

  // object StyleSetter {
  //
  //   type SS[V] = StyleSetter[V | String]
  // }

  class DerivedStyleSetter[V](
    override val key: DerivedStyleProp[V],
    override val value: V
  ) extends SimpleKeySetter[V, String, ReactiveHtmlElement.Base] {

    lazy val cssValue: String = key.encode(value)

    override def set(el: ReactiveHtmlElement.Base): Unit = {
      DerivedStylePropDomApi.set(el, key, value)
    }

    override def remove(el: ReactiveHtmlElement.Base): Unit = {
      DerivedStylePropDomApi.remove(el, key)
    }
  }

  // type PropSetter[V, DomV] = KeySetter[HtmlProp[V, DomV], V, DomV, ReactiveHtmlElement.Base]

  // type HtmlAttrSetter[V] = KeySetter[HtmlAttr[V], V, String, ReactiveHtmlElement.Base]

  // type SvgAttrSetter[V] = KeySetter[SvgAttr[V], V, String, ReactiveSvgElement.Base]

  // type AriaAttrSetter[V] = KeySetter[AriaAttr[V], V, String, ReactiveElement.Base]

  // type StyleSetter[V] = KeySetter[StyleProp[V], V | String, String, ReactiveHtmlElement.Base]

  // type DerivedStyleSetter[InputV] = SimpleKeySetter[DerivedStyleProp[InputV], InputV, String, ReactiveHtmlElement.Base]

  // See also: CompositeKeySetter type (used for composite attributes like `cls` or `role`),
  // which is separate from KeySetter because it is a bit too dynamic to have `val value`.
}
