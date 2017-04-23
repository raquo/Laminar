package com.raquo.laminar.subscriptions

import com.raquo.dombuilder.keys.EventProp
import com.raquo.dombuilder.modifiers.EventPropSetter
import com.raquo.laminar
import com.raquo.laminar.nodes.ReactiveNode
import com.raquo.xstream.{ShamefulStream, XStream}
import org.scalajs.dom

import scala.scalajs.js

class EventEmitter[Ev <: dom.Event](
  val eventProp: EventProp[Ev, ReactiveNode, dom.Event, js.Function1]
) extends AnyVal {

  // @TODO[Test] verify new fancy methods

  @inline def -->(stream: XStream[Ev]): EventPropSetter[Ev, ReactiveNode, dom.Event, js.Function1] = {
    sendTo(stream)
  }

  @inline def -->[V](processor: Ev => V, sendTo: XStream[V]): EventPropSetter[Ev, ReactiveNode, dom.Event, js.Function1] = {
    send(processor, sendTo)
  }

  @inline def -->[V](value: V, sendTo: XStream[V]): EventPropSetter[Ev, ReactiveNode, dom.Event, js.Function1] = {
    this.send(value, sendTo)
  }

  def sendTo(stream: XStream[Ev]): EventPropSetter[Ev, ReactiveNode, dom.Event, js.Function1] = {
    new EventPropSetter[Ev, ReactiveNode, dom.Event, js.Function1](eventProp, pushToStream(stream), laminar.eventApi)
  }

  def send[V](value: V, to: XStream[V]): EventPropSetter[Ev, ReactiveNode, dom.Event, js.Function1] = {
    new EventPropSetter[Ev, ReactiveNode, dom.Event, js.Function1](
      key = eventProp,
      value = (event: Ev) => pushToStream(to)(value),
      laminar.eventApi
    )
  }

  def send[V](processor: Ev => V, to: XStream[V]): EventPropSetter[Ev, ReactiveNode, dom.Event, js.Function1] = {
    new EventPropSetter[Ev, ReactiveNode, dom.Event, js.Function1](
      key = eventProp,
      value = (event: Ev) => pushToStream(to)(processor(event)),
      laminar.eventApi
    )
  }

  @inline private def pushToStream[T](stream: XStream[T])(value: T): Unit = {
    new ShamefulStream(stream).shamefullySendNext(value)
  }
}


