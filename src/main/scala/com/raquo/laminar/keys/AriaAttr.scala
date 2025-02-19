package com.raquo.laminar.keys

import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.domapi.KeyDomApi.AriaAttrDomApi
import com.raquo.laminar.domapi.RWKeyDomApi
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.nodes.ReactiveElement.Base

/**
 * This class represents an HTML Element Attribute. Meaning the key that can be set, not the whole a key-value pair.
 *
 * @tparam V type of values that this Attribute can be set to
 */
class AriaAttr[V](
  suffix: String,
  val codec: Codec[V, String]
) extends SimpleKey[V, String, ReactiveElement.Base] {

  override type Self[VV] = AriaAttr[VV]

  override val domApi: RWKeyDomApi[AriaAttr, Base] = AriaAttrDomApi

  override val name: String = "aria-" + suffix

}
