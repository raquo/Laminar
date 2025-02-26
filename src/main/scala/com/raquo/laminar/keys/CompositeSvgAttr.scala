package com.raquo.laminar.keys

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.nodes.ReactiveSvgElement

import scala.scalajs.js.|

class CompositeSvgAttr(
  val localName: String,
  val namespacePrefix: Option[String],
  override val separator: String
) extends CompositeKey[ReactiveSvgElement.Base] {

  /** Qualified name, including namespace */
  override val name: String = namespacePrefix.map(_ + ":" + localName).getOrElse(localName)

  val namespaceUri: Option[String] = namespacePrefix.map(SvgAttr.namespaceUri)

  override private[laminar] def getRawDomValue(
    element: ReactiveSvgElement.Base
  ): String | Unit = {
    DomApi.getAttributeRaw(element.ref, localName, namespaceUri.orNull)
  }

  override private[laminar] def setRawDomValue(
    element: ReactiveSvgElement.Base,
    value: String
  ): Unit = {
    DomApi.setAttributeRaw(
      element = element.ref,
      localName = localName,
      qualifiedName = name,
      namespaceUri = namespaceUri.orNull,
      domValue = value
    )
  }
}
