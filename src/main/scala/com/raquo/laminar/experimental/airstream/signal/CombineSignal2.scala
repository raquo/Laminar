package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.features.CombineMemoryObservable2

class CombineSignal2[A, B](
  override protected[this] val parent1: Signal[A],
  override protected[this] val parent2: Signal[B]
) extends Signal[(A, B)] with CombineMemoryObservable2[A, B]

// @TODO add map2 method
