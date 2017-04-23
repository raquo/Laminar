package com.raquo.laminar.receivers

import com.raquo.dombuilder.keys.Style
import com.raquo.dombuilder.modifiers.Modifier
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveNode}
import com.raquo.xstream.XStream

class StyleReceiver[V](val style: Style[V, ReactiveNode]) extends AnyVal {

  def <--($value: XStream[V]): Modifier[ReactiveElement] = {
    new Modifier[ReactiveElement] {
      override def applyTo(element: ReactiveElement): Unit = {
        element.subscribe($value, onNext)

        @inline def onNext(value: V): Unit = {
          element.elementApi.setStyle(element.ref, style.name, value)
        }
      }
    }
  }
}
