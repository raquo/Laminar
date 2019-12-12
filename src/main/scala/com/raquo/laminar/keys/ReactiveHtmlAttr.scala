package com.raquo.laminar.keys

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.domtypes.generic.keys.HtmlAttr
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{HtmlElement, optionToModifier}
import com.raquo.laminar.setters.Setter

class ReactiveHtmlAttr[V](
  override val name: String,
  override val codec: Codec[V, String]
) extends HtmlAttr[V](name, codec) { self =>

  @inline def apply(value: V): Modifier[HtmlElement] = {
    this := value
  }

  def maybe(value: Option[V]): Modifier[HtmlElement] = {
    value.map(v => this := v)
  }

  def :=(value: V): Modifier[HtmlElement] = {
    new Setter[ReactiveHtmlAttr[V], V, HtmlElement](this, value, DomApi.setHtmlAttribute)
  }

  // @TODO[Performance] Is this encoded efficiently in Scala.js? Note the `self`.
  def <--($value: Observable[V]): Modifier[HtmlElement] = {
    new Modifier[HtmlElement] {
      override def apply(element: HtmlElement): Unit = {
        element.subscribe($value) { value =>
          DomApi.setHtmlAttribute(element, self, value)
        }
      }
    }
  }

}
