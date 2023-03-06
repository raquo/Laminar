package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.L.{HtmlElement, optionToSetter}
import com.raquo.laminar.modifiers.KeySetter.StyleSetter
import com.raquo.laminar.modifiers.KeyUpdater.DerivedStyleUpdater
import com.raquo.laminar.modifiers.{KeySetter, KeyUpdater, Setter}
import com.raquo.laminar.nodes.ReactiveHtmlElement

/** This class represents derived style props like `height.px` or `backgroundImage.url` */
class DerivedStyleProp[InputV](
  val key: StyleProp[_],
  val encode: InputV => String
) {

  @inline def apply(value: InputV): StyleSetter = {
    this := value
  }

  def :=(value: InputV): StyleSetter = {
    new KeySetter[StyleProp[_], String, HtmlElement](
      key,
      encode(value),
      DomApi.setHtmlStringStyle
    )
  }

  def maybe(value: Option[InputV]): Setter[HtmlElement] = {
    optionToSetter(value.map(v => this := v))
  }

  def <--(values: Source[InputV]): DerivedStyleUpdater[InputV] = {
    new KeyUpdater[ReactiveHtmlElement.Base, StyleProp[_], InputV](
      key = key,
      values = values.toObservable,
      update = (el, v, _) => DomApi.setHtmlStringStyle(el, key, encode(v))
    )
  }
}
