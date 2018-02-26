package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.Transaction
import com.raquo.laminar.experimental.airstream.features.SingleParentObservable

import scala.scalajs.js
import scala.scalajs.js.timers.SetTimeoutHandle

// @TODO[Test] Verify debounce

/** [[DebounceEventStream]] emits the last event emitted by `parent`, but only after `delayFromLastEventMillis` ms
  * has elapsed since `parent` emitted the previous event. So essentially, this stream emits the parent's last event
  * once the parent stops emitting events for a while.
  *
  * [[DebounceEventStream]] is an async filter, emitting some of the input events, and with a delay, whereas
  * [[ThrottleEventStream]] acts like a synchronous filter on input events
  */
class DebounceEventStream[A](
  override protected[this] val parent: EventStream[A],
  delayFromLastEventMillis: Int
) extends EventStream[A] with SingleParentObservable[A, A] {

  private[this] var maybeLastTimeoutHandle: js.UndefOr[SetTimeoutHandle] = js.undefined

  /** All emitted events are delayed, so we fire up a new transaction and reset topoRank */
  override protected[airstream] val topoRank: Int = 1

  /** Every time [[parent]] emits an event, we clear the previous timer and set a new one.
    * This stream only emits when the parent has stopped emitting for [[delayFromLastEventMillis]] ms.
    */
  override protected[airstream] def onNext(nextValue: A, transaction: Transaction): Unit = {
    maybeLastTimeoutHandle.foreach(js.timers.clearTimeout)
    maybeLastTimeoutHandle = js.defined(
      js.timers.setTimeout(delayFromLastEventMillis) {
        new Transaction(fire(nextValue, _))
      }
    )
  }

}
