package com.raquo.laminar.keys

import com.raquo.airstream.core.Observer
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.emitter.{EventPropEmitter, EventPropTransformation}
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

class ReactiveEventProp[Ev <: dom.Event](override val name: String) extends EventProp[Ev](name) {

  def config[El <: ReactiveElement[dom.Element]](useCapture: Boolean = false): EventPropTransformation[Ev, Ev, El] = {
    new EventPropTransformation(this, useCapture, processor = (ev: Ev, _: El) => Some(ev))
  }

  def -->[El <: ReactiveElement[dom.Element]](
    observer: Observer[Ev]
  ): EventPropEmitter[Ev, Ev, El] = new EventPropEmitter[Ev, Ev, El](
    observer,
    this,
    useCapture = false,
    processor = (ev: Ev, _: El) => Some(ev)
  )

}
