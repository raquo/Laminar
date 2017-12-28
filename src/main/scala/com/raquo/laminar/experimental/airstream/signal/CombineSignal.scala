package com.raquo.laminar.experimental.airstream.signal

import org.scalajs.dom

trait CombineSignal[A] extends ComputedSignal[A] {

  override private[airstream] def propagate(haltOnNextCombine: Boolean): Unit = {
    dom.console.log(s"> ${this}.CombineSignal.propagate", haltOnNextCombine)
    if (haltOnNextCombine) {
      Propagation.addPendingSignal(this)
    } else {
      super.propagate(haltOnNextCombine = true)
    }
  }

  override def toString: String = s"CombineSignal@${hashCode()}(value=$currentValue)"
}
