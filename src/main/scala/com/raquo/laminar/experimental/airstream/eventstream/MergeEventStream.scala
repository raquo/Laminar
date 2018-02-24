package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.{Observable, Observation, SyncObservable, Transaction}
import com.raquo.laminar.experimental.airstream.features.InternalParentObserver
import com.raquo.laminar.experimental.airstream.util.JsPriorityQueue

import scala.scalajs.js

/** Stream that emit events from all of its parents.
  *
  * This feature exists only for EventStream-s because merging MemoryObservable-s
  * (Signal and State) does not make sense, conceptually.
  */
class MergeEventStream[A](
  parents: Iterable[Observable[A]]
) extends EventStream[A] with SyncObservable[A] {

  override protected[airstream] val topoRank: Int = {
    var maxParentRank = 0
    parents.foreach { parent =>
      maxParentRank = maxParentRank max parent.topoRank
    }
    maxParentRank + 1
  }

  private[this] val pendingParentValues: JsPriorityQueue[Observation[A]] = new JsPriorityQueue(_.observable.topoRank)

  private[this] val parentObservers: js.Array[InternalParentObserver[A]] = js.Array()

  parents.foreach(parent => parentObservers.push(makeInternalObserver(parent)))

  // @TODO document this, and document the topo parent order
  /** If this stream has already fired in a given transaction, the next firing will happen in a new transaction.
    *
    * This is needed for a combination of two reasons:
    * 1) only one event can propagate in a transaction at the same time
    * 2) We do not want the merged stream to "swallow" events
    *
    * We made it this way because the user probably expects this behavior.
    */
  override private[airstream] def syncFire(transaction: Transaction): Unit = {
    // At least one value is guaranteed to exist if this observable is pending
    fire(pendingParentValues.dequeue().value, transaction)

    while (pendingParentValues.nonEmpty) {
      println("NEW TRX from MergeEventStream")
      val nextValue = pendingParentValues.dequeue().value
      new Transaction(fire(nextValue, _))
    }
  }

  override protected[this] def onStart(): Unit = {
    parentObservers.foreach(_.addToParent())
  }

  override protected[this] def onStop(): Unit = {
    parentObservers.foreach(_.removeFromParent())
  }

  private def makeInternalObserver(parent: Observable[A]): InternalParentObserver[A] = {
    InternalParentObserver(parent, onNext = (nextValue, transaction) => {
      pendingParentValues.enqueue(new Observation(parent, nextValue))
      if (!transaction.pendingObservables.contains(this)) {
        transaction.pendingObservables.enqueue(this)
      }
    })
  }
}
