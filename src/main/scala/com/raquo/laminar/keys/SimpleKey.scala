package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.modifiers.{SimpleKeySetter, SimpleKeyUpdater}
import com.raquo.laminar.nodes.ReactiveElement

import scala.scalajs.js.|

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
  V,
  -El <: ReactiveElement.Base
] { self: Self =>

  val name: String

  // `ThisV` here is needed primarily for good typing of keywords like opacity.none
  def :=[ThisV <: V](value: ThisV): SimpleKeySetter[Self, ThisV, El] =
    SimpleKeySetter(this, value)((el, _, value) => set(el, value))

  @inline def apply[ThisV <: V](value: ThisV): SimpleKeySetter[Self, ThisV, El] = {
    this := value
  }

  def <--(values: Source[V]): SimpleKeyUpdater[Self, V, El] =
    new SimpleKeyUpdater(
      key = this,
      values = values.toObservable,
      update = (key, value) => set(key, value)
    )

  def maybe: SimpleKey[_ <: SimpleKey[_, Option[V], El], Option[V], El]

  /** `null` means "unset this key" */
  protected def set(el: El, value: V | Null): Unit

}

object SimpleKey {

  implicit class SimpleKeyExt[ //
    +Self <: SimpleKey[Self, V, El],
    V,
    -El <: ReactiveElement.Base
  ](
    val key: SimpleKey[Self, V, El] with Self
  ) extends AnyVal {

    // #Note: Aside from the general utility, we need this
    //  implicit-supporting version of the `<--` operator
    //  to handle Scala 2 union types.
    // #Note: We can't make this the ONLY implementation of <--,
    //  or even move it into class body, because it breaks
    //  type inference for Scala 3 string literal types –
    //  see StringLiteralTypeWideningSpec.scala
    def <--[ThisV](
      values: Source[ThisV]
    )(implicit
      ev: ThisV => V
    ): SimpleKeyUpdater[Self, ThisV, El] =
      new SimpleKeyUpdater[Self, ThisV, El](
        key = key,
        values = values.toObservable,
        update = (el, value) => key.set(el, Codec.mapNullable(value, ev))
      )
  }
}
