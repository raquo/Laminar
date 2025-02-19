package com.raquo.laminar.domapi

import com.raquo.laminar.DomApi
import com.raquo.laminar.keys._
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement, ReactiveSvgElement}

import scala.scalajs.js.|

// #nc >>> Can we abstract over attributes? I don't want almost-identical implementations for html, svg, aria, and soon math attributes.
// #nc organize the rest of domapi-s (trees, events, etc.) in this package too
// #nc Name this SimpleKeyDomApi? Or not, since this domapi package is outside of Laminar terms...
//  - Does CompositeKey play into this in any way?
abstract class KeyDomApi[K[_] <: SimpleKey[?, _, El], -El <: ReactiveElement.Base] {

  def set[V](
    element: El,
    key: K[V],
    value: V
  ): Unit

  def remove[V](
    element: El,
    key: K[V],
  ): Unit

}

abstract class RWKeyDomApi[K[_] <: SimpleKey[?, _, El], El <: ReactiveElement.Base]
extends KeyDomApi[K, El] {

  def get[V](
    element: El,
    key: K[V]
  ): V | Unit

}

object KeyDomApi {

  object HtmlPropDomApi extends RWKeyDomApi[HtmlProp.Of, ReactiveHtmlElement.Base] {

    override def set[V](
      element: ReactiveHtmlElement.Base,
      key: HtmlProp[V, _],
      value: V
    ): Unit = {
      DomApi.setHtmlPropertyRaw(element.ref, key.name, key.codec.encode(value))
    }

    override def remove[V](
      element: ReactiveHtmlElement.Base,
      key: HtmlProp[V, _]
    ): Unit = {
      DomApi.removeHtmlPropertyRaw(element.ref, key.name, key.reflectedAttrName)
    }

    override def get[V](
      element: ReactiveHtmlElement.Base,
      key: HtmlProp[V, _]
    ): V | Unit = {
      DomApi.getHtmlPropertyRaw(element.ref, key.name).map(key.codec.decode)
    }
  }

  object HtmlAttrDomApi extends RWKeyDomApi[HtmlAttr, ReactiveHtmlElement.Base] {

    override def set[V](
      element: ReactiveHtmlElement.Base,
      key: HtmlAttr[V],
      value: V
    ): Unit = {
      DomApi.setHtmlAttributeRaw(element.ref, key.name, key.codec.encode(value))
    }

    override def remove[V](
      element: ReactiveHtmlElement.Base,
      key: HtmlAttr[V]
    ): Unit = {
      DomApi.setHtmlAttributeRaw(element.ref, key.name, domValue = null)
    }

    override def get[V](
      element: ReactiveHtmlElement.Base,
      key: HtmlAttr[V]
    ): V | Unit = {
      DomApi.getHtmlAttributeRaw(element.ref, key.name).map(key.codec.decode)
    }
  }

  object SvgAttrDomApi extends RWKeyDomApi[SvgAttr, ReactiveSvgElement.Base] {

    override def set[V](
      element: ReactiveSvgElement.Base,
      key: SvgAttr[V],
      value: V
    ): Unit = {
      DomApi.setSvgAttributeRaw(
        element = element.ref,
        localName = key.localName,
        qualifiedName = key.name,
        namespaceUri = key.namespaceUri.orNull,
        domValue = key.codec.encode(value)
      )
    }

    override def remove[V](
      element: ReactiveSvgElement.Base,
      key: SvgAttr[V]
    ): Unit = {
      DomApi.setSvgAttributeRaw(
        element = element.ref,
        localName = key.localName,
        qualifiedName = key.name,
        namespaceUri = key.namespaceUri.orNull,
        domValue = null
      )
    }

    override def get[V](
      element: ReactiveSvgElement.Base,
      key: SvgAttr[V]
    ): V | Unit = {
      DomApi.getSvgAttributeRaw(
        element = element.ref,
        localName = key.localName,
        namespaceUri = key.namespaceUri.orNull
      ).map(key.codec.decode)
    }
  }

  object AriaAttrDomApi extends RWKeyDomApi[AriaAttr, ReactiveElement.Base] {

    override def set[V](
      element: ReactiveElement.Base,
      key: AriaAttr[V],
      value: V
    ): Unit = {
      DomApi.setAriaAttributeRaw(element.ref, key.name, key.codec.encode(value))
    }

    override def remove[V](
      element: ReactiveElement.Base,
      key: AriaAttr[V]
    ): Unit = {
      DomApi.setAriaAttributeRaw(element.ref, key.name, domValue = null)
    }

    override def get[V](
      element: ReactiveElement.Base,
      key: AriaAttr[V]
    ): V | Unit = {
      DomApi.getAriaAttributeRaw(
        element = element.ref,
        name = key.name
      ).map(key.codec.decode)
    }
  }

  object StylePropDomApi extends KeyDomApi[StyleProp, ReactiveHtmlElement.Base] {

    override def set[V](
      element: ReactiveHtmlElement.Base,
      key: StyleProp[V],
      value: V
    ): Unit = {
      DomApi.setHtmlStyleRaw(
        element = element.ref,
        styleCssName = key.name,
        prefixes = key.prefixes,
        styleValue = DomApi.cssValue(value) //key.encode(value) // #nc
      )
    }

    override def remove[V](
      element: ReactiveHtmlElement.Base,
      key: StyleProp[V]
    ): Unit = {
      DomApi.setHtmlStyleRaw(
        element = element.ref,
        styleCssName = key.name,
        prefixes = key.prefixes,
        styleValue = null
      )
    }

    // #nc TODO add getInline & getComputed?

    // override def get[V](
    //   element: ReactiveHtmlElement.Base,
    //   key: StyleProp[V]
    // ): V | Unit = {
    //   DomApi.getHtmlStyleRaw(element.ref, key.name)
    //   ??? // #nc we can't encode styles – may need special treatment
    // }
  }

  object DerivedStylePropDomApi extends KeyDomApi[DerivedStyleProp, ReactiveHtmlElement.Base] {

    override def set[V](
      element: ReactiveHtmlElement.Base,
      key: DerivedStyleProp[V],
      value: V
    ): Unit = {
      DomApi.setHtmlStyleRaw(
        element = element.ref,
        styleCssName = key.name,
        prefixes = key.key.prefixes,
        styleValue = key.encode(value)
      )
    }

    override def remove[V](
      element: ReactiveHtmlElement.Base,
      key: DerivedStyleProp[V]
    ): Unit = {
      DomApi.setHtmlStyleRaw(
        element = element.ref,
        styleCssName = key.name,
        prefixes = key.key.prefixes,
        styleValue = null
      )
    }

    // #nc TODO add getInline & getComputed?

    // override def get[V](
    //   element: ReactiveHtmlElement.Base,
    //   key: StyleProp[V]
    // ): V | Unit = {
    //   DomApi.getHtmlStyleRaw(element.ref, key.name)
    //   ??? // #nc we can't encode styles – may need special treatment
    // }
  }
}

