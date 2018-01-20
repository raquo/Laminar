package com.raquo.laminar.experimental.airstream.core

trait InternalParentObserver[A] extends InternalObserver[A] {

  val parent: Observable[A]

  def addToParent(): Unit = {
    parent.addInternalObserver(this)
  }

  def removeFromParent(): Unit = {
    parent.removeInternalObserver(this)
  }
}

object InternalParentObserver {

  protected[airstream] def apply[A](
    parent: Observable[A],
    onNext: (A, Transaction) => Unit
  ): InternalParentObserver[A] = {
    val parentParam = parent
    val onNextParam = onNext
    new InternalParentObserver[A] {

      override val parent: Observable[A] = parentParam

      override protected[airstream] def onNext(nextValue: A, transaction: Transaction): Unit = {
        onNextParam(nextValue, transaction)
      }
    }
  }
}
