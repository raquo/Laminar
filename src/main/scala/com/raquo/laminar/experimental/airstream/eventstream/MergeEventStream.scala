package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.{InternalObserver, InternalParentObserver, Observable, Observer, Transaction}

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

  // @TODO EVENTBUS REALLY SHOULD EXTEND THIS, OR A COMMON ANCESTOR

  // @TODO We need a better definition for how we want to handle cycles
  // @TODO vvvv How does this play with cycles? Shouldn't all streams behave like that?
  /** Only one event can propagate in a transaction at the same time.
    * If this stream has already fired in a given transaction, the next firing will be
    * delayed to a new transaction.
    */
//  val parentObservers: js.Array[InternalParentObserver[A]] = js.Array()

  // @TODO[Elegance] is there a less lame way to efficiently create a js.Array out of an Iterable?
//  parents.foreach(addParent)

  private[this] var lastFiredInTransactionId: js.UndefOr[Int] = js.undefined
//  private[this] var lastFiredParentId: js.UndefOr[Int] = js.undefined

//  protected def addParent(parent: Observable[A]): Unit = {
//    parentObservers.push(
//      InternalParentObserver(parent, onNext = (nextValue, transaction) => {
//        // - Only one parent is allowed to emit within a transaction
//        //   to prevent multiplication of simultaneous events.
//        // - More than one event per parent is allowed to accommodate cycles within a transaction
//        // @TODO ^^^ Does that second condition make sense?
//        if (js.defined(transaction.id) == lastFiredInTransactionId) {
//          new Transaction(newTransaction => {
//            lastFiredInTransactionId = newTransaction.id
//            lastFiredParentId = parent.id
//            fire(nextValue, newTransaction)
//          })
//        } else {
//          lastFiredInTransactionId = transaction.id
//          lastFiredParentId = parent.id
//          fire(nextValue, transaction)
//        }
//      })
//    )
//  }

  // protected def removeParent

  override protected[airstream] def onNext(nextValue: A, transaction: Transaction): Unit = {
    if (js.defined(transaction.id) == lastFiredInTransactionId) {
      new Transaction(fire(nextValue, _))
    } else {
      fire(nextValue, transaction)
    }
  }

  override protected[this] def fire(nextValue: A, transaction: Transaction): Unit = {
    lastFiredInTransactionId = transaction.id
    super.fire(nextValue, transaction)
  }

  override protected[airstream] def syncDependsOn(
    otherObservable: Observable[_],
    seenObservables: js.Array[Observable[_]]
  ): Boolean = {
    // @TODO seenObservables should be done better here
    parents.exists { parent =>
      seenObservables.push(parent)
      if (parent == otherObservable) {
        true
      } else {
        parent.syncDependsOn(otherObservable, seenObservables)
      }
    }
  }

  override protected[this] def onStart(): Unit = {
//    parentObservers.foreach(_.addToParent())
    parents.foreach(_.addInternalObserver(this))
  }

  override protected[this] def onStop(): Unit = {
//    parentObservers.foreach(_.removeFromParent())
    parents.foreach(_.removeInternalObserver(this))
  }
}
