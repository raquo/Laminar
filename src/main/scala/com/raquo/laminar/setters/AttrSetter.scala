package com.raquo.laminar.setters

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.Attr
import com.raquo.laminar.DomApi
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

class AttrSetter[V](
  val attr: Attr[V],
  $value: Observable[V]
) extends Modifier[ReactiveHtmlElement[dom.html.Element]] {

  override def apply(element: ReactiveHtmlElement[dom.html.Element]): Unit = {
    element.subscribe(
      $value,
      (value: V) => DomApi.htmlElementApi.setAttribute(element, attr, value)
    )
  }
}
