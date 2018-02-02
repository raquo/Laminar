package com.raquo.laminar.experimental.airstream.state

import com.raquo.laminar.experimental.airstream.features.CombineMemoryObservable2
import com.raquo.laminar.experimental.airstream.ownership.Owner

class CombineState2[A, B, O](
  override protected[this] val parent1: State[A],
  override protected[this] val parent2: State[B],
  override protected[this] val combinator: (A, B) => O,
  override protected[state] val owner: Owner
) extends State[O] with CombineMemoryObservable2[A, B, O] {

  override protected[airstream] val topoRank: Int = (parent1.topoRank max parent2.topoRank) + 1

  onStart()

  override protected[this] def initialValue(): O = combinator(parent1.now(), parent2.now())

  override protected[this] def registerWithOwner(): Unit = {
    owner.own(this)
  }
}
