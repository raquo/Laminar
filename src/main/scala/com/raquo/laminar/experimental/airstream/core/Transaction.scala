package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.util.{GlobalCounter, JsPriorityQueue}
import org.scalajs.dom

import scala.scalajs.js

class Transaction(code: Transaction => Any) {

  // @TODO this is not used except for debug logging. Remove eventually
  val id: Int = Transaction.nextId()

  /** "Priority queue" of pending observables: sorted by their topoRank */
  private[airstream] val pendingObservables: JsPriorityQueue[SyncObservable[_]] = new JsPriorityQueue(_.topoRank)

  Transaction.add(this)

  // @TODO rename
  private[airstream] def run(): Unit = {
    dom.console.log(s"TRX($id).run")
    Transaction.isSafeToRemoveObserver = false
    code(this) // this evaluates a pass-by-name param @TODO[Integrity] make sure this is not DCE-d in fullOptJS
    dom.console.log(s"TRX($id).pendingObservables.length = ${pendingObservables.size}")
    resolvePendingObservables()
    Transaction.isSafeToRemoveObserver = true
    Transaction.done(this)
  }

  @inline private def resolvePendingObservables(): Unit = {
    while (pendingObservables.nonEmpty) {
      //      dom.console.log("RANKS: ", pendingObservables.map(_.topoRank))
      pendingObservables.dequeue().syncFire(this) // Fire the first pending observable and remove it from the list
    }
  }
}

object Transaction extends GlobalCounter {

  private var isSafeToRemoveObserver: Boolean = true

  private[this] val pendingTransactions: js.Array[Transaction] = js.Array()

  private[this] val pendingObserverRemovals: js.Array[() => Unit] = js.Array()

  protected[airstream] def removeExternalObserver[A](observable: Observable[A], observer: Observer[A]): Unit = {
    if (isSafeToRemoveObserver) {
      // remove right now – useful for efficient recursive removals
      observable.removeExternalObserverNow(observer)
    } else {
      // schedule removal to happen at the end of the transaction
      // (don't want to interfere with iteration over the list of observers)
      pendingObserverRemovals.push(() => observable.removeExternalObserverNow(observer))
    }
  }

  // @TODO Maybe make more public for more extensibility
  protected[airstream] def removeInternalObserver[A](observable: Observable[A], observer: InternalObserver[A]): Unit = {
    if (isSafeToRemoveObserver) {
      // remove right now – useful for efficient recursive removals
      observable.removeInternalObserverNow(observer)
    } else {
      // schedule removal to happen in a new transaction
      // (don't want to interfere with iteration over observables' lists of observers)
      pendingObserverRemovals.push(() => observable.removeInternalObserverNow(observer))
    }
  }

  private def resolvePendingObserverRemovals(): Unit = {
    if (!isSafeToRemoveObserver) {
      throw new Exception("It's not safe to remove observers right now!")
    }
    pendingObserverRemovals.foreach(remove => remove())
    pendingObserverRemovals.clear()
  }

  private def add(transaction: Transaction): Unit = {
    // If a transaction is currently running, the new transaction will be triggered
    // from the .done() call after the current transaction finishes.
    // Otherwise, if there are no pending transactions other than this new transaction,
    // we need to run this transaction right now because no one will do it for us.
    val hasPendingTransactions = pendingTransactions.length > 0
    pendingTransactions.push(transaction)
    if (!hasPendingTransactions) {
      transaction.run()
    }
  }

  private def done(transaction: Transaction): Unit = {
    if (pendingTransactions(0) != transaction) {
      // @TODO[Integrity] Should we really throw here?
      throw new Exception("Transaction mismatch: done transaction is not first in list")
    }
    pendingTransactions.shift()

    resolvePendingObserverRemovals()

    if (pendingTransactions.length > 0) {
      pendingTransactions(0).run()
    }
  }

}
