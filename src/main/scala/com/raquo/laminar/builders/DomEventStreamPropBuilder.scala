package com.raquo.laminar.builders

import com.raquo.airstream.eventstream.EventStream
import com.raquo.domtypes.generic.builders.EventPropBuilder
import org.scalajs.dom

class DomEventStreamPropBuilder(
  eventTarget: dom.EventTarget
) extends EventPropBuilder[EventStream, dom.Event] {

  override protected def eventProp[Ev <: dom.Event](eventKey: String): EventStream[Ev] = {
    new DomEventStream[Ev](eventTarget, eventKey = eventKey, useCapture = false)
  }
}
