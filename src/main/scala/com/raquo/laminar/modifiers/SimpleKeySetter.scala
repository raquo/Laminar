package com.raquo.laminar.modifiers

import com.raquo.laminar.DomApi
import com.raquo.laminar.keys.{AriaAttr, DerivedStyleProp, HtmlAttr, HtmlProp, SimpleKey, StyleProp, SvgAttr}
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base
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
trait SimpleKeySetter[K <: SimpleKey[V, DomV, El], V, DomV, -El <: ReactiveElement.Base]
extends Setter[El] {

  val key: K

  val value: V

  def set(el: El): Unit

  def unset(el: El): Unit

  @inline override def apply(element: El): Unit = set(element)
}

object SimpleKeySetter {

  type Of[V, DomV, -El <: ReactiveElement.Base] = SimpleKeySetter[? <: SimpleKey[V, DomV, El], V, DomV, El]

  class PropSetter[V, DomV](
    override val key: HtmlProp[V, DomV],
    override val value: V
  ) extends SimpleKeySetter[HtmlProp[V, DomV], V, DomV, ReactiveHtmlElement.Base] {

    override def set(el: ReactiveHtmlElement.Base): Unit = {
      DomApi.setHtmlProperty(el, key, value)
    }

    override def unset(el: Base): Unit = {
      DomApi.removeHtmlProperty(el, key)
    }
  }

  class HtmlAttrSetter[V](
    override val key: HtmlAttr[V],
    override val value: V
  ) extends SimpleKeySetter[HtmlAttr[V], V, String, ReactiveHtmlElement.Base] {

    override def set(el: ReactiveHtmlElement.Base): Unit = {
      DomApi.setHtmlAttribute(el, key, value)
    }

    override def unset(el: Base): Unit = {
      DomApi.removeHtmlAttribute(el, key)
    }
  }

  class SvgAttrSetter[V](
    override val key: SvgAttr[V],
    override val value: V
  ) extends SimpleKeySetter[SvgAttr[V], V, String, ReactiveSvgElement.Base] {

    override def set(el: ReactiveSvgElement.Base): Unit = {
      DomApi.setSvgAttribute(el, key, value)
    }

    override def unset(el: ReactiveSvgElement.Base): Unit = {
      DomApi.removeSvgAttribute(el, key)
    }
  }

  class AriaAttrSetter[V](
    override val key: AriaAttr[V],
    override val value: V
  ) extends SimpleKeySetter[AriaAttr[V], V, String, ReactiveElement.Base] {

    override def set(el: ReactiveElement.Base): Unit = {
      DomApi.setAriaAttribute(el, key, value)
    }

    override def unset(el: ReactiveElement.Base): Unit = {
      DomApi.removeAriaAttribute(el, key)
    }
  }

  class StyleSetter[V](
    override val key: StyleProp[V],
    override val value: V | String
  ) extends SimpleKeySetter[StyleProp[V], V | String, String, ReactiveHtmlElement.Base] {

    // #TODO[API] Is this a long term solution?
    lazy val cssValue: String = DomApi.cssValue(value)

    override def set(el: Base): Unit = {
      DomApi.setHtmlStyle(el, key, value)
    }

    override def unset(el: Base): Unit = {
      DomApi.removeHtmlStyle(el, key)
    }
  }

  class DerivedStyleSetter[V](
    override val key: DerivedStyleProp[V],
    override val value: V
  ) extends SimpleKeySetter[DerivedStyleProp[V], V, String, ReactiveHtmlElement.Base] {

    lazy val cssValue: String = key.encode(value)

    override def set(el: Base): Unit = {
      DomApi.setHtmlStringStyle(el, key.key, cssValue)
    }

    override def unset(el: Base): Unit = {
      DomApi.removeHtmlStyle(el, key.key)
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
