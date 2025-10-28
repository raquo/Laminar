package com.raquo.laminar.keys

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.nodes.ReactiveElement

import scala.scalajs.js.|

/** Common type of all composite (e.g. space-separated) attributes:
  *  - [[CompositeHtmlAttr]]
  *  - [[CompositeSvgAttr]]
  *
  * See also the simple keys under [[SimpleKey]].
  */
trait CompositeAttr[+Self <: CompositeAttr[Self, El], -El <: ReactiveElement.Base]
extends CompositeKey[El] { self: Self =>

  val name: String

  val localName: String

  val namespaceUri: Option[String]

  override private[laminar] def getRawDomValue(
    element: El
  ): String | Unit = {
    DomApi.getCompositeAttribute(element, this)
  }

  override private[laminar] def setRawDomValue(
    element: El,
    value: String
  ): Unit = {
    DomApi.setCompositeAttribute(element, this, value)
  }
}
