package com.raquo.laminar.keys

import com.raquo.airstream.core.{EventStream, Observable, Observer, Sink}
import com.raquo.laminar.api.UnitArrowsFeature
import com.raquo.laminar.modifiers.Binder
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

class LockedEventKey[Ev <: dom.Event, -In, +Out](
  eventProcessor: EventProcessor[Ev, In],
  composer: EventStream[In] => Observable[Out]
) {

  def -->(sink: Sink[Out]): Binder.Base = {
    Binder { el =>
      val observable = composer(el.events(eventProcessor))
      ReactiveElement.bindSink[Out](el, observable)(sink)
    }
  }

  @inline def -->(onNext: Out => Unit): Binder.Base = {
    -->(Observer(onNext))
  }

  @inline def -->(onNext: => Unit)(implicit evidence: UnitArrowsFeature): Binder.Base = {
    -->(Observer[Any](_ => onNext))
  }
}
