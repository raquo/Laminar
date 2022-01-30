package com.raquo.laminar.keys

import com.raquo.airstream.core.{EventStream, Observable, Observer, Sink}
import com.raquo.laminar.modifiers.Binder
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

class LockedEventKey[Ev <: dom.Event, -In, +Out](
  eventProcessor: EventProcessor[Ev, In],
  composer: EventStream[In] => Observable[Out]
) {

  def -->(sink: Sink[Out]): Binder[ReactiveElement.Base] = {
    Binder { el =>
      ReactiveElement.bindSink[Out](el, composer(el.events(eventProcessor)))(sink)
    }
  }

  @inline def -->(onNext: Out => Unit): Binder[ReactiveElement.Base] = {
    -->(Observer(onNext))
  }
}
