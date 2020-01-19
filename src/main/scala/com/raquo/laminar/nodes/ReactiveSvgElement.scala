package com.raquo.laminar.nodes

import com.raquo.laminar.DomApi
import com.raquo.laminar.builders.SvgTag
import com.raquo.laminar.keys.ReactiveSvgAttr
import com.raquo.laminar.receivers.SvgAttrReceiver
import org.scalajs.dom

class ReactiveSvgElement[+Ref <: dom.svg.Element](val tag: SvgTag[Ref])
  extends ReactiveElement[Ref] {

  final override val ref: Ref = DomApi.createSvgElement(this)

  final def <--[V](attr: ReactiveSvgAttr[V]): SvgAttrReceiver[V] = new SvgAttrReceiver(attr, this)

  override def toString: String = {
    // `ref` is not available inside ReactiveElement's constructor due to initialization order, so fall back to `tag`.
    s"ReactiveSvgElement${ if (ref != null) ref.outerHTML else s"tag=${tag.name}"})"
  }
}

object ReactiveSvgElement {

  type Base = ReactiveSvgElement[dom.svg.Element]
}
