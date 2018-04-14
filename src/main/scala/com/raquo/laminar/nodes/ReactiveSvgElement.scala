package com.raquo.laminar.nodes

import com.raquo.laminar.DomApi
import com.raquo.laminar.builders.ReactiveSvgTag
import com.raquo.laminar.keys.ReactiveSvgAttr
import com.raquo.laminar.receivers.SvgAttrReceiver
import org.scalajs.dom

class ReactiveSvgElement[+Ref <: dom.svg.Element](val tag: ReactiveSvgTag[Ref])
  extends ReactiveElement[Ref] {

  final override val ref: Ref = DomApi.svgElementApi.createSvgElement(this)

  final def <--[V](attr: ReactiveSvgAttr[V]): SvgAttrReceiver[V] = new SvgAttrReceiver(attr, this)
}
