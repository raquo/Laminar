package com.raquo.laminar.keys

import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.nodes.ReactiveMathMlElement

/**
  * This class represents an HTML Element Attribute. Meaning the key that can be set, not the whole a key-value pair.
  *
  * @tparam V type of values that this Attribute can be set to
  */
class MathMlAttr[V](
  override val name: String,
  override val codec: Codec[V, String]
) extends SimpleAttr[MathMlAttr[V], V, ReactiveMathMlElement] {

  override lazy val maybe: MathMlAttr[Option[V]] = {
    new MathMlAttr[Option[V]](name, codec.optAsNull)
  }
}
