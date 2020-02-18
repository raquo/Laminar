package com.raquo.laminar.keys

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.domtypes.generic.keys.Prop
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{HtmlElement, optionToSetter}
import com.raquo.laminar.modifiers.{Binder, KeySetter, Setter}
import com.raquo.laminar.nodes.ReactiveElement

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

  def <--($value: Observable[V]): Binder[HtmlElement] = {
    Binder { element =>
      ReactiveElement.bindFn(element, $value) { value =>
        DomApi.setHtmlProperty(element, self, value)
      }
    }
  }
}
