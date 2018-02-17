package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.Transaction

// @TODO[Airstream] needs testing

// @TODO[API] Is it desirable that we re-emit these events when this stream is re-started, or should that only happen once?
// @TODO[API] This is how XStream does it, I haven't compared with other libs
/** This event stream emits a sequence of events every time it is started */
class SeqEventStream[A](events: Seq[A]) extends EventStream[A] {

  override protected[airstream] val topoRank: Int = 1

  override protected[this] def onStart(): Unit = {
    super.onStart()
    events.foreach(event => new Transaction(fire(event, _)))
  }

}
