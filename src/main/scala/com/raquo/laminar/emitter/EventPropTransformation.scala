package com.raquo.laminar.emitter

import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.experimental.airstream.core.Observer
import com.raquo.laminar.experimental.airstream.eventbus.EventBus
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
class EventPropTransformation[Ev <: dom.Event, V, -El <: ReactiveElement[dom.Element]](
  protected val eventProp: EventProp[Ev],
  protected val useCapture: Boolean = false,
  protected val processor: (Ev, El) => Option[V]
) {

  /** Prevent default browser action for the given event (e.g. following the link when it is clicked)
    * https://developer.mozilla.org/en-US/docs/Web/API/Event/preventDefault
    *
    * Note: this is just a standard transformation, so it will be fired in whatever order you have applied it.
    * So for example, you can [[filter]] events before applying this, preventing default action only for certain events.
    *
    * Example: `input(onKeyUp().filter(ev => ev.keyCode == KeyCode.Tab).preventDefault --> tabKeyUpBus)`
    */
  def preventDefault: EventPropTransformation[Ev, V, El] = {
    copy(newProcessor = (ev, thisNode) => {
      val value = processor(ev, thisNode)
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
  def stopPropagation: EventPropTransformation[Ev, V, El] = {
    copy(newProcessor = (ev, thisNode) => {
      val value = processor(ev, thisNode)
      ev.stopPropagation()
      value
    })
  }

  /** Values that do not pass will not propagate down the chain and into the emitter. */
  def filter(passes: V => Boolean): EventPropTransformation[Ev, V, El] = {
    copy(newProcessor = (ev, thisNode) => processor(ev, thisNode).filter(passes))
  }

  def map[V2](project: V => V2): EventPropTransformation[Ev, V2, El] = {
    copy(newProcessor = (ev, thisNode) => processor(ev, thisNode).map(project))
  }

  /** Note: similar to XStream, `value` is passed as call-by-value, not call-by-name,
    * i.e. it's evaluated only once before being passed to this method, it is not
    * re-evaluated on every event.
    *
    * TODO[API]: this is consistent with XStream, but do other Scala libs behave the same?
    */
  def mapTo[V2](value: V2): EventPropTransformation[Ev, V2, El] = {
    copy(newProcessor = (ev, thisNode) => processor(ev, thisNode).map(_ => value))
  }

  def collect[V2](pf: PartialFunction[V, V2]): EventPropTransformation[Ev, V2, El] = {
    copy(newProcessor = (ev, thisNode) => processor(ev, thisNode).collect(pf))
  }

  // Don't need the extra codegen overhead of a case class
  private def copy[V2, El2 <: El](newProcessor: (Ev, El2) => Option[V2]): EventPropTransformation[Ev, V2, El2] = {
    new EventPropTransformation[Ev, V2, El2](eventProp, useCapture, newProcessor)
  }

  @inline def -->[El2 <: El](observer: Observer[V]): EventPropEmitter[Ev, V, El2] = {
    new EventPropEmitter(observer, eventProp, useCapture, processor)
  }

  @inline def -->[BusEv >: V, El2 <: El](eventBus: EventBus[BusEv]): EventPropEmitter[Ev, V, El2] = {
    -->(eventBus.writer)
  }
}
