package com.raquo.laminar.experimental.airstream

import org.scalajs.dom

import scala.scalajs.js

// @TODO[API] Add private[airstream] to the whole object? Maybe classes as well? See how that works, exactly
object Airstream {

  private[this] val pendingSignals: js.Array[CombineSignal[_]] = js.Array()

  private[this] val pendingObservations: js.Array[Observation[_]] = js.Array()

  private[this] def isSignalPending(child: ComputedSignal[_]): Boolean = {
    pendingSignals.indexOf(child) != -1
  }

  private[airstream] def addPendingSignal(signal: CombineSignal[_]): Unit = {
    dom.console.log("> addPendingSignal", signal.toString)
    val alreadyPending = isSignalPending(signal)
    if (!alreadyPending) {
      pendingSignals.push(signal)
    }
  }

  private[airstream] def addPendingObservation[A](value: A, observer: Observer[A]): Unit = {
    dom.console.log(s"> addPendingObservation($value, $observer)")
    pendingObservations.push(new Observation(value, observer))
  }

  private[airstream] def finalizePropagation(): Unit = {
    resolvePendingSignals()
    runObservations()
  }

  /** Pending signals are CombineSignal-s (signals that have multiple parents)
    * that the propagation encountered on its way and was not able to go through.
    *
    * Propagation halts on combine signals the first time it sees this particular signal.
    * This is when the combine signal is added to the pending signals list.
    *
    * Subsequently, this `resolvePendingSignals` method runs until it all pending signals
    * are resolved. This works the following way: if a given pending signal depends
    * (directly or indirectly) on any other pending signal, we skip processing it for now.
    *
    * If a pending signal does not depend on any other pending signals, this means that
    * we can proceed in recalculating it and propagating its value to its children, which
    * in turn could add more pending signals if it encounters previously unseen combine
    * signals.
    *
    * Important: This algorithm is only guaranteed to terminate for acyclical dataflow graphs.
    * That is, signals must not indirectly depend on themselves by means of signal propagation.
    * It is technically ok for a signal to indirectly depend on itself if the signal triggers
    * the update of its dependency AFTER all pending signals have been resolved (e.g. in an
    * observer). However, in that case this will be considered a completely separate propagation,
    * meaning that all necessary signals will be recalculated again, and relevant observers
    * would fire again.
    *
    * Note: we use recursive function here to make sure that the stack blows up if the graph
    * has unresolvable cycles. // @TODO[Integrity] Could we have proper loop detection?
    */
  private[airstream] def resolvePendingSignals(): Unit = {
    dom.console.log(">>>>>>>")
    dom.console.log(pendingSignals)
    def loop(): Unit = {
      if (pendingSignals.length > 0) {
        var index = 0
        while (pendingSignals.length > index) {
          val signal = pendingSignals(index)
          if (!dependsOnAnyOtherPendingSignal(signal)) {
            pendingSignals.splice(index, deleteCount = 1)
            signal.propagate(haltOnNextCombine = false)
          } else {
            index += 1
          }
        }
        loop()
      }
    }
    loop()
  }

  /** Running an observation could trigger another propagation which could add more observations.
    *
    * If an observation does trigger a propagation, that propagation will call this `runObservations`
    * method itself, and that method will have its own while loop, which will start running
    * observations on its own while the original loop waits.
    *
    * This is perhaps unintuitive, but it does work as expected with arbitrary amount of nesting
    * because observations are always **appended** to `pendingObservations`, so every loop that
    * will work on this list will process it in the same, correct order.
    */
  private[airstream] def runObservations(): Unit = {
    // Note: observation itself might trigger more
    while (pendingObservations.length > 0) {
      pendingObservations.shift().observe()
    }
  }

  private[airstream] def dependsOnAnyOtherPendingSignal(signal: ComputedSignal[_]): Boolean = {
    // @TODO[Performance] can be improved. Good enough for PoC
    var index = 0
    var depends = false
    while (!depends && index < pendingSignals.length) {
      val otherSignal = pendingSignals(index)
      if (signal != otherSignal) {
        depends = dependsOnOtherSignal(signal = signal, otherSignal)
      }
      index += 1
    }
    depends
  }

  private[airstream] def dependsOnOtherSignal(signal: ComputedSignal[_], otherSignal: ComputedSignal[_]): Boolean = {
    // @TODO[Performance] can be improved. Good enough for PoC
    signal.parents.exists { parent =>
      parent match {
        case `otherSignal` => true
        case _: Var[_] => false
        case computed: ComputedSignal[_] => dependsOnOtherSignal(signal = computed, otherSignal)
      }
    }
  }

}
