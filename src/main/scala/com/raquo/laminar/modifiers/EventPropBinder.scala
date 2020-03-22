package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observer
import com.raquo.airstream.ownership.{DynamicSubscription, Subscription}
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.api.Laminar.onClick
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

import scala.scalajs.js

/** @param useCapture – see https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener about "useCapture"
  */
class EventPropBinder[Ev <: dom.Event](
  val key: EventProp[Ev],
  val value: Ev => Unit,
  val useCapture: Boolean
) extends Binder[ReactiveElement.Base] {

  /** To make sure that you remove the event listener successfully in JS DOM, you need to
    * provide the same Javascript callback function that was originally added as a listener.
    * However, the implicit conversion from a Scala function to a JS function creates a new
    * JS function every time, so we would never get referentially equal JS functions if we
    * used the Scala-to-JS conversion more than once. Therefore, we need to perform that
    * conversion only once and save the result. This method encapsulates such conversion.
    */
  val domValue: js.Function1[Ev, Unit] = value

  override def bind(element: ReactiveElement.Base): DynamicSubscription = {
    ReactiveElement.bindSubscription(element){ c =>
      ReactiveElement.addEventListener(element, this)
      new Subscription(c.owner, cleanup = () => {
        ReactiveElement.removeEventListener(element, this)
      })
    }
  }

  override def equals(that: Any): Boolean = {
    that match {
      case setter: EventPropBinder[_] if (key == setter.key) && (domValue == setter.domValue) => true
      case _ => false
    }
  }
}

object EventPropBinder {

  def apply[Ev <: dom.Event, V, El <: ReactiveElement.Base](
    observer: Observer[V],
    eventProp: EventProp[Ev],
    useCapture: Boolean,
    processor: Ev => Option[V]
  ): EventPropBinder[Ev] = {

    val callback = (ev: Ev) => {
      if (
        ev.defaultPrevented
          && eventProp == onClick
          && ev.target.asInstanceOf[dom.Element].tagName == "INPUT" // ugly but performy
          && ev.target.asInstanceOf[dom.html.Input].`type` == "checkbox"
      ) {
        // Special case: See README and/or https://stackoverflow.com/a/32710212/2601788
        // @TODO[API] Should this behaviour extend to all checkbox.onClick events by default?
        js.timers.setTimeout(0)(processor(ev).foreach(observer.onNext))
        ()
      } else {
        processor(ev).foreach(observer.onNext)
      }
    }

    new EventPropBinder[Ev](eventProp, callback, useCapture = useCapture)
  }

}
