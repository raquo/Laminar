package com.raquo.laminar.keys

import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.nodes.ReactiveElement

/**
  * This class represents an HTML Element Attribute. Meaning the key that can be set, not the whole a key-value pair.
  *
  * @tparam V type of values that this Attribute can be set to
  */
class AriaAttr[V](
  val suffix: String,
  override val codec: Codec[V, String]
)
extends GlobalAttr[V](
  name = s"aria-${suffix}",
  codec = codec
)
with SimpleAttr[AriaAttr[V], V, ReactiveElement.Base] {

  override lazy val maybe: AriaAttr[Option[V]] = {
    new AriaAttr[Option[V]](suffix, codec.optAsNull)
  }
}
