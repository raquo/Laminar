package com.raquo.laminar.experimental.airstream

import org.scalajs.dom

trait ComputedSignal[A] extends Signal[A] {

  override protected var currentValue: A = calc()

  dom.console.log(s"> Create $this")

  private[airstream] val parents: Seq[Signal[_]] // @TODO[API] this could be a set, probably

  val context: Context

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

  override def toString: String = s"ComputedSignal@${hashCode()}(value=$currentValue)"
}
