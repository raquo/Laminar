package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.keys.ReactiveSvgAttr
import com.raquo.laminar.nodes.ReactiveSvgElement

class SvgAttrReceiver[V](
  attr: ReactiveSvgAttr[V],
  element: ReactiveSvgElement.Base
) {

  def <--($value: Observable[V]): Unit = {
    (attr <-- $value)(element)
  }
}
