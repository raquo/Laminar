package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.keys.ReactiveProp
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

class PropReceiver[V, DomV](
  prop: ReactiveProp[V, DomV],
  element: ReactiveHtmlElement[dom.html.Element]
) {

  def <--($value: Observable[V]): Unit = {
    (prop <-- $value)(element)
  }
}
