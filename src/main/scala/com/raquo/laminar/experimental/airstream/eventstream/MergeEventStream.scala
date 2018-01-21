package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.{InternalObserver, Observable, Transaction}

import scala.scalajs.js

// @TODO[API] Provide EventStream.toSignal(initialValue), Signal.changes and other integration methods
// @TODO[API] to make this API indirectly applicable to Signals and State

/** Stream that emit events from all of its parents.
  *
  * This feature exists only for EventStream-s because merging MemoryObservable-s
  * (Signal and State) does not make sense, conceptually.
  */
class MergeEventStream[A](
  parents: Iterable[Observable[A]]
) extends EventStream[A] with InternalObserver[A] {

  override protected[airstream] val topoRank: Int = {
    var maxParentRank = 0
    parents.foreach { parent =>
      maxParentRank = maxParentRank max parent.topoRank
    }
    maxParentRank + 1
  }

  private[this] var lastFiredInTransactionId: js.UndefOr[Int] = js.undefined

  /** If this stream has already fired in a given transaction, the next firing will happen in a new transaction.
    *
    * This is needed for a combination of two reasons:
    * 1) only one event can propagate in a transaction at the same time
    * 2) We do not want the merged stream to "swallow" events
    */
  override protected[airstream] def onNext(nextValue: A, transaction: Transaction): Unit = {
    if (js.defined(transaction.id) == lastFiredInTransactionId) {
      println("NEW TRX from MergeEventStream")
      new Transaction(fire(nextValue, _))
    } else {
      fire(nextValue, transaction)
    }
  }

  override protected[this] def fire(nextValue: A, transaction: Transaction): Unit = {
    lastFiredInTransactionId = transaction.id
    super.fire(nextValue, transaction)
  }

  override protected[this] def onStart(): Unit = {
    parents.foreach(_.addInternalObserver(this))
  }

  override protected[this] def onStop(): Unit = {
    parents.foreach(_.removeInternalObserver(this))
  }
}
