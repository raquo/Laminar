package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.keys.Attr
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

/** An Attribute receiver that is locked to a particular element */
class LockedAttrReceiver[V](
  attr: Attr[V],
  element: ReactiveHtmlElement[dom.html.Element]
) {

  def <--($value: Observable[V]): Unit = {
    (new AttrReceiver(attr) <-- $value)(element)
  }
}
