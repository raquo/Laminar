package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{Element, optionToSetter}
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.modifiers.KeyUpdater.AriaAttrUpdater
import com.raquo.laminar.modifiers.{KeySetter, KeyUpdater, Setter}

/**
 * This class represents an HTML Element Attribute. Meaning the key that can be set, not the whole a key-value pair.
 *
 * @tparam V type of values that this Attribute can be set to
 */
class AriaAttr[V](
  suffix: String,
  val codec: Codec[V, String]
) extends Key {

  override val name: String = "aria-" + suffix

  @inline def apply(value: V): Setter[Element] = {
    this := value
  }

  def maybe(value: Option[V]): Setter[Element] = {
    optionToSetter(value.map(v => this := v))
  }

  def :=(value: V): Setter[Element] = {
    new KeySetter[AriaAttr[V], V, Element](this, value, DomApi.setAriaAttribute)
  }

  def <--($value: Source[V]): AriaAttrUpdater[V] = {
    new KeyUpdater[Element, AriaAttr[V], V](
      this,
      $value.toObservable,
      (el, v) => DomApi.setAriaAttribute(el, this, v)
    )
  }

}
