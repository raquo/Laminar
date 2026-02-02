package com.raquo.laminar.keys

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.SimpleKeySetter.DerivedStyleSetter
import com.raquo.laminar.nodes.ReactiveElement

import scala.scalajs.js.|

/** This class represents derived style props like `height.px` or `backgroundImage.url` */
class DerivedStyleProp[V](
  val key: StyleProp[_],
  val encode: V => String
) extends SimpleKey[DerivedStyleProp[V], V, ReactiveElement.Base] {

  override val name: String = key.name

  override def :=[ThisV <: V](value: ThisV): DerivedStyleSetter[V, ThisV] = {
    // More specific return type that offers with .cssValue: String
    // I think the concrete Type[V] also helps us avoid problems with existential types in Scala 3.
    new DerivedStyleSetter(key = this, value)
  }

  // #Note This overload is needed for Scala 2 union types (but it's also active in Scala 3)
  def :=[ThisV](value: ThisV)(implicit ev: ThisV => V): DerivedStyleSetter[V, V] =
    this := ev(value)

  override def apply[ThisV <: V](value: ThisV): DerivedStyleSetter[V, ThisV] =
    this := value // Same impl. as super, just more specific return type

  // #Note This overload is needed for Scala 2 union types (but it's also active in Scala 3)
  def apply[ThisV](value: ThisV)(implicit ev: ThisV => V): DerivedStyleSetter[V, V] =
    this := ev(value)

  override lazy val maybe: DerivedStyleProp[Option[V]] = {
    new DerivedStyleProp[Option[V]](key, _.map(encode).orNull)
  }

  override protected def set(el: ReactiveElement.Base, value: V | Null): Unit = {
    DomApi.setDerivedStyle(el, this, value)
  }
}
