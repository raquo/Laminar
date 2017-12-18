package com.raquo.laminar.streams

import com.raquo.xstream.XStream

/** Encapsulates a producer of events and exposes the resulting stream.
  *
  * Events will only be sent to the output stream if it has at least one listener.
  */
class EventBus[A]() extends WriteBus[A] {

  /** Stream of produced events */
  val $: XStream[A] = XStream.create(producer)
}
