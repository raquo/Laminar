package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.ownership.Owner

class CombineSignal2[A, B](
  parent1: Signal[A],
  parent2: Signal[B],
  override protected val owner: Owner
) extends CombineSignal[(A, B)] {

  override private[airstream] val parents: Seq[Signal[_]] = List(parent1, parent2)

  parent1.linkChild(this)
  parent2.linkChild(this)

  override protected[this] def calc(): (A, B) = {
    (parent1.now(), parent2.now())
  }

  def map2[C](project: (A, B) => C)(implicit mapSignalOwner: Owner): MapSignal[(A, B), C] = {
    new MapSignal(parent = this, combinedValue => project(combinedValue._1, combinedValue._2), mapSignalOwner)
  }
}
