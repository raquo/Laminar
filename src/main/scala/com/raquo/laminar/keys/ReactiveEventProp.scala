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

}
