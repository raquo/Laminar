package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.{SimpleKeySetter, SimpleKeyUpdater}
import com.raquo.laminar.nodes.ReactiveElement

/**
  * This class represents a Key typically found on the left hand side of the key-value pair `key := value`
  *
  * Example would be a particular attribute or a property (without the corresponding value), e.g. "href"
  *
  * "Simple" in the name means that the key can hold only a single value at a time.
  * In contrast, [[CompositeKey]] is not a SimpleKey, it sets multiple values, and has a similar but different API.
  *
  * Hierarchy:
  *  - [[SimpleKey]]
  *     - [[SimpleAttr]]
  *        - [[HtmlAttr]], [[SvgAttr]], [[AriaAttr]]
  *     - [[HtmlProp]]
  *     - [[StyleProp]]
  *     - [[DerivedStyleProp]]
  *  - [[CompositeKey]]
  *     - [[CompositeAttr]]
  *       - [[CompositeHtmlAttr]], [[CompositeSvgAttr]]
  */
trait SimpleKey[ //
  +Self <: SimpleKey[Self, V, El],
  -V,
  -El <: ReactiveElement.Base
] { self: Self =>

  val name: String

  def :=[ThisV <: V](value: ThisV): SimpleKeySetter[Self, ThisV, El]

  @inline def apply[ThisV <: V](value: ThisV): SimpleKeySetter[Self, ThisV, El] = {
    this := value
  }

  def maybe: SimpleKey[_, Option[V], El]

  def <--[ThisV](values: Source[ThisV])(implicit ev: ThisV => V): SimpleKeyUpdater[Self, ThisV, El]

}
