package com.raquo.laminar.keys

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}

import scala.scalajs.js.|

/** An attribute that we can add multiple values to, often in the shape of
  * space-separated strings, e.g. `class` (`cls` in Scala).
  *
  * Includes:
  *  - HTML-specific ones like `role`, as well as
  *  - Global attrs that apply to all element types, like `class`
  *
  * Note: [[CompositeAttr]] does not support SVG namespaces because
  * there simply aren't any composite attrs with a non-null namespace.
  *
  * See also the simple keys (attrs / props / etc.) under [[SimpleKey]].
  */
class CompositeAttr[-El <: ReactiveElement.Base](
  override val name: String,
  override val separator: String
)
extends CompositeKey[CompositeAttr[El], El] {

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

object CompositeAttr {

  type Base = CompositeAttr[ReactiveElement.Base]

  type HtmlCompositeAttr = CompositeAttr[ReactiveHtmlElement.Base]

  // #Note Composite SVG attrs don't actually exist...
  // type SvgCompositeAttr = CompositeAttr[ReactiveSvgElement.Base]
}
