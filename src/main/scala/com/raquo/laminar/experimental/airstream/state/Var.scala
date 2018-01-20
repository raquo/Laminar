package com.raquo.laminar.experimental.airstream.state

import com.raquo.laminar.experimental.airstream.core.{Observable, Observation, Observer, Transaction}
import com.raquo.laminar.experimental.airstream.ownership.Owner
import org.scalajs.dom

import scala.scalajs.js

class Var[A](initialValue: A)(implicit owner: Owner) extends State[A] {

  override protected var currentValue: A = initialValue

  dom.console.log(s"> Create $this")

  private[this] var isDead = false

  val toObserver: Observer[A] = Observer(set)

  /** Update the value of this Var. Does nothing after this var is killed. */
  @inline def set(newValue: A): Unit = {
    if (!isDead) {
      Var.set(new Observation(this.toObserver, newValue)) // @TODO[Performance] can be denormalized to make more efficient
    }
  }

  /** Convenient syntax for myVar.set(fn(myVar.now())), e.g.:
    *
    * Var.update(_ + 1) // increment counter
    * Var.update(_.copy(someProp = newValue)) // update case class
    */
  @inline def update(fn: A => A): Unit = {
    set(fn(currentValue))
  }

  override protected[airstream] def syncDependsOn(
    otherObservable: Observable[_],
    seenObservables: js.Array[Observable[_]]
  ): Boolean = {
    // @TODO Wait, but how do we deal with this being an Observer, so essentially being able to observe any other Observable with no record of it?
    // @TODO I think the answer lies in this observation not being part of the propagation. Technically you could do anything inside any .foreach anyway.
    false
  }

  override protected[this] def registerWithOwner(): Unit = {
    owner.own(this)
  }

  override def toString: String = s"Var@${hashCode()}(value=$currentValue)"
}

object Var {

  /** Set multiple vars at once. These updates will be treated as a single propagation.
    *
    * Use this syntax for brevity: Var.set(myVar1 -> value1, myVar2 -> value2)
    * (Powered by implicit conversions defined in the [[Observation]] object)
    */
  def set(observations: Observation[_]*): Unit = {
    // @TODO The way it is, each update will happen in a separate transaction
    // @TODO rework this eventually once other things are done

    /// val transaction = new Transaction
    // @TODO this needs a transaction, if we decide to keep vars
    // First we update all source vars, and for each of them we initiate propagation
    observations.foreach { observation =>
      observation.observe()
//      observation.sourceVar.propagate(haltOnNextCombine = true)
    }
    //transaction.resolvePendingSyncObservables()
  }

}
