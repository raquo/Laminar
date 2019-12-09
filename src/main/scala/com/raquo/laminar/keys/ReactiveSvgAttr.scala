package com.raquo.laminar.keys

import com.raquo.airstream.core.Observable
import com.raquo.domtypes
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{SvgElement, optionToModifier}
import com.raquo.laminar.setters.Setter

class ReactiveSvgAttr[V](
  override val name: String,
  override val codec: Codec[V, String],
  override val namespace: Option[String]
) extends domtypes.generic.keys.SvgAttr[V](name, codec, namespace) { self =>

  @inline def apply(value: V): Modifier[SvgElement] = {
    this := value
  }

  def maybe(value: Option[V]): Modifier[SvgElement] = {
    value.map(v => this := v)
  }

  def :=(value: V): Modifier[SvgElement] = {
    new Setter[ReactiveSvgAttr[V], V, SvgElement](this, value, DomApi.setSvgAttribute)
  }

  def <--($value: Observable[V]): Modifier[SvgElement] = {
    new Modifier[SvgElement] {
      override def apply(element: SvgElement): Unit = {
        element.subscribe($value) { value =>
          DomApi.setSvgAttribute(element, self, value)
        }
      }
    }
  }


}
