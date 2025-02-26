package com.raquo.laminar.domapi

import com.raquo.laminar.keys.EventProcessor
import com.raquo.laminar.modifiers.EventListener
import org.scalajs.dom

trait DomEvents {

  def addEventListener[Ev <: dom.Event](
    element: dom.Element,
    listener: EventListener[Ev, _]
  ): Unit = {
    // println(s"> Adding listener on ${DomApi.debugNodeDescription(element.ref)} for `${eventPropSetter.key.name}` with useCapture=${eventPropSetter.useCapture}")
    element.addEventListener(
      `type` = EventProcessor.eventProp(listener.eventProcessor).name,
      listener = listener.domCallback,
      options = listener.options
    )
  }

  def removeEventListener[Ev <: dom.Event](
    element: dom.Element,
    listener: EventListener[Ev, _]
  ): Unit = {
    element.removeEventListener(
      `type` = EventProcessor.eventProp(listener.eventProcessor).name,
      listener = listener.domCallback,
      options = listener.options
    )
  }
}
