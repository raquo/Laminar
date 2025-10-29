package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.SimpleKeySetter.{DerivedStyleSetter, StyleSetter}
import com.raquo.laminar.modifiers.SimpleKeyUpdater
import com.raquo.laminar.nodes.ReactiveHtmlElement

/** This class represents derived style props like `height.px` or `backgroundImage.url` */
class DerivedStyleProp[V](
  val key: StyleProp[_],
  val encode: V => String
) extends SimpleKey[DerivedStyleProp[V], V, ReactiveHtmlElement.Base] {

  override val name: String = key.name

  override def :=[ThisV <: V](value: ThisV): DerivedStyleSetter[V, ThisV] = {
    // More specific return type that offers with .cssValue: String
    // I think the concrete Type[V] also helps us avoid problems with existential types in Scala 3.
    new DerivedStyleSetter(key = this, value)
  }

  // #Note This overload is needed for Scala 2 (but it's also active in Scala 3)
  def :=[ThisV](value: ThisV)(implicit ev: ThisV => V): DerivedStyleSetter[V, V] =
    this := ev(value)

  override def apply[ThisV <: V](value: ThisV): DerivedStyleSetter[V, ThisV] =
    this := value // Same impl. as super, just more specific return type

  // #Note This overload is needed for Scala 2 (but it's also active in Scala 3)
  def apply[ThisV](value: ThisV)(implicit ev: ThisV => V): DerivedStyleSetter[V, V] =
    this := ev(value)

  override def <--[ThisV](
    values: Source[ThisV]
  )(implicit
    ev: ThisV => V
  ): SimpleKeyUpdater[DerivedStyleProp[V], ThisV, ReactiveHtmlElement.Base] =
    new SimpleKeyUpdater(
      key = this,
      values = values.toObservable,
      update = (el, value) => {
        DomApi.setHtmlDerivedStyle(el, this, ev(value))
      }
    )

  override lazy val maybe: DerivedStyleProp[Option[V]] = {
    new DerivedStyleProp[Option[V]](key, _.map(encode).orNull)
  }
}
