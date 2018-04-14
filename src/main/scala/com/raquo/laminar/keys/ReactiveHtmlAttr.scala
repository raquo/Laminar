package com.raquo.laminar.keys

import com.raquo.airstream.core.Observable
import com.raquo.dombuilder.generic.modifiers.Setter
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.domtypes.generic.keys.HtmlAttr
import com.raquo.laminar.DomApi
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

class ReactiveHtmlAttr[V](
  override val name: String,
  override val codec: Codec[V, String]
) extends HtmlAttr[V](name, codec) { self =>

  private type HtmlElement = ReactiveHtmlElement[dom.html.Element]

  @inline def apply(value: V): Modifier[HtmlElement] = {
    :=(value)
  }

  def :=(value: V): Modifier[HtmlElement] = {
    new Setter[ReactiveHtmlAttr[V], V, HtmlElement](this, value, DomApi.htmlElementApi.setHtmlAttribute)
  }

  // @TODO[Performance] Is this encoded efficiently in Scala.js? Note the `self`.
  def <--($value: Observable[V]): Modifier[HtmlElement] = {
    new Modifier[HtmlElement] {
      override def apply(element: HtmlElement): Unit = {
        element.subscribe(
          $value,
          (value: V) => DomApi.htmlElementApi.setHtmlAttribute(element, self, value)
        )
      }
    }
  }

}
