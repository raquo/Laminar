package com.raquo.laminar.experimental.airstream.features

import com.raquo.laminar.experimental.airstream.core.{InternalObserver, InternalParentObserver, SyncObservable, Transaction}

import scala.scalajs.js

trait CombineObservable[A] extends SyncObservable[A] {

  // @TODO[API] This is a legacy field from SyncObservable. It allows for bypassing deadlock. Not sure what to do with it yet.
  private[airstream] val isSoft: Boolean = true

  protected[this] var maybeCombinedValue: Option[A] = None

  protected[this] val parentObservers: js.Array[InternalParentObserver[_]] = js.Array()

  // Implementations should call internalObserver.onNext() instead of .fire()
  // Transaction will call .fireSync() when it's time, and that will in turn call .fire()
  protected[this] val internalObserver: InternalObserver[A] = InternalObserver(
    (nextValue, transaction) => {
      if (!transaction.pendingObservables.contains(this)) {
        println(s"Marking CombineObs($id) as pending in TRX(${transaction.id})")
        transaction.pendingObservables.enqueue(this)
      }
      maybeCombinedValue = Some(nextValue)
    }
  )

  override protected[this] def fire(nextValue: A, transaction: Transaction): Unit = {
    println(s"!!! Firing CombineObs($id) in TRX(${transaction.id}) (isPending=${transaction.pendingObservables.contains(this)})")
    super.fire(nextValue, transaction)
  }

  /** This method is called after this pending observable has been resolved */
  override private[airstream] def syncFire(transaction: Transaction): Unit = {
    maybeCombinedValue.foreach { combinedValue =>
      maybeCombinedValue = None
      fire(combinedValue, transaction)
    }
  }

  override protected[this] def onStart(): Unit = {
    parentObservers.foreach(_.addToParent())
  }

  override protected[this] def onStop(): Unit = {
    parentObservers.foreach(_.removeFromParent())
  }

}
