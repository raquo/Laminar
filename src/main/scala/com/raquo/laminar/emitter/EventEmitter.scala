package com.raquo.laminar.emitter

import com.raquo.domtypes.generic.keys.EventProp
import org.scalajs.dom

class EventEmitter[Ev <: dom.Event](
  val eventProp: EventProp[Ev]
) extends AnyVal {

  @inline def -->(eventBus: EventBus[Ev]): EventPropReceiver[Ev, Ev] = new EventPropReceiver(
    key = eventProp,
    eventBus = eventBus,
    processor = ev => ev
  )

  @inline def -->[V](value: V, eventBus: EventBus[V]): EventPropReceiver[Ev, V] = new EventPropReceiver(
    key = eventProp,
    eventBus = eventBus,
    processor = _ => value
  )

  @inline def -->[V](processor: Ev => V, eventBus: EventBus[V]): EventPropReceiver[Ev, V] = new EventPropReceiver(
    key = eventProp,
    eventBus = eventBus,
    processor = processor
  )

  @inline def -->[V](processor: () => V, eventBus: EventBus[V]): EventPropReceiver[Ev, V] = new EventPropReceiver(
    key = eventProp,
    eventBus = eventBus,
    processor = (_: Ev) => processor()
  )
}


