package com.raquo.laminar.modifiers

import com.raquo.airstream.ownership.{DynamicSubscription, Subscription}
import com.raquo.laminar.DomApi
import com.raquo.laminar.keys.EventProcessor
import com.raquo.laminar.lifecycle.MountContext
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

import scala.scalajs.js

class EventListener[Ev <: dom.Event, Out](
  val eventProcessor: EventProcessor[Ev, Out],
  val callback: Out => Unit
) extends Binder[ReactiveElement.Base] {

  /** To make sure that you remove the event listener successfully in JS DOM, you need to
    * provide the same Javascript callback function that was originally added as a listener.
    * However, the implicit conversion from a Scala function to a JS function creates a new
    * JS function every time, so we would never get referentially equal JS functions if we
    * used the Scala-to-JS conversion more than once. Therefore, we need to perform that
    * conversion only once and save the result. This method encapsulates such conversion.
    */
  val domCallback: js.Function1[Ev, Unit] = ev => {
    val processor = EventProcessor.processor(eventProcessor)
    processor(ev).foreach(callback)
  }

  @deprecated("Renamed: Use `domCallback` instead of `domValue`", "0.12.0")
  val domValue: js.Function1[Ev, Unit] = domCallback

  override def bind(element: ReactiveElement.Base): DynamicSubscription = {
    bind(element, unsafePrepend = false)
  }

  private[laminar] def bind(element: ReactiveElement.Base, unsafePrepend: Boolean): DynamicSubscription = {
    if (element.indexOfEventListener(this) == -1) {
      //println(s"> bind ${EventProcessor.eventProp(eventProcessor).name} listener to " + element.ref.outerHTML + s" (prepend = $unsafePrepend)")
      val subscribe = (ctx: MountContext[ReactiveElement.Base]) => {
        //println(s"> add ${EventProcessor.eventProp(eventProcessor).name} listener to " + element.ref.outerHTML + s" (prepend = $unsafePrepend)")
        DomApi.addEventListener(element, this)
        new Subscription(ctx.owner, cleanup = () => {
          val listenerIndex = element.indexOfEventListener(this)
          if (listenerIndex != -1) {
            //println(s"> remove ${EventProcessor.eventProp(eventProcessor).name} listener from " + element.ref.outerHTML + s" (prepend = $unsafePrepend)")
            element.removeEventListener(listenerIndex)
            DomApi.removeEventListener(element, this)
          } else {
            // @TODO[Warn] Issue a warning, this isn't supposed to happen
            //println(">> Trying to remove an listener that isn't there...")
          }
        })
      }

      val sub = if (unsafePrepend) {
        ReactiveElement.unsafeBindPrependSubscription(element)(subscribe)
      } else {
        ReactiveElement.bindSubscriptionUnsafe(element)(subscribe)
      }

      element.addEventListener(this, unsafePrepend)

      sub
    } else {
      // @TODO[Warn] Issue a warning, this isn't supposed to happen
      //println(">> Trying to add an listener that's already there...")
      ReactiveElement.bindCallback(element)(_ => ())
    }
  }

  override def toString: String = s"EventListener(${EventProcessor.eventProp(eventProcessor).name})"

  // I don't think this is needed, it rounds down to reference equality anyway.
  //override def equals(that: Any): Boolean = {
  //  that match {
  //    case setter: EventListener[_, _] if (eventProcessor == setter.eventProcessor) && (domCallback == setter.domCallback) => true
  //    case _ => false
  //  }
  //}
}

object EventListener {

  /** Any kind of event listener */
  type Base = EventListener[_ <: dom.Event, _]
}
