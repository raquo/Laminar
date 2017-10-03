package com.raquo.laminar.subscriptions

import com.raquo.dombuilder.generic
import com.raquo.dombuilder.jsdom.JsCallback
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.DomApi
import com.raquo.laminar.nodes.ReactiveNode
import com.raquo.xstream.{ShamefulStream, XStream}
import org.scalajs.dom

class EventEmitter[Ev <: dom.Event](
  val eventProp: EventProp[Ev]
) extends AnyVal {

  // @TODO[Test] verify new fancy methods

  type EventSetter = generic.modifiers.EventPropSetter[ReactiveNode, dom.Element, dom.Node, Ev, dom.Event, JsCallback]

  @inline def -->(stream: XStream[Ev]): EventSetter = {
    sendTo(stream)
  }

  @inline def -->(stream: XStream[Ev], useCapture: Boolean): EventSetter = {
    sendTo(stream, useCapture)
  }

  @inline def -->[V](value: V, target: XStream[V]): EventSetter = {
    sendValue(value, target)
  }

  @inline def -->[V](value: V, sendTo: XStream[V], useCapture: Boolean = false): EventSetter = {
    sendValue(value, sendTo, useCapture)
  }

  @inline def -->[V](processor: Ev => V, target: XStream[V]): EventSetter = {
    sendVia(processor, target)
  }

  @inline def -->[V](processor: Ev => V, target: XStream[V], useCapture: Boolean): EventSetter = {
    sendVia(processor, target, useCapture)
  }

  @inline def sendTo(
    targetStream: XStream[Ev],
    useCapture: Boolean = false
  ): EventSetter = new EventSetter(
    key = eventProp,
    value = pushToStream(targetStream),
    useCapture
  )(DomApi.eventApi)

  @inline def sendValue[V](
    value: V,
    target: XStream[V],
    useCapture: Boolean = false
  ): EventSetter = new EventSetter(
    key = eventProp,
    value = (event: Ev) => pushToStream(target)(value),
    useCapture
  )(DomApi.eventApi)

  @inline def sendVia[V](
    processor: Ev => V,
    target: XStream[V],
    useCapture: Boolean = false
  ): EventSetter = new EventSetter(
    key = eventProp,
    value = (event: Ev) => pushToStream(target)(processor(event)),
    useCapture
  )(DomApi.eventApi)

  @inline private def pushToStream[T](stream: XStream[T])(value: T): Unit = {
    // @TODO this should probably work on Producers... somehow
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


