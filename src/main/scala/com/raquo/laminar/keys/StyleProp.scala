package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.domtypes.generic.builders.StyleStringValueBuilder
import com.raquo.domtypes.generic.defs.styles
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{HtmlElement, optionToSetter}
import com.raquo.laminar.builders.DerivedStyleBuilders
import com.raquo.laminar.modifiers.KeySetter.StyleSetter
import com.raquo.laminar.modifiers.KeyUpdater.StyleUpdater
import com.raquo.laminar.modifiers.{KeySetter, KeyUpdater, Setter}
import com.raquo.laminar.nodes.ReactiveHtmlElement

import scala.scalajs.js.|

class StyleProp[V](
  override val name: String,
  val prefixes: Seq[String] = Nil
) extends Key with StyleStringValueBuilder[StyleSetter[V]] with DerivedStyleBuilders[DerivedStyleProp.Base] {

  @inline def apply(value: V | String): Setter[HtmlElement] = {
    this := value
  }

  def :=(value: V | String): StyleSetter[V] = {
    new KeySetter[StyleProp[V], String, HtmlElement](this, value.toString, DomApi.setHtmlStringStyle)
  }

  def maybe(value: Option[V | String]): Setter[HtmlElement] = {
    optionToSetter(value.map(v => this := v))
  }

  def <--[A]($value: Source[A])(implicit ev: A => V | String): StyleUpdater[V] = {
    new KeyUpdater[ReactiveHtmlElement.Base, StyleProp[V], V | String](
      this,
      $value.asInstanceOf[Source[V | String]].toObservable,
      (el, v) => DomApi.setHtmlAnyStyle(el, this, v)
    )
  }

  /** Create a new key for this style with these prefixes */
  def withPrefixes(ps: (styles.VendorPrefixes.type => String)*): StyleProp[V] = {
    new StyleProp[V](name, ps.map(_ (styles.VendorPrefixes)))
  }

  override protected def buildStringValue(value: String): StyleSetter[V] = {
    this := value
  }

  override protected def valueAsString(value: StyleSetter[V]): String = {
    value.value
  }

  override protected def derivedStyle[InputV](encode: InputV => String): DerivedStyleProp[InputV, V] = {
    new DerivedStyleProp[InputV, V](this, encode)
  }
}
