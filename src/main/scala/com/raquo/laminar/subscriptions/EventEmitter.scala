package com.raquo.laminar.subscriptions

import com.raquo.dombuilder.jsdom
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.xstream.{ShamefulStream, XStream}
import org.scalajs.dom

class EventEmitter[Ev <: dom.Event](
  val eventProp: EventProp[Ev]
) extends AnyVal {

  // @TODO[Test] verify new fancy methods

  type EventSetter = jsdom.modifiers.EventPropSetter[Ev]

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
    new jsdom.modifiers.EventPropSetter[Ev](
      key = eventProp,
      value = pushToStream(stream)
    )
  }

  def send[V](value: V, to: XStream[V]): EventSetter = {
    new jsdom.modifiers.EventPropSetter[Ev](
      key = eventProp,
      value = (event: Ev) => pushToStream(to)(value)
    )
  }

  def send[V](processor: Ev => V, to: XStream[V]): EventSetter = {
    new jsdom.modifiers.EventPropSetter[Ev](
      key = eventProp,
      value = (event: Ev) => pushToStream(to)(processor(event))
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


