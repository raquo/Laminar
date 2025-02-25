package com.raquo.laminar.domapi

import com.raquo.laminar.DomApi
import com.raquo.laminar.keys._
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}

import scala.scalajs.js.|

// #nc organize the rest of domapi-s (trees, events, etc.) in this package too
// #nc Name this SimpleKeyDomApi? Or not, since this domapi package is outside of Laminar terms...
//  - Does CompositeKey play into this in any way?
abstract class KeyDomApi[-K[_] <: SimpleKey[?, _, El], -El <: ReactiveElement.Base] {

  def set[V](
    element: El,
    key: K[V],
    value: V
  ): Unit

  def remove[V](
    element: El,
    key: K[V],
  ): Unit = {
    set(element, key, value = null.asInstanceOf[V])
  }

}

abstract class RWKeyDomApi[-K[_] <: SimpleKey[?, _, El], -El <: ReactiveElement.Base]
extends KeyDomApi[K, El] {

  def get[V](
    element: El,
    key: K[V]
  ): V | Unit

}

object KeyDomApi {

  object SimpleAttrDomApi extends RWKeyDomApi[SimpleAttr.Of, ReactiveElement.Base] {

    override def set[V](element: ReactiveElement.Base, key: SimpleAttr.Of[V], value: V): Unit = {
      DomApi.setAttributeRaw(
        element = element.ref,
        localName = key.localName,
        qualifiedName = key.name,
        namespaceUri = key.namespaceUri.orNull,
        domValue = key.codec.encode(value)
      )
    }

    override def get[V](element: ReactiveElement.Base, key: SimpleAttr.Of[V]): V | Unit = {
      DomApi
        .getAttributeRaw(
          element = element.ref,
          localName = key.localName,
          namespaceUri = key.namespaceUri.orNull
        )
        .map(key.codec.decode)
    }

  }

  object HtmlPropDomApi extends RWKeyDomApi[HtmlProp.Of, ReactiveHtmlElement.Base] {

    override def set[V](
      element: ReactiveHtmlElement.Base,
      key: HtmlProp[V, _],
      value: V
    ): Unit = {
      val domValue = key.codec.encode(value)
      DomApi.setHtmlPropertyRaw(element.ref, key.name, domValue)
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

