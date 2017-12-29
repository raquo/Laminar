package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.observation.Observer
import com.raquo.laminar.experimental.airstream.ownership.Owner
import org.scalajs.dom

class Var[A](initialValue: A)(override protected implicit val owner: Owner) extends Signal[A] with Observer[A] {

  override protected var currentValue: A = initialValue

  dom.console.log(s"> Create $this")

  private[this] var isDead = false

  /** Update the value of this Var. Does nothing after this var is killed. */
  @inline def set(newValue: A): Unit = {
    onNext(newValue)
  }

  /** Convenient syntax for myVar.set(fn(myVar.now())), e.g.:
    *
    * Var.update(_ + 1) // increment counter
    * Var.update(_.copy(someProp = newValue)) // update case class
    */
  @inline def update(fn: A => A): Unit = {
    onNext(fn(currentValue))
  }

  /** Standard [[Observer]] method for receiving a new value.
    * [[set]] and [[update]] methods call into this, and are provided just for convenience. */
  override def onNext(nextValue: A): Unit = {
    if (!isDead) {
      Var.set(new Assignment(this, nextValue)) // @TODO[Performance] can be denormalized to make more efficient
    }
  }

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
