package com.raquo.laminar.experimental.airstream.state

import com.raquo.laminar.experimental.airstream.features.CombineMemoryObservable2
import com.raquo.laminar.experimental.airstream.ownership.Owner

class CombineState2[A, B](
  override protected[this] val parent1: State[A],
  override protected[this] val parent2: State[B],
  override protected val owner: Owner
) extends State[(A, B)] with CombineMemoryObservable2[A, B] {

  // @TODO[Convenience] Add map2 method

}
