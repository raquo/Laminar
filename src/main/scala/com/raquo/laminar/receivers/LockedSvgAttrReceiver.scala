package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.keys.SvgAttr
import com.raquo.laminar.nodes.ReactiveSvgElement
import org.scalajs.dom

/** An Attribute receiver that is locked to a particular element */
class LockedSvgAttrReceiver[V](
  attr: SvgAttr[V],
  element: ReactiveSvgElement[dom.svg.Element]
) {

  def <--($value: Observable[V]): Unit = {
    (new SvgAttrReceiver(attr) <-- $value)(element)
  }
}
