package com.raquo.laminar.keys

import com.raquo.laminar.DomApi
import com.raquo.laminar.nodes.ReactiveHtmlElement

import scala.scalajs.js.|

class CompositeHtmlAttr(
  override val name: String,
  override val separator: String
) extends CompositeKey[ReactiveHtmlElement.Base] {

  override private[laminar] def getRawDomValue(
    element: ReactiveHtmlElement.Base
  ): String | Unit = {
    DomApi.getHtmlAttributeRaw(element.ref, name)
  }

  override private[laminar] def setRawDomValue(
    element: ReactiveHtmlElement.Base,
    value: String
  ): Unit = {
    DomApi.setHtmlAttributeRaw(element.ref, name, value)
  }
}
