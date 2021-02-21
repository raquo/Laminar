package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.domtypes.generic.keys.HtmlAttr
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{HtmlElement, optionToSetter}
import com.raquo.laminar.modifiers.KeyUpdater.HtmlAttrUpdater
import com.raquo.laminar.modifiers.{KeySetter, KeyUpdater, Setter}

class ReactiveHtmlAttr[V](
  override val name: String,
  override val codec: Codec[V, String]
) extends HtmlAttr[V](name, codec) {

  @inline def apply(value: V): Setter[HtmlElement] = {
    this := value
  }

  def maybe(value: Option[V]): Setter[HtmlElement] = {
    value.map(v => this := v)
  }

  def :=(value: V): Setter[HtmlElement] = {
    new KeySetter[ReactiveHtmlAttr[V], V, HtmlElement](this, value, DomApi.setHtmlAttribute)
  }

  def <--($value: Source[V]): HtmlAttrUpdater[V] = {
    new KeyUpdater[HtmlElement, ReactiveHtmlAttr[V], V](
      this,
      $value.toObservable,
      (el, v) => DomApi.setHtmlAttribute(el, this, v)
    )
  }

}
