package com.raquo.laminar.experimental.airstream.features

import com.raquo.laminar.experimental.airstream.core.{MemoryObservable, Observer}

trait CombineMemoryObservable2[A, B] extends MemoryObservable[(A, B)] with CombineObservable2[A, B] {

  override protected[this] val parent1: MemoryObservable[A]
  override protected[this] val parent2: MemoryObservable[B]

  override protected[this] var currentValue: (A, B) = (parent1.now(), parent2.now())

  override protected[this] val parent1Observer: Observer[A] = Observer(nextParent1Value => {
    fire((nextParent1Value, parent2.now()))
  })

  override protected[this] val parent2Observer: Observer[B] = Observer(nextParent2Value => {
    fire((parent1.now(), nextParent2Value))
  })
}
