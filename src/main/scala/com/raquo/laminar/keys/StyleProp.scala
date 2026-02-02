package com.raquo.laminar.keys

import com.raquo.laminar.defs.styles.traits.GlobalKeywords
import com.raquo.laminar.defs.styles.units.GlobalUnits
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter
import com.raquo.laminar.nodes.ReactiveElement

import scala.scalajs.js.|

class StyleProp[V](
  override val name: String,
  val prefixes: Seq[String] = Nil
) extends SimpleKey[StyleProp[V], V, ReactiveElement.Base]
with GlobalKeywords[V]
with GlobalUnits[DerivedStyleProp]
with StyleBuilder[StyleSetter[V, String]]
with DerivedStyleBuilder[DerivedStyleProp] { self =>

  override def :=[ThisV <: V](value: ThisV): StyleSetter[V, ThisV] = {
    // More specific return type that offers with .cssValue: String
    // I think the concrete Type[V] also helps us avoid problems with existential types in Scala 3.
    new StyleSetter[V, ThisV](this, value)
  }

  // #Note This overload is needed for Scala 2 (but it's also active in Scala 3)
  def :=[ThisV](value: ThisV)(implicit ev: ThisV => V): StyleSetter[V, V] =
    this := ev(value)

  override def apply[ThisV <: V](value: ThisV): StyleSetter[V, ThisV] =
    this := value // Same impl. as super, just more specific return type

  // #Note This overload is needed for Scala 2 (but it's also active in Scala 3)
  def apply[ThisV](value: ThisV)(implicit ev: ThisV => V): StyleSetter[V, V] =
    this := ev(value)

  override protected def set(el: ReactiveElement.Base, value: V | Null): Unit =
    DomApi.setStyle(el, this, DomApi.cssValue(value))

  override lazy val maybe: DerivedStyleProp[Option[V]] = {
    new DerivedStyleProp[Option[V]](
      this, _.map(DomApi.cssValue).orNull.asInstanceOf[String] // #safe Because we expect nulls here
    )
  }

  /** Create a new key for this style with these prefixes */
  def withPrefixes(ps: (StyleVendorPrefixes.type => String)*): StyleProp[V] = {
    new StyleProp[V](name, ps.map(_(StyleVendorPrefixes)))
  }

  // #nc test that I can :=, (), and <-- into StyleProp[_] or something equally generic...

  // #nc if this has to be public, rename to `:=`?
  // #nc scala 3 does not allow access
  // #TODO[Scala3] bug – Scala does not allow me to use the parent method of this from trait Auto. Make a small reproduction. This method should be protected.
  //  https://github.com/scala/scala3/issues/24305
  override def styleSetter(value: String): StyleSetter[V, String] =
    new StyleSetter(this, value)

  override protected def derivedStyle[InputV](encode: InputV => String): DerivedStyleProp[InputV] = {
    new DerivedStyleProp[InputV](key = this, encode)
  }
}
