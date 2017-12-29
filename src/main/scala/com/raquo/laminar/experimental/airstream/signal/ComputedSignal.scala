package com.raquo.laminar.experimental.airstream.signal

import org.scalajs.dom

trait ComputedSignal[A] extends Signal[A] {

  override protected var currentValue: A = calc()

  dom.console.log(s"> Create $this")

  private[airstream] val parents: Seq[Signal[_]] // @TODO[API] this could be a set, probably

  protected[this] def calc(): A

  /** @return whether the value of this computed signal has changed */
  private[airstream] def recalculate(): Boolean = {
    val prevValue = currentValue
    currentValue = calc()
    currentValue != prevValue
  }

  override private[airstream] def propagate(haltOnNextCombine: Boolean): Unit = {
    dom.console.log(s"> ${this}.ComputedSignal.propagate", haltOnNextCombine)
    if (recalculate()) {
      super.propagate(haltOnNextCombine = true)
    }
  }

  /** When killing a computed signal, we additionally want to make sure that its parents don't hold a reference to it.
    * This ensures that a circular reference between parents and children will not prevent GC of killed children
    * (assuming the killed children are otherwise unreachable).
    */
  override private[airstream] def kill(): Unit = {
    parents.foreach(_.unlinkChild(this))
    super.kill()
  }

  override def toString: String = s"ComputedSignal@${hashCode()}(value=$currentValue)"
}
