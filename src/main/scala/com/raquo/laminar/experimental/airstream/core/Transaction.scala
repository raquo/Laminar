package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.features.CombineObservable
import com.raquo.laminar.experimental.airstream.util.GlobalCounter
import org.scalajs.dom

import scala.scalajs.js

class Transaction(code: Transaction => Any) {

  val id: Int = Transaction.nextId()

  private[this] val pendingObservables: js.Array[CombineObservable[_]] = js.Array()

  Transaction.add(this)

  private[airstream] def addPending(observable: CombineObservable[_]): Unit = {
    pendingObservables.push(observable)
  }

  private[airstream] def isPending(observable: CombineObservable[_]): Boolean = {
    pendingObservables.indexOf(observable) != -1
  }

  // @TODO rename
  private[airstream] def run(): Unit = {
    code(this) // this evaluates a pass-by-name param @TODO[Integrity] make sure this is not DCE-d in fullOptJS
    resolvePendingSyncObservables()
    Transaction.done(this)
  }

  private[airstream] def resolvePendingSyncObservables(): Unit = {
    dom.console.log(s">>>>>> resolvePendingSyncObservables (num=${pendingObservables.length})")
    dom.console.log(pendingObservables)
    // @TODO[Performance] recursion was here simply to blow up the stack in case of a deadlock, but we don't need that anymore
    def loop(shouldResolveDeadlock: Boolean = false): Unit = {
      if (pendingObservables.length > 0) {
        var isDeadlock = true
        var index = 0
        while (index < pendingObservables.length) {
          val observable = pendingObservables(index)
          val shouldFire = if (shouldResolveDeadlock) {
            // We try to resolve deadlock by getting through a soft synced observable.
            // Note: deadlock condition means that all pending observables
            //       depend on each other, so there is no point in checking
            //       syncDependsOnAnyOtherPendingObservable in this branch
            //       (we clear deadlock status down below as soon as we find
            //       a soft sync observable to fire).
            observable.isSoft
          } else {
            // Normally, we fire the first pending observable that does not depend on
            // any other pending observables
            !syncDependsOnAnyOtherPendingObservable(observable)
          }
          if (shouldFire) {
            isDeadlock = false
            pendingObservables.splice(index, deleteCount = 1)
            observable.syncFire(this)
            if (shouldResolveDeadlock) {
              // We've fired a soft-synced observable to resolve the deadlock.
              // At this point, the deadlock *might* be resolved.
              // We should start a new loop to try from the beginning.
              index = pendingObservables.length
            }
          } else {
            index += 1
          }
        }
        if (isDeadlock) {
          throw new Exception("Airstream: Deadlock detected")
          // @TODO[API] Exception should be its own class
          // @TODO[Integrity] What should we do here? Should we clear pending observables?
          // @TODO We should probably send an error to the first observable and check for deadlock again (once we have error handling)
        }
        // @TODO >>> throw a deadlock exception here?
        loop(shouldResolveDeadlock = isDeadlock)
      }
    }
    loop()
  }

  private[airstream] def syncDependsOnAnyOtherPendingObservable(observable: CombineObservable[_]): Boolean = {
    // @TODO[Performance] see if this can be improved
    var index = 0
    var depends = false
    while (!depends && index < pendingObservables.length) {
      val otherObservable = pendingObservables(index)
      if (observable != otherObservable) {
        depends = observable.syncDependsOn(otherObservable, seenObservables = js.Array())
      }
      index += 1
    }
    depends
  }
}

object Transaction extends GlobalCounter {

  val pendingTransactions: js.Array[Transaction] = js.Array()

  private[Transaction] def add(transaction: Transaction): Unit = {
    // @TODO [Integrity] I think
    // @TODO [Integrity]
    pendingTransactions.push(transaction)
    // If a transaction is currently running, the new transaction will be triggered
    // from the .done() call after the current transaction finishes.
    if (pendingTransactions.length == 1) {
      // Otherwise, if there are no pending transactions other than this new transaction,
      // we need to run this transaction right now because no one will do it for us.
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
