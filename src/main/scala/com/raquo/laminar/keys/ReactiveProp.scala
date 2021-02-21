package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.domtypes.generic.keys.Prop
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{HtmlElement, optionToSetter}
import com.raquo.laminar.modifiers.KeyUpdater.PropUpdater
import com.raquo.laminar.modifiers.{KeySetter, KeyUpdater, Setter}

class ReactiveProp[V, DomV](
  override val name: String,
  override val codec: Codec[V, DomV]
) extends Prop[V, DomV](name, codec) { self =>

  @inline def apply(value: V): Setter[HtmlElement] = {
    this := value
  }

  def maybe(value: Option[V]): Setter[HtmlElement] = {
    value.map(v => this := v)
  }

  def :=(value: V): Setter[HtmlElement] = {
    new KeySetter[ReactiveProp[V, DomV], V, HtmlElement](this, value, DomApi.setHtmlProperty)
  }

  def <--($value: Source[V]): PropUpdater[V, DomV] = {
    new KeyUpdater[HtmlElement, ReactiveProp[V, DomV], V](
      this,
      $value.toObservable,
      (element, nextValue) => DomApi.setHtmlProperty(element, this, nextValue)
    )
  }

}
