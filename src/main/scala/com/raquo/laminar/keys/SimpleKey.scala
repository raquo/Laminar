package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.api.L.seqToSetter
import com.raquo.laminar.modifiers.{Setter, SimpleKeySetter, SimpleKeyUpdater}
import com.raquo.laminar.nodes.ReactiveElement

// #nc TODO explain the type params
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
  *     - [[CompositeHtmlAttr]]
  *     - [[CompositeSvgAttr]]
  */
trait SimpleKey[ //
  +Self <: SimpleKey[Self, V, El],
  V,
  -El <: ReactiveElement.Base
] { self: Self =>

  val name: String

  def :=(value: V): SimpleKeySetter[Self, V, El]

  @inline def apply(value: V): SimpleKeySetter[Self, V, El] = {
    this := value
  }

  def maybe(value: Option[V]): Setter[El] = {
    seqToSetter[Option, El](value.map(v => this := v))
  }

  def <--(values: Source[V]): SimpleKeyUpdater[Self, V, El]

}
