package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.L.{HtmlElement, optionToSetter}
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.modifiers.KeySetter.HtmlAttrSetter
import com.raquo.laminar.modifiers.KeyUpdater.HtmlAttrUpdater
import com.raquo.laminar.modifiers.{KeySetter, KeyUpdater, Setter}

/**
  * This class represents an HTML Element Attribute. Meaning the key that can be set, not the whole a key-value pair.
  *
  * @tparam V type of values that this Attribute can be set to
  */
class HtmlAttr[V](
  override val name: String,
  val codec: Codec[V, String]
) extends Key {

  @inline def apply(value: V): HtmlAttrSetter[V] = {
    this := value
  }

  def maybe(value: Option[V]): Setter[HtmlElement] = {
    optionToSetter(value.map(v => this := v))
  }

  def :=(value: V): HtmlAttrSetter[V] = {
    new KeySetter[HtmlAttr[V], V, HtmlElement](this, value, DomApi.setHtmlAttribute)
  }

  def <--(values: Source[V]): HtmlAttrUpdater[V] = {
    new KeyUpdater[HtmlElement, HtmlAttr[V], V](
      key = this,
      values = values.toObservable,
      update = (el, v, _) => DomApi.setHtmlAttribute(el, this, v)
    )
  }

}
