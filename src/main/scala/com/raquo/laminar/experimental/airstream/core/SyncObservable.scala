package com.raquo.laminar.experimental.airstream.core

import com.raquo.laminar.experimental.airstream.features.SingleParentSyncObservable
import org.scalajs.dom

import scala.scalajs.js

// @TODO Less rambling, more docs
/** SyncObservable is an observable that waits for its sync dependencies to resolve before it can be resolved itself.
  *
  * What's a sync dependency? Every Observable determines that by itself in its syncDependsOn(otherObservable) method.
  * Typically, if firing an event to observable A would propagate to observable B via the observables chain, B is said
  * to be sync dependent on A. So e.g. if B = A.map(toB).filter(blah), or if B = C.combineWith(A), B would depend on A.
  *
  * Note that "addObserver" is not part of the observable propagation. A.addObserver(B) will NOT cause B or any of its
  * downstream observables to be sync dependent on A. I am not yet sure if it's a feature of a bug.
  *
  * When the propagation reaches a SyncObservable, such an observable is added to the list of pending observables, and
  * the propagation proceeds as if SyncObservable did not emit any events. After all other propagation paths have
  * finished executing, we try to resolve pending observables. We try to find the first observable in the list that
  * does not depend on any other pending observables. If we find such an observable, we remove it from the list of
  * pending observables, and resume the propagation from that observable. Then rinse and repeat.
  *
  * It is possible that pending observables resolution results in an infinite loop in case of cyclical dependencies,
  * i.e. a deadlock, when all pending observables depend on each other. We currently don't handle this case, but we
  * will, and we will provide a way to resolve it without an error. @TODO
  */
trait SyncObservable[A] extends SingleParentSyncObservable[A, A] {

  private[SyncObservable] var maybeLastValue: Option[A] = None

  @inline private[this] def isPending = maybeLastValue.isDefined

  override protected[this] val inputObserver: Observer[A] = Observer { newParentValue =>
    if (!isPending) {
      SyncObservable.pendingObservables.push(this)
    }
    maybeLastValue = Some(newParentValue)
  }

  private[airstream] def syncFire(): Unit = {
    // @TODO[Integrity] maybeLastValue MUST be defined at this point, if it isn't, something is wrong with Airstream logic
    maybeLastValue.foreach { syncedValue =>
      maybeLastValue = None
      super.fire(syncedValue)
    }
  }
}

object SyncObservable {

  private val pendingObservables: js.Array[SyncObservable[_]] = js.Array()

  // @TODO Add more comments / description

  private[airstream] def resolvePendingSyncObservables(): Unit = {
    dom.console.log(">>>>>> resolvePendingSyncObservables")
    dom.console.log(pendingObservables)
    // @TODO[Integrity,Performance] recursion here is simply to blow up the stack in case of a deadlock. We should
    // @TODO We should handle deadlocks properly
    // @TODO We should have weakSync() or something that would allow the observable to be processed in case of deadlock
    def loop(): Unit = {
      if (pendingObservables.length > 0) {
        var index = 0
        while (pendingObservables.length > index) {
          val observable = pendingObservables(index)
          if (!syncDependsOnAnyOtherPendingObservable(observable)) {
            pendingObservables.splice(index, deleteCount = 1)
            observable.syncFire()
          } else {
            index += 1
          }
        }
        loop()
      }
    }
    loop()
  }

  private[airstream] def syncDependsOnAnyOtherPendingObservable(observable: SyncObservable[_]): Boolean = {
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
