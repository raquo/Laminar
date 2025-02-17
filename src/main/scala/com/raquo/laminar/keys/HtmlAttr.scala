package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.L.HtmlElement
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.modifiers.SimpleKeySetter.HtmlAttrSetter
import com.raquo.laminar.modifiers.SimpleKeyUpdater
import com.raquo.laminar.modifiers.SimpleKeyUpdater.HtmlAttrUpdater
import com.raquo.laminar.nodes.ReactiveHtmlElement

/**
  * This class represents an HTML Element Attribute. Meaning the key that can be set, not the whole a key-value pair.
  *
  * @tparam V type of values that this Attribute can be set to
  */
class HtmlAttr[V](
  override val name: String,
  val codec: Codec[V, String]
) extends SimpleKey[V, String, ReactiveHtmlElement.Base] {

  def :=(value: V): HtmlAttrSetter[V] = {
    // new KeySetter[HtmlAttr[V], V, String, HtmlElement](this, value, DomApi.setHtmlAttribute)
    new HtmlAttrSetter[V](this, value)
  }

  def <--(values: Source[V]): HtmlAttrUpdater[V] = {
    new SimpleKeyUpdater[HtmlElement, HtmlAttr[V], V](
      key = this,
      values = values.toObservable,
      update = (el, v, _) => DomApi.setHtmlAttribute(el, this, v)
    )
  }

}
