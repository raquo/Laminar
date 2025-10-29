package com.raquo.laminar.domapi

import com.raquo.laminar.keys._
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}

import scala.scalajs.js.|

trait DomKeys {

  protected val raw: DomKeysRaw

  def getAttribute[V, El <: ReactiveElement.Base](
    element: El,
    attr: SimpleAttr[_, V, El]
  ): V | Unit = {
    val domValue = raw.getAttribute(element.ref, attr.localName, attr.namespaceUri.orNull[String])
    domValue.map(attr.codec.decode)
  }

  def setAttribute[ThisV <: V, V, El <: ReactiveElement.Base](
    element: El,
    attr: SimpleAttr[_, V, El],
    value: ThisV | Null
  ): Unit = {
    val domValue = if (value == null) {
      null
    } else {
      attr.codec.encode(value.asInstanceOf[ThisV]) // #Safe because null check above
    }
    raw.setAttribute(
      element = element.ref,
      localName = attr.localName,
      qualifiedName = attr.name,
      namespaceUri = attr.namespaceUri.orNull[String],
      domValue = domValue
    )
  }

  def getHtmlProperty[V, El <: ReactiveHtmlElement.Base](
    element: El,
    prop: HtmlProp[V]
  ): V | Unit = {
    raw.getHtmlProperty(element.ref, prop.name).map(prop.codec.decode)
  }

  def setHtmlProperty[ThisV <: V, V, El <: ReactiveHtmlElement.Base](
    element: El,
    prop: HtmlProp[V],
    value: ThisV | Null // #nc why do we need ThisV?
  ): Unit = {
    val domValue = if (value == null) {
      null
    } else {
      prop.codec.encode(value.asInstanceOf[ThisV]) // #Safe because null check above
    }
    raw.setHtmlProperty(
      element = element.ref,
      name = prop.name,
      value = domValue
    )
  }

  /** If you don't have a String value, pass `prop(scalaValue).cssValue` to value`. */
  def setHtmlStyle[V](
    element: ReactiveHtmlElement.Base,
    prop: StyleProp[V],
    value: String | Null
  ): Unit = {
    raw.setHtmlStyle(
      element = element.ref,
      styleCssName = prop.name,
      prefixes = prop.prefixes,
      styleValue = value
    )
  }

  def setHtmlDerivedStyle[V](
    element: ReactiveHtmlElement.Base,
    prop: DerivedStyleProp[V],
    value: V | Null
  ): Unit = {
    val encodedValue = if (value == null) {
      null
    } else {
      prop.encode(value.asInstanceOf[V]) // #Safe because null check above
    }
    raw.setHtmlStyle(
      element = element.ref,
      styleCssName = prop.name,
      prefixes = prop.key.prefixes,
      styleValue = encodedValue
    )
  }

  def getCompositeAttribute[El <: ReactiveElement.Base](
    element: El,
    attr: CompositeAttr[_, El]
  ): String | Unit =
    raw.getAttribute(element.ref, attr.localName, attr.namespaceUri.orNull[String])

  def setCompositeAttribute[El <: ReactiveElement.Base](
    element: El,
    attr: CompositeAttr[_, El],
    value: String | Null
  ): Unit = {
    raw.setAttribute(
      element = element.ref,
      localName = attr.localName,
      qualifiedName = attr.name,
      namespaceUri = attr.namespaceUri.orNull[String],
      domValue = value
    )
  }

  /** Stringifies the provided value for use in CSS.
    * #Note: this can potentially be applied twice (see usages), it's mostly a type helper.
    */
  def cssValue(value: Any): String | Null = {
    if (value == null) {
      null
    } else {
      // @TODO[Performance,Minor] not sure if converting to string is needed.
      //  - According to the API, yes, but in practice browsers are ok with raw values too it seems.
      //  - Given how rare Double / Int style props are, I think runtime type matching would be less efficient than string.toString
      value.toString
    }
  }

}
