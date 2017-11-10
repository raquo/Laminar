package com.raquo.laminar.emitter

import com.raquo.dombuilder.generic.modifiers.EventPropSetter
import com.raquo.dombuilder.jsdom.JsCallback
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.DomApi
import com.raquo.laminar.bundle.onClick
import com.raquo.laminar.nodes.ReactiveNode
import org.scalajs.dom

import scala.scalajs.js

class EventPropEmitter[Ev <: dom.Event, V](
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

object EventPropEmitter {

  implicit def eventPropEmitterToEventPropSetter[Ev <: dom.Event, V](
    eventPropReceiver: EventPropEmitter[Ev, V]
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

      if (
        preventDefault
        && eventProp == onClick
        && ev.target.asInstanceOf[dom.Element].tagName == "INPUT"
        && ev.target.asInstanceOf[dom.html.Input].`type` == "checkbox"
      ) {
        // Special case: See README and/or https://stackoverflow.com/a/32710212/2601788
        // @TODO[API] Should this behaviour extend to all checkbox.onClick events by default?
        js.timers.setTimeout(0)(sendNext(ev))
        ()
      } else {
        sendNext(ev)
      }
    }

    new EventPropSetter[ReactiveNode, dom.Element, dom.Node, Ev, dom.Event, JsCallback](
      eventProp, callback, useCapture = useCapture
    )(DomApi.eventApi)
  }
}
