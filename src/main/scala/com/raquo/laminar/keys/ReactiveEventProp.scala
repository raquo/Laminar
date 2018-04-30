package com.raquo.laminar.keys

import com.raquo.airstream.core.Observer
import com.raquo.airstream.eventbus.EventBus
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.emitter.{EventPropEmitter, EventPropTransformation}
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

class ReactiveEventProp[Ev <: dom.Event](override val name: String) extends EventProp[Ev](name) {

  def config[El <: ReactiveElement[dom.Element]](useCapture: Boolean = false): EventPropTransformation[Ev, Ev] = {
    new EventPropTransformation(this, useCapture, processor = Some(_))
  }

  def -->[El <: ReactiveElement[dom.Element]](
    observer: Observer[Ev]
  ): EventPropEmitter[Ev, Ev, El] = new EventPropEmitter[Ev, Ev, El](
    observer,
    this,
    useCapture = false,
    processor = Some(_)
  )

  // @TODO[API] BusEv type param requires users to specify El type param in the --> method above when passing e.g. Observer(_ => ...), that's inconvenient
  /** Note: Without Ev2 this method would not work for TypedTargetMouseEvent events going into MouseEvent bus.
    *       Instead, EventPropTransformation.--> would apply instead, which is less efficient.
    */
  @inline def -->[BusEv >: Ev, El <: ReactiveElement[dom.Element]](eventBus: EventBus[BusEv]): EventPropEmitter[Ev, Ev, El] = {
    -->(eventBus.writer)
  }

}
