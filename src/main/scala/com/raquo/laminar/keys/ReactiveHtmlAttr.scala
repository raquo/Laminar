package com.raquo.laminar.keys

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.domtypes.generic.keys.HtmlAttr
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{HtmlElement, optionToSetter}
import com.raquo.laminar.modifiers.{Binder, KeySetter, Setter}
import com.raquo.laminar.nodes.ReactiveElement

class ReactiveHtmlAttr[V](
  override val name: String,
  override val codec: Codec[V, String]
) extends HtmlAttr[V](name, codec) { self =>

  @inline def apply(value: V): Setter[HtmlElement] = {
    this := value
  }

  def maybe(value: Option[V]): Setter[HtmlElement] = {
    value.map(v => this := v)
  }

  def :=(value: V): Setter[HtmlElement] = {
    new KeySetter[ReactiveHtmlAttr[V], V, HtmlElement](this, value, DomApi.setHtmlAttribute)
  }

  // @TODO[Performance] Is this encoded efficiently in Scala.js? Note the `self`.
  def <--($value: Observable[V]): Binder[HtmlElement] = {
    Binder { element =>
      ReactiveElement.bindFn(element, $value) { value =>
        DomApi.setHtmlAttribute(element, self, value)
      }
    }
  }

}
