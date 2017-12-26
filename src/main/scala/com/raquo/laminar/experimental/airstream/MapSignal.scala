package com.raquo.laminar.experimental.airstream

class MapSignal[A, B](
  parent: Signal[A],
  project: A => B,
  override val context: Context
) extends ComputedSignal[B] {

  override val parents: Seq[Signal[A]] = List(parent)

  parent.addChild(this)

  override protected[this] def calc(): B = {
    project(parent.now())
  }

  override def toString: String = s"MapSignal@${hashCode()}(value=$currentValue)"
}
