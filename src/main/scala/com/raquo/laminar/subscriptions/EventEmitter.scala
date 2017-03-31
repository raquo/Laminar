package com.raquo.laminar.subscriptions

import com.raquo.laminar.{RNode, RNodeData}
import com.raquo.snabbdom.EventCallback
import com.raquo.snabbdom.setters.{EventProp, EventPropSetter}
import com.raquo.xstream.{ShamefulStream, XStream}
import org.scalajs.dom.raw.Event

class EventEmitter[Ev <: Event](
  val eventProp: EventProp[EventCallback[Ev], RNode, RNodeData]
) extends AnyVal {

  // @TODO[Test] verify new fancy methods

  @inline def -->(stream: XStream[Ev]): EventPropSetter[EventCallback[Ev], RNode, RNodeData] = {
    sendTo(stream)
  }

  @inline def -->[V](processor: Ev => V, sendTo: XStream[V]): EventPropSetter[EventCallback[Ev], RNode, RNodeData] = {
    send(processor, sendTo)
  }

  @inline def -->[V](value: V, sendTo: XStream[V]): EventPropSetter[EventCallback[Ev], RNode, RNodeData] = {
    this.send(value, sendTo)
  }

  def sendTo(stream: XStream[Ev]): EventPropSetter[EventCallback[Ev], RNode, RNodeData] = {
    new EventPropSetter[EventCallback[Ev], RNode, RNodeData](eventProp, pushToStream(stream) _)
  }

  def send[V](value: V, to: XStream[V]): EventPropSetter[EventCallback[Ev], RNode, RNodeData] = {
    new EventPropSetter[EventCallback[Ev], RNode, RNodeData](
      key = eventProp,
      value = (event: Ev) => pushToStream(to)(value)
    )
  }

  def send[V](processor: Ev => V, to: XStream[V]): EventPropSetter[EventCallback[Ev], RNode, RNodeData] = {
    new EventPropSetter[EventCallback[Ev], RNode, RNodeData](
      key = eventProp,
      value = (event: Ev) => pushToStream(to)(processor(event))
    )
  }

  @inline private def pushToStream[T](stream: XStream[T])(value: T): Unit = {
    new ShamefulStream(stream).shamefullySendNext(value)
  }
}


