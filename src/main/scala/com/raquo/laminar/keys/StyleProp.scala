package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.api.unionOrNull
import com.raquo.laminar.defs.styles.traits.GlobalKeywords
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter
import com.raquo.laminar.modifiers.SimpleKeyUpdater
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base

import scala.scalajs.js.|

class StyleProp[V](
  override val name: String,
  val prefixes: Seq[String] = Nil
)
extends SimpleKey[StyleProp[V], V | String, ReactiveHtmlElement.Base]
with GlobalKeywords[V]
with StyleBuilder[StyleSetter[V], DerivedStyleProp] {

  override def :=(value: V | String): StyleSetter[V] = {
    // More specific return type that offers with .cssValue: String
    // I think the concrete Type[V] also helps us avoid problems with existential types in Scala 3.
    new StyleSetter(this, value)
  }

  override def <--(values: Source[V | String]): SimpleKeyUpdater[StyleProp[V], V | String, Base] =
    new SimpleKeyUpdater[StyleProp[V], V | String, ReactiveHtmlElement.Base](
      key = this,
      values = values.toObservable,
      update = (el, value) => {
        DomApi.setHtmlStyle(el, this, unionOrNull(value))
      }
    )

  override def apply(value: V | String): StyleSetter[V] =
    this := value // Same impl. as super, just more specific return type

  /** Create a new key for this style with these prefixes */
  def withPrefixes(ps: (StyleVendorPrefixes.type => String)*): StyleProp[V] = {
    new StyleProp[V](name, ps.map(_(StyleVendorPrefixes)))
  }

  override protected def styleSetter(value: String): StyleSetter[V] = {
    this := value
  }

  override protected def derivedStyle[InputV](encode: InputV => String): DerivedStyleProp[InputV] = {
    new DerivedStyleProp[InputV](key = this, encode)
  }
}
