package com.raquo.laminar.emitter

import com.raquo.airstream.core.Observer
import com.raquo.airstream.eventbus.EventBus
import com.raquo.airstream.eventstream.EventStream
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.modifiers.EventPropBinder
import org.scalajs.dom

/**
  * This class represents a set of transformations that can be applied to events feeding into an [[EventPropBinder]]
  * EventPropTransformation-s are immutable, so can be reused by multiple setters.
  *
  * Example syntax: `input(onChange().preventDefault.mapTo(true) --> myBooleanWriteBus)`
  *
  * Note: Params are protected to avoid confusing autocomplete options (e.g. "useCapture")
  *
  * @param shouldUseCapture (false means using bubble mode)
  *                         See `useCapture` docs here: https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener
  * @param processor
  *          Processes incoming events before they're passed to the next processor or to the listening EventBus.
  *          Returns an Option of the processed value. If None, the value should not passed down the chain.
  */
class EventPropTransformation[Ev <: dom.Event, V](
  protected val eventProp: EventProp[Ev],
  protected val shouldUseCapture: Boolean = false,
  protected val processor: Ev => Option[V]
) {

  // @TODO[Performance,Elegance] Is it possible to move these --> methods to ReactiveEventProp?
  // We can't have them in both places, because then type inference does not work

  @inline def -->[El <: ReactiveElement.Base](observer: Observer[V]): EventPropBinder[Ev] = {
    EventPropBinder(observer, eventProp, shouldUseCapture, processor)
  }

  @inline def -->[El <: ReactiveElement.Base](onNext: V => Unit): EventPropBinder[Ev] = {
    -->(Observer(onNext))
  }

  @inline def -->[BusEv >: V, El <: ReactiveElement.Base](eventBus: EventBus[BusEv]): EventPropBinder[Ev] = {
    -->(eventBus.writer)
  }

  /** Use capture mode (v=true) or bubble mode (v=false)
    *
    * Note that unlike `preventDefault` config which applies to individual events,
    * useCapture is used to install the listener onto the DOM node in the first place.
    *
    * See `useCapture` docs here: https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener
    */
  def useCapture: EventPropTransformation[Ev, V] = {
    new EventPropTransformation(eventProp, shouldUseCapture = true, processor = processor)
  }

  /** Use standard bubble propagation mode. You don't need to call this unless you set `useCapture` previously.
    *
    * See `useCapture` docs here: https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener
    */
  def useBubbleMode: EventPropTransformation[Ev, V] = {
    new EventPropTransformation(eventProp, shouldUseCapture = false, processor = processor)
  }

  /** Prevent default browser action for the given event (e.g. following the link when it is clicked)
    * https://developer.mozilla.org/en-US/docs/Web/API/Event/preventDefault
    *
    * Note: this is just a standard transformation, so it will be fired in whatever order you have applied it.
    * So for example, you can [[filter]] events before applying this, preventing default action only for certain events.
    *
    * Example: `input(onKeyUp().filter(ev => ev.keyCode == KeyCode.Tab).preventDefault --> tabKeyUpBus)`
    */
  def preventDefault: EventPropTransformation[Ev, V] = {
    copy(newProcessor = ev => {
      val value = processor(ev)
      ev.preventDefault()
      value
    })
  }

  /** Propagation here refers to DOM Event bubbling or capture propagation.
    * https://developer.mozilla.org/en-US/docs/Web/API/Event/stopPropagation
    *
    * Note: this is just a standard transformation, so it will be fired in whatever order you have applied it.
    * So for example, you can [[filter]] events before applying this, preventing default action only for certain events.
    *
    * Example: `div(onClick.filter(isGoodClick).stopPropagation --> goodClickBus)`
    */
  def stopPropagation: EventPropTransformation[Ev, V] = {
    copy(newProcessor = ev => {
      val value = processor(ev)
      ev.stopPropagation()
      value
    })
  }

  /** Values that do not pass will not propagate down the chain and into the emitter. */
  def filter(passes: V => Boolean): EventPropTransformation[Ev, V] = {
    copy(newProcessor = ev => processor(ev).filter(passes))
  }

  def map[V2](project: V => V2): EventPropTransformation[Ev, V2] = {
    copy(newProcessor = ev => processor(ev).map(project))
  }

  def mapTo[V2](value: => V2): EventPropTransformation[Ev, V2] = {
    copy(newProcessor = ev => processor(ev).map(_ => value))
  }

  def mapToValue[V2](value: V2): EventPropTransformation[Ev, V2] = {
    copy(newProcessor = ev => processor(ev).map(_ => value))
  }

  def collect[V2](pf: PartialFunction[V, V2]): EventPropTransformation[Ev, V2] = {
    copy(newProcessor = ev => processor(ev).collect(pf))
  }

  // Don't need the extra codegen overhead of a case class
  private def copy[V2](newProcessor: Ev => Option[V2]): EventPropTransformation[Ev, V2] = {
    new EventPropTransformation[Ev, V2](eventProp, shouldUseCapture, newProcessor)
  }
}

object EventPropTransformation {

  // This method lives in companion object so that I don't have to expose EPT's protected vals.
  // They don't need to be public, and would have polluted autocomplete with confusing names.
  def toEventStream[Ev <: dom.Event, V](
    ept: EventPropTransformation[Ev, V],
    element: ReactiveElement.Base
  ): EventStream[V] = {
    element
      .events(ept.eventProp, useCapture = ept.shouldUseCapture)
      .map(ept.processor)
      .collect { case Some(ev) => ev } // @TODO[Performance] Can I eliminate this second EventStream instance?
  }
}
