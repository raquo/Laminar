package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.keys.Prop
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

/** A Property receiver that is locked to a particular element */
class LockedPropReceiver[V, DomV](
  prop: Prop[V, DomV],
  element: ReactiveHtmlElement[dom.html.Element]
) {

  def <--($value: Observable[V]): Unit = {
    (new PropReceiver(prop) <-- $value)(element)
  }
}
