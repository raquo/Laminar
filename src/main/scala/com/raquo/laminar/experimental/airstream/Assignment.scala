package com.raquo.laminar.experimental.airstream

/** Encapsulates the act of assigning a new value to a Var. Only used for type safety when issuing a batch update. */
case class Assignment[A](sourceVar: Var[A], newValue: A) {

  private[airstream] def assign(): Boolean = {
    sourceVar.recalculate(newValue)
  }
}
