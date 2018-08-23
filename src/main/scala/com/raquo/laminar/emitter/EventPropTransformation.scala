package com.raquo.laminar.emitter

import com.raquo.airstream.core.Observer
import com.raquo.airstream.eventbus.EventBus
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

/**
  * This class represents a set of transformations that can be applied to events feeding into an [[EventPropEmitter]]
  * EventPropTransformation-s are immutable, so can be reused by multiple emitters.
  *
  * Example syntax: `input(onChange().preventDefault.mapTo(true) --> myBooleanWriteBus)`
  *
  * Note: Params are protected to avoid confusing autocomplete options (e.g. "useCapture")
  *
  * @param useCapture
  *          This is not a part of the processing pipeline since it takes effect when we register the event listener,
  *          not when a new event comes in. Therefore this option can only be set on initialization.
  *          See `useCapture` docs here: https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener
  * @param processor
  *          Processes incoming events before they're passed to the next processor or to the listening EventBus.
  *          Returns an Option of the processed value. If None, the value should not passed down the chain.
  */
class EventPropTransformation[Ev <: dom.Event, V](
  protected val eventProp: EventProp[Ev],
  protected val useCapture: Boolean = false,
  protected val processor: Ev => Option[V]
) {

  // @TODO[Performance,Elegance] Is it possible to move these --> methods to ReactiveEventProp?
  // We can't have them in both places, because then type inference does not work

  @inline def -->[El <: ReactiveElement[dom.Element]](observer: Observer[V]): EventPropEmitter[Ev, V, El] = {
    new EventPropEmitter(observer, eventProp, useCapture, processor)
  }

  @inline def -->[El <: ReactiveElement[dom.Element]](onNext: V => Unit): EventPropEmitter[Ev, V, El] = {
    -->(Observer(onNext))
  }

  @inline def -->[BusEv >: V, El <: ReactiveElement[dom.Element]](eventBus: EventBus[BusEv]): EventPropEmitter[Ev, V, El] = {
    -->(eventBus.writer)
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
    new EventPropTransformation[Ev, V2](eventProp, useCapture, newProcessor)
  }
}
