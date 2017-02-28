package com.raquo.laminar

import com.raquo.snabbdom.setters.{EventProp, EventPropSetter}
import com.raquo.snabbdom.EventCallback
import com.raquo.xstream.{ShamefulStream, XStream}
import org.scalajs.dom.raw.Event

class EventEmitter[Ev <: Event](val eventProp: EventProp[EventCallback[Ev]]) extends AnyVal {

  def addEventToStream(stream: XStream[Ev])(event: Ev): Unit = {
    new ShamefulStream(stream).shamefullySendNext(event)
  }

  def sendTo(stream: XStream[Ev]): EventPropSetter[EventCallback[Ev]] = {
    new EventPropSetter[EventCallback[Ev]](eventProp, addEventToStream(stream) _)
  }

  @inline def -->(stream: XStream[Ev]): EventPropSetter[EventCallback[Ev]] = {
    sendTo(stream)
  }
}


