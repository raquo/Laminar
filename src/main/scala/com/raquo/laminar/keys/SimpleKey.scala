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
  *        - [[GlobalAttr]]
  *          - [[AriaAttr]]
  *        - [[HtmlAttr]]
  *        - [[SvgAttr]]
  *        - [[MathMlAttr]]
  *     - [[HtmlProp]]
  *     - [[StyleProp]]
  *     - [[DerivedStyleProp]]
  *  - [[CompositeKey]]
  *     - [[CompositeAttr]]
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

  def maybe: SimpleKey[_ <: SimpleKey[_, Option[V], El], Option[V], El]

  // #Note: ThisV <: V bound prevents Scala 3 from widening string literal
  //  union types (e.g. "a" | "b") to String during type inference.
  //  The implicit ev is still needed for Scala 2 compatibility where union
  //  subtyping is emulated via implicit conversions.
  def <--[ThisV <: V](values: Source[ThisV])(implicit ev: ThisV => V): SimpleKeyUpdater[Self, ThisV, El]

}
