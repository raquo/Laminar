package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.util.{GlobalCounter, JsPriorityQueue}
import org.scalajs.dom

import scala.scalajs.js

class Transaction(code: Transaction => Any) {

  val id: Int = Transaction.nextId()

  /** "Priority queue" of pending observables: sorted by their topoRank */
  private[airstream] val pendingObservables: JsPriorityQueue[SyncObservable[_]] = new JsPriorityQueue(_.topoRank)

  Transaction.add(this)

  // @TODO rename
  private[airstream] def run(): Unit = {
    dom.console.log(s"TRX($id).run")
    code(this) // this evaluates a pass-by-name param @TODO[Integrity] make sure this is not DCE-d in fullOptJS
    dom.console.log(s"TRX($id).pendingObservables.length = ${pendingObservables.size}")
    while (pendingObservables.nonEmpty) {
//      dom.console.log("RANKS: ", pendingObservables.map(_.topoRank))
      pendingObservables.dequeue().syncFire(this) // Fire the first pending observable and remove it from the list
    }
    Transaction.done(this)
  }
}

object Transaction extends GlobalCounter {

  val pendingTransactions: js.Array[Transaction] = js.Array()

  private[Transaction] def add(transaction: Transaction): Unit = {
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

  private[Transaction] def done(transaction: Transaction): Unit = {
    if (pendingTransactions(0) != transaction) {
      // @TODO[Integrity] Should we really throw here?
      throw new Exception("Transaction mismatch: done transaction is not first in list")
    }
    pendingTransactions.shift()
    if (pendingTransactions.length > 0) {
      pendingTransactions(0).run()
    }
  }

}
