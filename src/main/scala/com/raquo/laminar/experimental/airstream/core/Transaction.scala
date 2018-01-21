package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.features.CombineObservable
import com.raquo.laminar.experimental.airstream.util.GlobalCounter
import org.scalajs.dom

import scala.scalajs.js

class Transaction(code: Transaction => Any) {

  val id: Int = Transaction.nextId()

  /** "Priority queue" of pending observables: sorted by their topoRank */
  private[this] val pendingObservables: js.Array[CombineObservable[_]] = js.Array()

  Transaction.add(this)

  /** Insert pending observable into priority queue at the correct index */
  private[airstream] def addPendingObservable(observable: CombineObservable[_]): Unit = {
    var insertAtIndex = 0
    var foundHigherRank = false
    while (
      insertAtIndex < pendingObservables.length &&
      !foundHigherRank
    ) {
      if (pendingObservables(insertAtIndex).topoRank <= observable.topoRank) {
        foundHigherRank = true
      }
      insertAtIndex += 1
    }
    pendingObservables.splice(index = insertAtIndex, deleteCount = 0, observable) // insert at index
  }

  private[airstream] def hasPendingObservable(observable: CombineObservable[_]): Boolean = {
    pendingObservables.indexOf(observable) != -1
  }

  // @TODO rename
  private[airstream] def run(): Unit = {
    dom.console.log(s"TRX($id).run")
    code(this) // this evaluates a pass-by-name param @TODO[Integrity] make sure this is not DCE-d in fullOptJS
    dom.console.log(s"TRX($id).pendingObservables.length = ${pendingObservables.length}")
    while (pendingObservables.length > 0) {
      dom.console.log("RANKS: ", pendingObservables.map(_.topoRank))
      pendingObservables.shift().syncFire(this) // Fire the first pending observable and remove it from the list
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
