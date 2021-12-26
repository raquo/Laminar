package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.domtypes.generic.codecs.Codec
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{SvgElement, optionToSetter}
import com.raquo.laminar.modifiers.KeyUpdater.SvgAttrUpdater
import com.raquo.laminar.modifiers.{KeySetter, KeyUpdater, Setter}

/**
  * This class represents an Svg Element Attribute. Meaning the key that can be set, not the whole a key-value pair.
  *
  * @tparam V type of values that this Attribute can be set to
  */
class SvgAttr[V](
  override val name: String,
  val codec: Codec[V, String],
  val namespace: Option[String]
) extends Key {

  @inline def apply(value: V): Setter[SvgElement] = {
    this := value
  }

  def maybe(value: Option[V]): Setter[SvgElement] = {
    optionToSetter(value.map(v => this := v))
  }

  def :=(value: V): Setter[SvgElement] = {
    new KeySetter[SvgAttr[V], V, SvgElement](this, value, DomApi.setSvgAttribute)
  }

  def <--($value: Source[V]): SvgAttrUpdater[V] = {
    new KeyUpdater[SvgElement, SvgAttr[V], V](
      this,
      $value.toObservable,
      (el, v) => DomApi.setSvgAttribute(el, this, v)
    )
  }

}
