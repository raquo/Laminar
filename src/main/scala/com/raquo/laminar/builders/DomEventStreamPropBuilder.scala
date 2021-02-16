package com.raquo.laminar.builders

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.web.DomEventStream
import com.raquo.domtypes.generic.builders.EventPropBuilder
import org.scalajs.dom

class DomEventStreamPropBuilder(
  eventTarget: dom.EventTarget
) extends EventPropBuilder[EventStream, dom.Event] {

  override protected def eventProp[Ev <: dom.Event](eventKey: String): EventStream[Ev] = {
    DomEventStream[Ev](eventTarget, eventKey = eventKey, useCapture = false)
  }

  /** Create custom event prop (e.g. for Web Components)
    *
    * @param eventKey - event type used as the first param to DOM addEventListener, e.g. "click" for onClick events
    *
    * @tparam Ev      - type of event that will be emitted
    */
  @inline def customEventProp[Ev <: dom.Event](eventKey: String): EventStream[Ev] = eventProp(eventKey)
}
