package com.raquo.laminar.experimental.airstream.eventbus

import com.raquo.laminar.experimental.airstream.eventstream.EventStream
import com.raquo.laminar.experimental.airstream.ownership.{Owned, Owner}

// @TODO[Naming] Maybe something with "Subscription"? Although nah...
class EventBusSource[A](
  eventBusStream: EventBusStream[A],
  val sourceStream: EventStream[A],
  override protected[this] val owner: Owner
) extends Owned {

  init()

  eventBusStream.addSource(this)

  override protected[this] def onKilled(): Unit = {
    eventBusStream.removeSource(this)
  }

  /** Remove this source stream from this event bus */
  override def kill(): Unit = {
    onKilled()
    owner.onKilledExternally(this)
  }
}
