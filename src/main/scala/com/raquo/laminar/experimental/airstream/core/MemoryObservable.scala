package com.raquo.laminar.experimental.airstream.core

/** An observable that remembers its current value */
trait MemoryObservable[+A] extends Observable[A] {

  // @TODO I guess we need sample/sampledBy methods after all
  /** We don't want public access to this for lazy MemoryObservables (Signals) because if you called .now() on a Signal,
    * you wouldn't know if you were getting the updated value or not, because that would depend on whether said Signal
    * has observers.*/
  protected[airstream] def now(): A

  protected[this] def setCurrentValue(newValue: A): Unit

  // @TODO implement this
//  lazy val changes: Stream[A] = ???


  override protected[this] def fire(nextValue: A, transaction: Transaction): Unit = {
    // @TOTO[API] The reason we might not want this for Signal is because Signal's now() is not guaranteed to be fresh. Not sure if we care.
//    if (nextValue != currentValue) {  // @TODO we want this check here, right? There's another check like this in State
    setCurrentValue(nextValue)
    super.fire(nextValue, transaction)
//    }
  }
}
