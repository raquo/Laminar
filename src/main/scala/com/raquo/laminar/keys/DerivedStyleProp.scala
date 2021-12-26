package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{HtmlElement, optionToSetter}
import com.raquo.laminar.modifiers.KeySetter.StyleSetter
import com.raquo.laminar.modifiers.KeyUpdater.DerivedStyleUpdater
import com.raquo.laminar.modifiers.{KeySetter, KeyUpdater, Setter}
import com.raquo.laminar.nodes.ReactiveHtmlElement

/** This class represents derived style props like `height.px` or `backgroundImage.url` */
class DerivedStyleProp[InputV, StyleV](
  val key: StyleProp[StyleV],
  val encode: InputV => String
) {

  @inline def apply(value: InputV): StyleSetter[StyleV] = {
    this := value
  }

  def :=(value: InputV): StyleSetter[StyleV] = {
    new KeySetter[StyleProp[StyleV], String, HtmlElement](
      key,
      encode(value),
      DomApi.setHtmlStringStyle
    )
  }

  def maybe(value: Option[InputV]): Setter[HtmlElement] = {
    optionToSetter(value.map(v => this := v))
  }

  def <--($value: Source[InputV]): DerivedStyleUpdater[InputV, StyleV] = {
    new KeyUpdater[ReactiveHtmlElement.Base, StyleProp[StyleV], InputV](
      key,
      $value.toObservable,
      (el, v) => DomApi.setHtmlAnyStyle[StyleV](el, key, encode(v))
    )
  }
}

object DerivedStyleProp {

  type Base[V] = DerivedStyleProp[V, _]
}
