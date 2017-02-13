package com.raquo

import com.raquo.snabbdom.EventCallback
import com.raquo.snabbdom._
import com.raquo.snabbdom.setters.{Attr, EventProp, Style}
import com.raquo.xstream.XStream
import org.scalajs.dom
import org.scalajs.dom.raw.Event

import scala.scalajs.js

package object laminar {

  private val modules = js.Array(AttrsModule, PropsModule, EventsModule, StyleModule)

  val patch: Snabbdom.PatchFn = Snabbdom.init(modules)

  def render(entryPoint: dom.Element, rootVNode: VNode): Unit = {
    patch(entryPoint, rootVNode)
  }

  val child = ChildReceiver

  implicit def childFromStream($child: XStream[VNode]): VNode = {
    child <-- $child // @TODO[API] Make this implicit def optional?
  }

  implicit def toAttrReceiver[V](attr: Attr[V]): AttrReceiver[V] = {
    new AttrReceiver(attr)
  }

  implicit def toStyleReceiver[V](style: Style[V]): StyleReceiver[V] = {
    new StyleReceiver(style)
  }

  implicit def toEventEmitter[Ev <: Event](eventProp: EventProp[EventCallback[Ev]]): EventEmitter[Ev] = {
    new EventEmitter(eventProp)
  }
}
