package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.keys.ReactiveStyle
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

import scala.scalajs.js.|

class StyleReceiver[V](
  style: ReactiveStyle[V],
  element: ReactiveHtmlElement.Base
) {

  def <--($value: Observable[V | String]): Unit = {
    (style <-- $value)(element)
  }
}
