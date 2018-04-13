package com.raquo.laminar.implicits

import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.emitter.EventPropTransformation
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

/** These implicits will only be tried if none of the implicits in [[Implicits]] fits the bill.
  *
  * Each implicit conversion defined in this file must have a clearly documented reason to be put here.
  *
  * For the general idea, see https://stackoverflow.com/a/1887678/2601788
  */
trait LowPriorityImplicits {

  /** Note: this must be kept at lower priority than [[Implicits.eventPropToEventPropOps]]
    * because of the conflicting `-->` methods.
    * Both conversions would achieve essentially the same outcome, but this one is less efficient.
    */
  @inline implicit def eventPropToEventPropTransformation[Ev <: dom.Event, El <: ReactiveElement[dom.Element]](
    eventProp: EventProp[Ev]
  ): EventPropTransformation[Ev, Ev, El] = {
    new EventPropTransformation(eventProp, useCapture = false, processor = (ev: Ev, _: El) => Some(ev))
  }
}
