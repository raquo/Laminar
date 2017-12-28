package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.ownership.Owner
import org.scalajs.dom

class Var[A](initialValue: A)(override protected implicit val owner: Owner) extends Signal[A] {

  override protected var currentValue: A = initialValue

  dom.console.log(s"> Create $this")

  private[this] var isDead = false

  /** Update the value of this Var. Does nothing after this var is killed. */
  def set(newValue: A): Unit = {
    if (!isDead) {
      Var.set(new Assignment(this, newValue)) // @TODO[Performance] can be denormalized to make more efficient
    }
  }

  /** Convenient syntax for myVar.set(fn(myVar.now())), e.g.:
    *
    * Var.update(_ + 1) // increment counter
    * Var.update(_.copy(someProp = newValue)) // update case class
    */
  def update(fn: A => A): Unit = {
    set(fn(currentValue))
  }

  // TODO I think this and set() should be part of the Observer API... Maybe... Think about it.
  // The original stream can always be transformed to A
  // @TODO Make sure to properly handle killing when we add/remove these streams
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

  override private[airstream] def kill(): Unit = {
    super.kill()
    isDead = true
  }

  override def toString: String = s"Var@${hashCode()}(value=$currentValue)"
}

object Var {

  /** Set multiple vars at once. These updates will be treated as a single propagation.
    *
    * Use this syntax for brevity: Var.set(myVar1 -> value1, myVar2 -> value2)
    * (Powered by implicit conversions defined in the [[Assignment]] object)
    */
  def set(assignments: Assignment[_]*): Unit = {
    // First we update all source vars, and for each of them we initiate propagation
    assignments.foreach { assignment =>
      assignment.assign()
      assignment.sourceVar.propagate(haltOnNextCombine = true)
    }
    // We now have some pending signals and observations, and at this point
    // it does not matter anymore how many vars were initially updated.
    Propagation.finalizePropagation()
  }

}
