package com.raquo.laminar.keys

import com.raquo.airstream.core.Observable
import com.raquo.dombuilder.generic.modifiers.Setter
import com.raquo.domtypes
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.laminar.DomApi
import com.raquo.laminar.nodes.ReactiveSvgElement
import org.scalajs.dom

class ReactiveSvgAttr[V](
  override val name: String,
  override val codec: Codec[V, String]
) extends domtypes.generic.keys.SvgAttr[V](name, codec) { self =>

  private type SvgElement = ReactiveSvgElement[dom.svg.Element]

  @inline def apply(value: V): Modifier[SvgElement] = {
    :=(value)
  }

  def :=(value: V): Modifier[SvgElement] = {
    new Setter[ReactiveSvgAttr[V], V, SvgElement](this, value, DomApi.svgElementApi.setSvgAttribute)
  }

  def <--($value: Observable[V]): Modifier[SvgElement] = {
    new Modifier[SvgElement] {
      override def apply(element: SvgElement): Unit = {
        element.subscribe($value) { value =>
          DomApi.svgElementApi.setSvgAttribute(element, self, value)
        }
      }
    }
  }


}
