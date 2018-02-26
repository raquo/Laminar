package com.raquo.laminar.receivers

import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.Style
import com.raquo.laminar.DomApi
import com.raquo.laminar.experimental.airstream.core.Observable
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

import scala.scalajs.js.|

class StyleReceiver[V](val style: Style[V]) extends AnyVal {

  // @TODO[API] this needs better API than `|`
  def <--($value: Observable[V | String]): Modifier[ReactiveElement[dom.Element]] = {
    new Modifier[ReactiveElement[dom.Element]] {
      override def apply(element: ReactiveElement[dom.Element]): Unit = {
        element.subscribe($value, onNext(_))

        @inline def onNext(value: V | String): Unit = {
          // @TODO[Integrity] Try to get rid of `|`
          if ((value: Any).isInstanceOf[String]) {
            DomApi.elementApi.setStringStyle(element, style, value.asInstanceOf[String])
          } else {
            DomApi.elementApi.setStyle(element, style, value.asInstanceOf[V])
          }
        }
      }
    }
  }
}
