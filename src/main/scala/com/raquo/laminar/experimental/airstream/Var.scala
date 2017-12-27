package com.raquo.laminar.experimental.airstream

import org.scalajs.dom

class Var[A](initialValue: A) extends Signal[A] {

  override protected var currentValue: A = initialValue

  dom.console.log(s"> Create $this")

  def set(newValue: A): Unit = {
    Var.set(Assignment(this, newValue)) // @TODO[Performance] can be denormalized to make more efficient
  }

  // The original stream can always be transformed to A
//  def updateFromStream(sourceStream: Stream[A])(implicit context: Context): Unit = {
//    sourceStream.foreach(this.update, context)
//  }

  /** @return whether value was changed */
  private[airstream] def recalculate(newValue: A): Boolean = {
    val valueChanged = newValue != currentValue
    if (valueChanged) {
      currentValue = newValue
    }
    valueChanged
  }

  override def toString: String = s"Var@${hashCode()}(value=$currentValue)"
}

object Var {

  def set(assignments: Assignment[_]*): Unit = {
    // First we update all source vars, and for each of them we initiate propagation
    assignments.foreach { assignment =>
      assignment.assign()
      assignment.sourceVar.propagate(haltOnNextCombine = true)
    }
    // We now have some pending signals and observations, and at this point
    // it does not matter anymore how many vars were initially updated.
    Airstream.finalizePropagation()
  }
}
