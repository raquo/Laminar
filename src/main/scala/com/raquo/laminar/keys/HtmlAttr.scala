package com.raquo.laminar.keys

import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.domapi.KeyDomApi.HtmlAttrDomApi
import com.raquo.laminar.domapi.RWKeyDomApi
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

  override type Self[VV] = HtmlAttr[VV]

  override val domApi: RWKeyDomApi[HtmlAttr, ReactiveHtmlElement.Base] = HtmlAttrDomApi

  // #nc remove
  // def :=(value: V): HtmlAttrSetter[V] = {
  //   // new KeySetter[HtmlAttr[V], V, String, HtmlElement](this, value, DomApi.setHtmlAttribute)
  //   new HtmlAttrSetter[V](this, value)
  // }

  // def <--(values: Source[V]): HtmlAttrUpdater[V] = {
  //   new SimpleKeyUpdater[HtmlElement, HtmlAttr[V], V](
  //     key = this,
  //     values = values.toObservable,
  //     update = (el, v, _) => DomApi.setHtmlAttribute(el, this, v)
  //   )
  // }

}
