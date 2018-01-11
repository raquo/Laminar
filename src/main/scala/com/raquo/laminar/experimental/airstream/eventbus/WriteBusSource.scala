package com.raquo.laminar.experimental.airstream.eventbus

import com.raquo.laminar.experimental.airstream.eventstream.EventStream
import com.raquo.laminar.experimental.airstream.ownership.{Owned, Owner}

// @TODO Rename? Maybe something with "Subscription"? Although nah...
class WriteBusSource[A](
  eventBusStream: WriteBusStream[A],
  val sourceStream: EventStream[A],
  owner: Owner
) extends Owned {

  eventBusStream.addSource(this)

  override protected[this] def registerWithOwner(): Unit = {
    owner.own(this)
  }

  def removeSource(): Unit = {
    kill()
    owner.onKilledExternally(this)
  }

  override private[airstream] def kill(): Unit = {
    // @TODO need to do anything else here, or maybe in removeSource? like onStop?
    eventBusStream.removeSource(this)
  }
}
