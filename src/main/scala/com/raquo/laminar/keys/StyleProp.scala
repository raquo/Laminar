package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.L.HtmlElement
import com.raquo.laminar.defs.styles.traits.GlobalKeywords
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter
import com.raquo.laminar.modifiers.SimpleKeyUpdater
import com.raquo.laminar.modifiers.SimpleKeyUpdater.StyleUpdater
import com.raquo.laminar.nodes.ReactiveHtmlElement

import scala.scalajs.js.|

class StyleProp[V](
  override val name: String,
  val prefixes: Seq[String] = Nil
)
extends SimpleKey[V | String, String, HtmlElement]
with GlobalKeywords
with StyleBuilder[StyleSetter[_], DerivedStyleProp] {

  def :=(value: V | String): StyleSetter[V] = {
    // new KeySetter[StyleProp[V], V | String, String, HtmlElement](this, value.toString, DomApi.setHtmlAnyStyle)
    new StyleSetter(this, value)
  }

  /** Source[V] and Source[String] are of course also accepted. */
  def <--(values: Source[V | String]): StyleUpdater[V] = {
    new SimpleKeyUpdater[ReactiveHtmlElement.Base, StyleProp[V], V | String](
      key = this,
      values = values.toObservable,
      update = (el, v, _) => DomApi.setHtmlStyle(el, this, v)
    )
  }

  /** Create a new key for this style with these prefixes */
  def withPrefixes(ps: (StyleVendorPrefixes.type => String)*): StyleProp[V] = {
    new StyleProp[V](name, ps.map(_(StyleVendorPrefixes)))
  }

  override protected def styleSetter(value: String): StyleSetter[_] = {
    this := value
  }

  // override protected def valueAsString(value: StyleSetter[V]): String = {
  //   value.value
  // }

  override protected def derivedStyle[InputV](encode: InputV => String): DerivedStyleProp[InputV] = {
    new DerivedStyleProp[InputV](this, encode)
  }
}
