package com.raquo.laminar.emitter

import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.experimental.airstream.core.Observer
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

class EventPropOps[Ev <: dom.Event](
  val eventProp: EventProp[Ev]
) extends AnyVal {

  def config[El <: ReactiveElement[dom.Element]](useCapture: Boolean = false): EventPropTransformation[Ev, Ev, El] = {
    new EventPropTransformation(eventProp, useCapture, processor = (ev: Ev, _: El) => Some(ev))
  }

  def -->[El <: ReactiveElement[dom.Element]](
    observer: Observer[Ev]
  ): EventPropEmitter[Ev, Ev, El] = new EventPropEmitter[Ev, Ev, El](
    observer,
    eventProp,
    useCapture = false,
    processor = (ev: Ev, _: El) => Some(ev)
  )
}
