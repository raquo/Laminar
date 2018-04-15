package com.raquo.laminar.emitter

import com.raquo.airstream.core.Observer
import com.raquo.dombuilder.generic.modifiers.EventPropSetter
import com.raquo.dombuilder.jsdom.JsCallback
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.L.onClick
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveNode}
import org.scalajs.dom

import scala.scalajs.js

class EventPropEmitter[Ev <: dom.Event, V, -El <: ReactiveElement[dom.Element]](
  protected val observer: Observer[V],
  eventProp: EventProp[Ev],
  useCapture: Boolean,
  processor: Ev => Option[V]
) extends EventPropTransformation(eventProp, useCapture, processor)
  with Modifier[El]
{
  override def apply(element: El): Unit = {
    val callback = (ev: Ev) => {
      if (
        ev.defaultPrevented
        && eventProp == onClick
        && ev.target.asInstanceOf[dom.Element].tagName == "INPUT" // ugly but performy
        && ev.target.asInstanceOf[dom.html.Input].`type` == "checkbox"
      ) {
        // Special case: See README and/or https://stackoverflow.com/a/32710212/2601788
        // @TODO[API] Should this behaviour extend to all checkbox.onClick events by default?
        js.timers.setTimeout(0)(processor(ev).foreach(observer.onNext))
        ()
      } else {
        processor(ev).foreach(observer.onNext)
      }
    }

    val addEventListener = new EventPropSetter[ReactiveNode, dom.Element, dom.Node, Ev, dom.Event, JsCallback](
      eventProp, callback, useCapture = useCapture
    )(DomApi.eventApi)

    // @TODO[Integrity] Check that we're not leaking memory here by never removing this event listener, especially in the zipWithNode case.
    addEventListener(element)
  }
}
