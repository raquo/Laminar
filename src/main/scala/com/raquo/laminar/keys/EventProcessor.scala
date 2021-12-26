package com.raquo.laminar.keys

import com.raquo.airstream.core.Sink
import com.raquo.laminar.DomApi
import com.raquo.laminar.modifiers.EventListener
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

/**
  * This class represents a sequence of transformations that are applied to events feeding into an [[EventListener]]
  * EventProcessor-s are immutable, so can be reused by multiple setters.
  *
  * Example syntax: `input(onChange().preventDefault.mapTo(true) --> myBooleanWriteBus)`
  *
  * Note: Params are protected to avoid confusing autocomplete options (e.g. "useCapture")
  *
  * @param shouldUseCapture (false means using bubble mode)
  *                         See `useCapture` docs here: https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener
  *
  * @param processor        Processes incoming events before they're passed to the next processor or to the listening EventBus.
  *                         Returns an Option of the processed value. If None, the value should not passed down the chain.
  */
class EventProcessor[Ev <: dom.Event, V](
  protected val eventProp: EventProp[Ev],
  protected val shouldUseCapture: Boolean = false,
  protected val processor: Ev => Option[V]
) {

  @inline def -->(sink: Sink[V]): EventListener[Ev, V] = {
    this --> (sink.toObserver.onNext(_))
  }

  @inline def -->[El <: ReactiveElement.Base](onNext: V => Unit): EventListener[Ev, V] = {
    new EventListener[Ev, V](this, onNext)
  }

  /** Use capture mode (v=true) or bubble mode (v=false)
    *
    * Note that unlike `preventDefault` config which applies to individual events,
    * useCapture is used to install the listener onto the DOM node in the first place.
    *
    * See `useCapture` docs here: https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener
    */
  def useCapture: EventProcessor[Ev, V] = {
    new EventProcessor(eventProp, shouldUseCapture = true, processor = processor)
  }

  /** Use standard bubble propagation mode. You don't need to call this unless you set `useCapture` previously.
    *
    * See `useCapture` docs here: https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener
    */
  def useBubbleMode: EventProcessor[Ev, V] = {
    new EventProcessor(eventProp, shouldUseCapture = false, processor = processor)
  }

  /** Prevent default browser action for the given event (e.g. following the link when it is clicked)
    * https://developer.mozilla.org/en-US/docs/Web/API/Event/preventDefault
    *
    * Note: this is just a standard processor, so it will be fired in whatever order you have applied it.
    * So for example, you can [[filter]] events before applying this, preventing default action only for certain events.
    *
    * Example: `input(onKeyUp().filter(ev => ev.keyCode == KeyCode.Tab).preventDefault --> tabKeyUpBus)`
    */
  def preventDefault: EventProcessor[Ev, V] = {
    withNewProcessor { ev =>
      processor(ev).map { value =>
        ev.preventDefault()
        value
      }
    }
  }

  /** Propagation here refers to DOM Event bubbling or capture propagation.
    * https://developer.mozilla.org/en-US/docs/Web/API/Event/stopPropagation
    *
    * Note: this is just a standard processor, so it will be fired in whatever order you have applied it.
    * So for example, you can [[filter]] events before applying this, propagation will be stopped only for certain events.
    *
    * Example: `div(onClick.filter(isGoodClick).stopPropagation --> goodClickBus)`
    */
  def stopPropagation: EventProcessor[Ev, V] = {
    withNewProcessor { ev =>
      processor(ev).map { value =>
        ev.stopPropagation()
        value
      }
    }
  }

  /** This method prevents other listeners of the same event from being called.
    * If several listeners are attached to the same element for the same event type,
    * they are called in the order in which they were added. If stopImmediatePropagation()
    * is invoked during one such call, no remaining listeners will be called.
    *
    * MDN https://developer.mozilla.org/en-US/docs/Web/API/Event/stopImmediatePropagation
    *
    * Note: this is just a standard processor, so it will be fired in whatever order you have applied it.
    * So for example, you can [[filter]] events before applying this, propagation will be stopped only for certain events.
    *
    * Example: `div(onClick.filter(isGoodClick).stopImmediatePropagation --> goodClickBus)`
    */
  def stopImmediatePropagation: EventProcessor[Ev, V] = {
    withNewProcessor { ev =>
      processor(ev).map { value =>
        ev.stopImmediatePropagation()
        value
      }
    }
  }

  /** Values that do not pass will not propagate down the chain and into the emitter. */
  def filter(passes: V => Boolean): EventProcessor[Ev, V] = {
    withNewProcessor(ev => processor(ev).filter(passes))
  }

  def collect[V2](pf: PartialFunction[V, V2]): EventProcessor[Ev, V2] = {
    withNewProcessor(ev => processor(ev).collect(pf))
  }

  def map[V2](project: V => V2): EventProcessor[Ev, V2] = {
    withNewProcessor(ev => processor(ev).map(project))
  }

  /** Same as `map(_ => value)`
    *
    * Note: `value` will be re-evaluated every time the event is fired
    */
  def mapTo[V2](value: => V2): EventProcessor[Ev, V2] = {
    withNewProcessor(ev => processor(ev).map(_ => value))
  }

  /** Like mapTo, but with strict evaluation of the value */
  def mapToStrict[V2](value: V2): EventProcessor[Ev, V2] = {
    withNewProcessor(ev => processor(ev).map(_ => value))
  }

  /** Get the original event. You might want to call this in a chain, after some other processing. */
  def mapToEvent: EventProcessor[Ev, Ev] = {
    withNewProcessor(ev => processor(ev).map(_ => ev))
  }

  /** Get the value of `event.target.value` */
  def mapToValue: EventProcessor[Ev, String] = {
    withNewProcessor { ev =>
      processor(ev).map { _ =>
        // @TODO[Warn] Throw or console.log a warning here if using on the wrong element type
        DomApi.getValue(ev.target.asInstanceOf[dom.Element]).getOrElse("")
      }
    }
  }

  /** Get the value of `event.target.checked` */
  def mapToChecked: EventProcessor[Ev, Boolean] = {
    withNewProcessor { ev =>
      processor(ev).map { _ =>
        // @TODO[Warn] Throw or console.log a warning here if using on the wrong element type
        DomApi.getChecked(ev.target.asInstanceOf[dom.Element]).getOrElse(false)
      }
    }
  }

  /** Evaluate `f` if the value was filtered out up the chain. For example:
    *
    *     onClick.filter(isRightClick).orElseEval(_.preventDefault()) --> observer
    *
    * This observer will fire only on right clicks, and for events that aren't right
    * clicks, ev.preventDefault() will be called instead.
    */
  def orElseEval(f: Ev => Unit): EventProcessor[Ev, V] = {
    withNewProcessor { ev =>
      val result = processor(ev)
      if (result.isEmpty) {
        f(ev)
      }
      result
    }
  }

  /** (originalEvent, accumulatedValue) => newAccumulatedValue
    *
    * Unlike other processors, this one will fire regardless of .filter-s up the chain.
    * Instead, if the event was filtered, project will receive None as accumulatedValue.
    * The output of project should be Some(newValue), or None if you want to filter out this event.
    */
  def mapRaw[V2](project: (Ev, Option[V]) => Option[V2]): EventProcessor[Ev, V2] = {
    withNewProcessor { ev =>
      project(ev, processor(ev))
    }
  }

  /** Write the resulting string into `event.target.value`.
    * You can only do this on elements that have a value property - input, textarea, select
    */
  def setAsValue(implicit stringEvidence: V <:< String): EventProcessor[Ev, V] = {
    withNewProcessor { ev =>
      processor(ev).map { value =>
        val nextInputValue = stringEvidence(value)
        // @TODO[Warn] Console.log a warning here if using on the wrong element type
        DomApi.setValue(ev.target.asInstanceOf[dom.Element], nextInputValue)
        value
      }
    }
  }

  /** Write the resulting boolean into `event.target.checked`.
    * You can only do this on checkbox or radio button elements.
    *
    * Warning: if using this, do not use preventDefault. The browser may override the value you set here.
    */
  def setAsChecked(implicit boolEvidence: V <:< Boolean): EventProcessor[Ev, V] = {
    withNewProcessor { ev =>
      processor(ev).map { value =>
        val nextChecked = boolEvidence(value)
        // @TODO[Warn] Console.log a warning here if using on the wrong element type
        DomApi.setChecked(ev.target.asInstanceOf[dom.Element], nextChecked)
        value
      }
    }
  }

  private def withNewProcessor[V2](newProcessor: Ev => Option[V2]): EventProcessor[Ev, V2] = {
    new EventProcessor[Ev, V2](eventProp, shouldUseCapture, newProcessor)
  }
}

object EventProcessor {

  def empty[Ev <: dom.Event](eventProp: EventProp[Ev], shouldUseCapture: Boolean = false): EventProcessor[Ev, Ev] = {
    new EventProcessor(eventProp, shouldUseCapture, Some(_))
  }

  // These methods are only exposed publicly via companion object
  // to avoid polluting autocomplete when chaining EventProcessor-s

  @inline def eventProp[Ev <: dom.Event](prop: EventProcessor[Ev, _]): EventProp[Ev] = prop.eventProp

  @inline def shouldUseCapture(prop: EventProcessor[_, _]): Boolean = prop.shouldUseCapture

  @inline def processor[Ev <: dom.Event, Out](prop: EventProcessor[Ev, Out]): Ev => Option[Out] = prop.processor
}
