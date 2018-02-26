package com.raquo.laminar.experimental.airstream.features

import com.raquo.laminar.experimental.airstream.core.MemoryObservable

trait CombineMemoryObservable2[A, B, O] extends MemoryObservable[O] with CombineObservable[O] {

  protected[this] val combinator: (A, B) => O

  protected[this] val parent1: MemoryObservable[A]
  protected[this] val parent2: MemoryObservable[B]

  parentObservers.push(
    InternalParentObserver[A](parent1, (nextParent1Value, transaction) => {
      internalObserver.onNext(combinator(nextParent1Value, parent2.now()), transaction)
    }),
    InternalParentObserver[B](parent2, (nextParent2Value, transaction) => {
      internalObserver.onNext(combinator(parent1.now(), nextParent2Value), transaction)
    })
  )
}
