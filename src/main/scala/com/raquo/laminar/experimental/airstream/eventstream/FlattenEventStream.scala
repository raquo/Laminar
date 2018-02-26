package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.{InternalObserver, MemoryObservable, Observable, Transaction}
import com.raquo.laminar.experimental.airstream.features.SingleParentObservable

import scala.scalajs.js

/** This stream emits the events from the last (stream of events) emitted by `parent`.
  *
  * Events are emitted at the same time as the currently tracked stream emits them.
  *
  * When `parent` emits a new value, this stream switches to emitting events from that new value (which is a stream).
  *
  * Warning: Similar to [[com.raquo.laminar.experimental.airstream.eventbus.EventBus]], this stream emits events in
  * a new transaction because its proper topoRank would need to be dynamic, which we don't support.
  *
  * Note: this stream loses its memory if stopped.
  */
class FlattenEventStream[A](
  override protected[this] val parent: Observable[EventStream[A]]
) extends EventStream[A] with SingleParentObservable[EventStream[A], A] {

  override protected[airstream] val topoRank: Int = 1

  private[this] var maybeCurrentEventStream: js.UndefOr[EventStream[A]] = parent match {
    case mo: MemoryObservable[EventStream[A]] => mo.now()
    case _ => js.undefined
  }

  private[this] val internalEventObserver: InternalObserver[A] = InternalObserver {
    (event, _) => new Transaction(fire(event, _))
  }

  override protected[airstream] def onNext(nextStream: EventStream[A], transaction: Transaction): Unit = {
    removeInternalObserverFromCurrentEventStream()
    maybeCurrentEventStream = nextStream
    // If we're receiving events, this stream is started, so no need to check for that
    nextStream.addInternalObserver(internalEventObserver)
  }

  override protected[this] def onStart(): Unit = {
    maybeCurrentEventStream.foreach(_.addInternalObserver(internalEventObserver))
    super.onStart()
  }

  override protected[this] def onStop(): Unit = {
    removeInternalObserverFromCurrentEventStream()
    maybeCurrentEventStream = js.undefined
    super.onStop()
  }

  @inline private def removeInternalObserverFromCurrentEventStream(): Unit = {
    maybeCurrentEventStream.foreach { currentStream =>
      Transaction.removeInternalObserver(currentStream, internalEventObserver)
    }
  }

}
