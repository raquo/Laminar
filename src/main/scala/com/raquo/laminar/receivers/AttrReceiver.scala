package com.raquo.laminar.receivers

import com.raquo.dombuilder.keys.Attr
import com.raquo.dombuilder.modifiers.Modifier
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveNode}
import com.raquo.xstream.XStream
import org.scalajs.dom

class AttrReceiver[V](val attr: Attr[V, ReactiveNode, dom.Element, dom.Node]) extends AnyVal {

  def <--($value: XStream[V]): Modifier[ReactiveElement] = {
    new Modifier[ReactiveElement] {
      override def applyTo(element: ReactiveElement): Unit = {
        element.subscribe($value, onNext)

        @inline def onNext(value: V): Unit ={
          element.elementApi.setAttribute(element.ref, attr.name, value)
        }
      }
    }
  }
}
