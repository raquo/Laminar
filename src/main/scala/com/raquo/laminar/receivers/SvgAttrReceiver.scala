package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.keys.ReactiveSvgAttr
import com.raquo.laminar.nodes.ReactiveSvgElement
import org.scalajs.dom

class SvgAttrReceiver[V](
  attr: ReactiveSvgAttr[V],
  element: ReactiveSvgElement[dom.svg.Element]
) {

  def <--($value: Observable[V]): Unit = {
    (attr <-- $value)(element)
  }
}
