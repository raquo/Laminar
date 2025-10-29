package com.raquo.laminar.keys

import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.nodes.ReactiveHtmlElement

/**
  * This class represents an HTML Element Attribute. Meaning the key that can be set, not the whole a key-value pair.
  *
  * @tparam V type of values that this Attribute can be set to
  */
class HtmlAttr[V](
  override val name: String,
  override val codec: Codec[V, String]
) extends SimpleAttr[HtmlAttr[V], V, ReactiveHtmlElement.Base] {

  override lazy val maybe: HtmlAttr[Option[V]] = {
    new HtmlAttr[Option[V]](name, codec.optAsNull)
  }
}
