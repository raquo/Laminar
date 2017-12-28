package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.ownership.Owner

class MapSignal[A, B](
  parent: Signal[A],
  project: A => B,
  override val owner: Owner
) extends ComputedSignal[B] {

  override val parents: Seq[Signal[A]] = List(parent)

  parent.addChild(this)

  override protected[this] def calc(): B = {
    project(parent.now())
  }

  override def toString: String = s"MapSignal@${hashCode()}(value=$currentValue)"
}
