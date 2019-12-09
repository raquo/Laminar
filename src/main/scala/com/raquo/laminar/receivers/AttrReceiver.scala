package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.keys.ReactiveHtmlAttr
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

class AttrReceiver[V](
  attr: ReactiveHtmlAttr[V],
  element: ReactiveHtmlElement.Base
) {

  def <--($value: Observable[V]): Unit = {
    (attr <-- $value)(element)
  }
}
