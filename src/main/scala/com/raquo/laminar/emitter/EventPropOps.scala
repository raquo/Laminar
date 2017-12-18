package com.raquo.laminar.emitter

import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.streams.WriteBus
import org.scalajs.dom

class EventPropOps[Ev <: dom.Event](
  val eventProp: EventProp[Ev]
) extends AnyVal {

  // @TODO[IDE] IntelliJ 2017.3 does not understand onClick(useCapture = true) syntax (marks useCapture as non-existing param)
  def apply[El <: ReactiveElement[dom.Element]](useCapture: Boolean = false): EventPropTransformation[Ev, Ev, El] = {
    new EventPropTransformation(eventProp, useCapture, processor = (ev: Ev, _: El) => Some(ev))
  }

  def -->[El <: ReactiveElement[dom.Element], BusEv >: Ev](
    writeBus: WriteBus[BusEv]
  ): EventPropEmitter[Ev, Ev, BusEv, El] = new EventPropEmitter[Ev, Ev, BusEv, El](
    writeBus,
    eventProp,
    useCapture = false,
    processor = (ev: Ev, _: El) => Some(ev)
  )
}
