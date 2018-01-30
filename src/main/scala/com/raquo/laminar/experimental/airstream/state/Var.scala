package com.raquo.laminar.experimental.airstream.state

import com.raquo.laminar.experimental.airstream.core.{Observation, Observer}
import com.raquo.laminar.experimental.airstream.ownership.Owner
import org.scalajs.dom

class Var[A](override protected[this] val initialValue: A)(implicit owner: Owner) extends State[A] {

  dom.console.log(s"> Create $this")

  override protected[airstream] val topoRank: Int = 1

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
    set(fn(now()))
  }

  override protected[this] def registerWithOwner(): Unit = {
    owner.own(this)
  }

  override def toString: String = s"Var@${hashCode()}(value=${now()})"
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
