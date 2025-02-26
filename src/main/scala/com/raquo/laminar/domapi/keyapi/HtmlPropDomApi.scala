package com.raquo.laminar.domapi.keyapi

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.keys.HtmlProp
import com.raquo.laminar.nodes.ReactiveHtmlElement

import scala.scalajs.js.|

object HtmlPropDomApi extends RWDomKeyApi[HtmlProp.Of, ReactiveHtmlElement.Base] {

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
