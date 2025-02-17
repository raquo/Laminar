package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.L.Element
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.modifiers.SimpleKeySetter.AriaAttrSetter
import com.raquo.laminar.modifiers.SimpleKeyUpdater
import com.raquo.laminar.modifiers.SimpleKeyUpdater.AriaAttrUpdater
import com.raquo.laminar.nodes.ReactiveElement

/**
 * This class represents an HTML Element Attribute. Meaning the key that can be set, not the whole a key-value pair.
 *
 * @tparam V type of values that this Attribute can be set to
 */
class AriaAttr[V](
  suffix: String,
  val codec: Codec[V, String]
) extends SimpleKey[V, String, ReactiveElement.Base] {

  override val name: String = "aria-" + suffix

  override def :=(value: V): AriaAttrSetter[V] = {
    // new KeySetter[AriaAttr[V], V, String, Element](this, value, DomApi.setAriaAttribute)
    new AriaAttrSetter(this, value)
  }

  override def <--(values: Source[V]): AriaAttrUpdater[V] = {
    new SimpleKeyUpdater[Element, AriaAttr[V], V](
      key = this,
      values = values.toObservable,
      update = (el, v, _) => DomApi.setAriaAttribute(el, this, v)
    )
  }

}
