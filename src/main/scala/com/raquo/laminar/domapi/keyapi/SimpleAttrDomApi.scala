package com.raquo.laminar.domapi.keyapi

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.keys.SimpleAttr
import com.raquo.laminar.nodes.ReactiveElement

import scala.scalajs.js.|

object SimpleAttrDomApi extends RWDomKeyApi[SimpleAttr.Of, ReactiveElement.Base] {

  override def set[V](
    element: ReactiveElement.Base,
    key: SimpleAttr.Of[V],
    value: V
  ): Unit = {
    DomApi.setAttributeRaw(
      element = element.ref,
      localName = key.localName,
      qualifiedName = key.name,
      namespaceUri = key.namespaceUri.orNull,
      domValue = key.codec.encode(value)
    )
  }

  override def get[V](
    element: ReactiveElement.Base,
    key: SimpleAttr.Of[V]
  ): V | Unit = {
    DomApi
      .getAttributeRaw(
        element = element.ref,
        localName = key.localName,
        namespaceUri = key.namespaceUri.orNull
      )
      .map(key.codec.decode)
  }

}
