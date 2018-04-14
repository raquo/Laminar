package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.keys.Style
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

import scala.scalajs.js.|

/** A Style receiver that is locked to a particular element */
class LockedStyleReceiver[V](
  style: Style[V],
  element: ReactiveHtmlElement[dom.html.Element]
) {

  def <--($value: Observable[V | String]): Unit = {
    (new StyleReceiver(style) <-- $value)(element)
  }
}
