package com.raquo.laminar.keys

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.nodes.ReactiveHtmlElement

import scala.scalajs.js.|

class CompositeHtmlAttr(
  override val name: String,
  override val separator: String
) extends CompositeKey[ReactiveHtmlElement.Base] {

  override private[laminar] def getRawDomValue(
    element: ReactiveHtmlElement.Base
  ): String | Unit = {
    DomApi.getAttributeRaw(element.ref, name, namespaceUri = null)
  }

  override private[laminar] def setRawDomValue(
    element: ReactiveHtmlElement.Base,
    value: String
  ): Unit = {
    DomApi.setAttributeRaw(
      element = element.ref,
      localName = name,
      qualifiedName = name,
      namespaceUri = null,
      domValue = value
    )
  }
}
