package com.raquo.laminar.setters

import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.Style
import com.raquo.laminar.DomApi
import com.raquo.laminar.experimental.airstream.core.Observable
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

class StyleSetter[V](
  val style: Style[V],
  $value: Observable[V]
) extends Modifier[ReactiveHtmlElement[dom.html.Element]] {

  override def apply(element: ReactiveHtmlElement[dom.html.Element]): Unit = {
    element.subscribe($value, onNext(_))

    @inline def onNext(value: V): Unit = {
      // @TODO[Integrity] Does not really make sense to have two separate methods for this
      if ((value: Any).isInstanceOf[String]) {
        DomApi.htmlElementApi.setStringStyle(element, style, value.asInstanceOf[String])
      } else {
        DomApi.htmlElementApi.setStyle(element, style, value.asInstanceOf[V])
      }
    }
  }
}
