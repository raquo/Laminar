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

  type EventSetter = EventPropSetter[Ev, ReactiveNode, dom.Event, js.Function1]

  @inline def -->(stream: XStream[Ev]): EventSetter = {
    sendTo(stream)
  }

  @inline def -->[V](processor: Ev => V, sendTo: XStream[V]): EventSetter = {
    send(processor, sendTo)
  }

  @inline def -->[V](value: V, sendTo: XStream[V]): EventSetter = {
    this.send(value, sendTo)
  }

  def sendTo(stream: XStream[Ev]): EventSetter = {
    new EventPropSetter[Ev, ReactiveNode, dom.Event, js.Function1](
      key = eventProp,
      value = pushToStream(stream),
      laminar.eventApi
    )
  }

  def send[V](value: V, to: XStream[V]): EventSetter = {
    new EventPropSetter[Ev, ReactiveNode, dom.Event, js.Function1](
      key = eventProp,
      value = (event: Ev) => pushToStream(to)(value),
      laminar.eventApi
    )
  }

  def send[V](processor: Ev => V, to: XStream[V]): EventSetter = {
    new EventPropSetter[Ev, ReactiveNode, dom.Event, js.Function1](
      key = eventProp,
      value = (event: Ev) => pushToStream(to)(processor(event)),
      laminar.eventApi
    )
  }

  @inline private def pushToStream[T](stream: XStream[T])(value: T): Unit = {
    // @TODO this should probably work on producers... somehow
    // In sendTo:
    // - Create producer
    // - When producer is started, add an event listener to the node
    //   - When a new event comes in from the user, fire listener.next()
    // - When producer is stopped, remove event listener from the node
    // Now we need to tie this to our API somehow:
    // - 1. This should be a modifier
    // - 2. sendTo take a stream to send the value to. Should instead... take... a producer... a Source?
    // Hrm so given those constraints, how should the API look?
    //   val clickSource = makeSource[Node]()
    //   onClick --> (_.target, to = clickSource)
    // And what's the whole point to all this?
    // - Avoid bad design by pushing events onto random streams
    // - But do we need the whole dance with producers then?

    new ShamefulStream(stream).shamefullySendNext(value)
  }
}


