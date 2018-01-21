package com.raquo.laminar.experimental.airstream.core

/** Observable that can become pending for the purpose of synchronization */
trait SyncObservable[+A] extends Observable[A] {

  /** This method is called after this pending observable has been resolved */
  private[airstream] def syncFire(transaction: Transaction): Unit
}
