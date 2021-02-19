package com.raquo.laminar.keys

import com.raquo.airstream.core.{EventStream, Observable, Observer}
import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.api.Laminar.Var
import com.raquo.laminar.modifiers.Binder
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

class LockedEventKey[Ev <: dom.Event, In, Out](
  eventProcessor: EventProcessor[Ev, In],
  composer: EventStream[In] => Observable[Out]
) {

  @inline def -->(observer: Observer[Out]): Binder[ReactiveElement.Base] = {
    Binder { el =>
      ReactiveElement.bindObserver[Out](el, composer(el.events(eventProcessor)))(observer)
    }
  }

  @inline def -->(onNext: Out => Unit): Binder[ReactiveElement.Base] = {
    -->(Observer(onNext))
  }

  @inline def -->(eventBus: EventBus[Out]): Binder[ReactiveElement.Base] = {
    -->(eventBus.writer)
  }

  @inline def -->(targetVar: Var[Out]): Binder[ReactiveElement.Base] = {
    -->(targetVar.writer)
  }
}
