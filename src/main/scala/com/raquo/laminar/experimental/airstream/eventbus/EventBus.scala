package com.raquo.laminar.experimental.airstream.eventbus

import com.raquo.laminar.experimental.airstream.eventstream.EventStream

/** EventBus combines a WriteBus and a stream of its events.
  *
  * `writer` and `events` are made separate to allow you to manage permissions.
  * For example, you can pass only the `writer` instance to a function that
  * should only have access to writing events, not reading all events from the bus.
  */
class EventBus[A] {

  val writer: WriteBus[A] = new WriteBus[A]

  val events: EventStream[A] = writer.stream
}
