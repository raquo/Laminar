package com.raquo.laminar.experimental.airstream

class CombineSignal2[A, B](
  parent1: Signal[A],
  parent2: Signal[B],
  override val context: Context
) extends CombineSignal[(A, B)] {

  override private[airstream] val parents: Seq[Signal[_]] = List(parent1, parent2)

  parent1.addChild(this)
  parent2.addChild(this)

  override protected[this] def calc(): (A, B) = {
    (parent1.now(), parent2.now())
  }

  def map2[C](project: (A, B) => C)(implicit context: Context): MapSignal[(A, B), C] = {
    new MapSignal(parent = this, combinedValue => project(combinedValue._1, combinedValue._2), context)
  }
}
