package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.ownership.Owner

class MapSignal[I, O](
  parent: Signal[I],
  project: I => O,
  override val owner: Owner
) extends ComputedSignal[O] {

  override val parents: Seq[Signal[I]] = List(parent)

  parent.linkChild(this)

  override protected[this] def calc(): O = {
    project(parent.now())
  }

  override def toString: String = s"MapSignal@${hashCode()}(value=$currentValue)"
}
