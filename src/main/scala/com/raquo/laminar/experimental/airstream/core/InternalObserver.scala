package com.raquo.laminar.experimental.airstream.core

trait InternalObserver[-A] {

  protected[airstream] def onNext(nextValue: A, transaction: Transaction): Unit
}

object InternalObserver {
  protected[airstream] def apply[A](onNext: (A, Transaction) => Unit): InternalObserver[A] = {
    val onNextParam = onNext // It's beautiful on the outside
    new InternalObserver[A] {
      override protected[airstream] def onNext(nextValue: A, transaction: Transaction): Unit = {
        onNextParam(nextValue, transaction)
      }
    }
  }
}
