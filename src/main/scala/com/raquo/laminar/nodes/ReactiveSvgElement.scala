package com.raquo.laminar.nodes

import com.raquo.domtypes.generic.keys.SvgAttr
import com.raquo.laminar.DomApi
import com.raquo.laminar.builders.ReactiveSvgTag
import com.raquo.laminar.receivers.LockedSvgAttrReceiver
import org.scalajs.dom

class ReactiveSvgElement[+Ref <: dom.svg.Element](val tag: ReactiveSvgTag[Ref])
  extends ReactiveElement[Ref] {

  override val ref: Ref = DomApi.svgElementApi.createSvgElement(this)

  @inline def <--[V](attr: SvgAttr[V]): LockedSvgAttrReceiver[V] = new LockedSvgAttrReceiver(attr, this)
}
