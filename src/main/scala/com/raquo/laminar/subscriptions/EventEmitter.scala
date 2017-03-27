package com.raquo.laminar.subscriptions

import com.raquo.laminar.{RNode, RNodeData}
import com.raquo.snabbdom.EventCallback
import com.raquo.snabbdom.setters.{EventProp, EventPropSetter}
import com.raquo.xstream.{ShamefulStream, XStream}
import org.scalajs.dom.raw.Event

class EventEmitter[Ev <: Event](
  val eventProp: EventProp[EventCallback[Ev], RNode, RNodeData]
) extends AnyVal {

  def addEventToStream(stream: XStream[Ev])(event: Ev): Unit = {
    new ShamefulStream(stream).shamefullySendNext(event)
  }

  def sendTo(stream: XStream[Ev]): EventPropSetter[EventCallback[Ev], RNode, RNodeData] = {
    new EventPropSetter[EventCallback[Ev], RNode, RNodeData](eventProp, addEventToStream(stream) _)
  }

  @inline def -->(stream: XStream[Ev]): EventPropSetter[EventCallback[Ev], RNode, RNodeData] = {
    sendTo(stream)
  }
}


