package com.raquo.laminar.builders

import com.raquo.airstream.core.Transaction
import com.raquo.airstream.eventstream.EventStream
import org.scalajs.dom

import scala.scalajs.js

// @TODO[API,Integrity] Use this for element events too
// @TODO[API] This could potentially replace EventPropTransformation
class DomEventStream[Ev <: dom.Event](eventTarget: dom.EventTarget, eventKey: String, useCapture: Boolean) extends EventStream[Ev] {

  // @TODO[API] We need to figure out a better protection mechanism in Airstream, this only works for Laminar because it shares com.raquo with Airstream
  override protected[raquo] val topoRank: Int = 1

  val eventHandler: js.Function1[Ev, Unit] = (ev: Ev) => new Transaction(fire(ev, _))

  override protected[this] def onStart(): Unit = {
    eventTarget.addEventListener(eventKey, eventHandler, useCapture)
  }

  override protected[this] def onStop(): Unit = {
    eventTarget.removeEventListener(eventKey, eventHandler, useCapture)
  }
}
