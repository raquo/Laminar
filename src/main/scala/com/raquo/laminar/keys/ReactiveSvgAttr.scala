package com.raquo.laminar.keys

import com.raquo.airstream.core.Observable
import com.raquo.domtypes
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{SvgElement, optionToSetter}
import com.raquo.laminar.modifiers.KeyUpdater.SvgAttrUpdater
import com.raquo.laminar.modifiers.{KeySetter, KeyUpdater, Setter}

class ReactiveSvgAttr[V](
  override val name: String,
  override val codec: Codec[V, String],
  override val namespace: Option[String]
) extends domtypes.generic.keys.SvgAttr[V](name, codec, namespace) {

  @inline def apply(value: V): Setter[SvgElement] = {
    this := value
  }

  def maybe(value: Option[V]): Setter[SvgElement] = {
    value.map(v => this := v)
  }

  def :=(value: V): Setter[SvgElement] = {
    new KeySetter[ReactiveSvgAttr[V], V, SvgElement](this, value, DomApi.setSvgAttribute)
  }

  def <--($value: Observable[V]): SvgAttrUpdater[V] = {
    new KeyUpdater[SvgElement, ReactiveSvgAttr[V], V](
      this,
      $value,
      (el, v) => DomApi.setSvgAttribute(el, this, v)
    )
  }

}
