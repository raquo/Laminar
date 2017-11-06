package com.raquo.laminar.emitter

import com.raquo.dombuilder.generic.modifiers.EventPropSetter
import com.raquo.dombuilder.jsdom.JsCallback
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.DomApi
import com.raquo.laminar.nodes.ReactiveNode
import org.scalajs.dom

// @TODO[Naming] I'm not sure if "Receiver" is a good term for this
class EventPropReceiver[Ev <: dom.Event, V](
  protected val key: EventProp[Ev],
  protected val eventBus: EventBus[V],
  protected val processor: Ev => V
) {

  protected var _useCapture = false
  protected var _preventDefault = false
  protected var _stopPropagation = false

  def useCapture: this.type = {
    _useCapture = true
    this
  }

  def useCapture(newUseCapture: Boolean): this.type = {
    _useCapture = newUseCapture
    this
  }

  def preventDefault: this.type = {
    _preventDefault = true
    this
  }

  def preventDefault(newPreventDefault: Boolean): this.type = {
    _preventDefault = newPreventDefault
    this
  }

  def stopPropagation: this.type = {
    _stopPropagation = true
    this
  }

  def stopPropagation(newStopPropagation: Boolean): this.type = {
    _stopPropagation = newStopPropagation
    this
  }
}

object EventPropReceiver {

  implicit def eventPropReceiverToEventPropSetter[Ev <: dom.Event, V](
    eventPropReceiver: EventPropReceiver[Ev, V]
  ): EventPropSetter[ReactiveNode, dom.Element, dom.Node, Ev, dom.Event, JsCallback] = {
    eventPropSetter(
      eventPropReceiver.key,
      sendNext = ev => eventPropReceiver.eventBus.sendNext(eventPropReceiver.processor(ev)),
      useCapture = eventPropReceiver._useCapture,
      preventDefault = eventPropReceiver._preventDefault,
      stopPropagation = eventPropReceiver._stopPropagation
    )
  }

  def eventPropSetter[Ev <: dom.Event](
    eventProp: EventProp[Ev],
    sendNext: Ev => Unit,
    useCapture: Boolean,
    preventDefault: Boolean,
    stopPropagation: Boolean
  ): EventPropSetter[ReactiveNode, dom.Element, dom.Node, Ev, dom.Event, JsCallback] = {

    val callback = (ev: Ev) => {
      if (preventDefault) {
        ev.preventDefault()
      }
      if (stopPropagation) {
        ev.stopPropagation()
      }
      sendNext(ev)
    }

    new EventPropSetter[ReactiveNode, dom.Element, dom.Node, Ev, dom.Event, JsCallback](
      eventProp, callback, useCapture = useCapture
    )(DomApi.eventApi)
  }
}
