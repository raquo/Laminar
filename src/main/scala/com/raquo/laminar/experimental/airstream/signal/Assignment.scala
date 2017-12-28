package com.raquo.laminar.experimental.airstream.signal

/** Encapsulates the act of assigning a new value to a Var. Only used for type safety when issuing a batch update. */
class Assignment[A](val sourceVar: Var[A], val newValue: A) {

  private[airstream] def assign(): Boolean = {
    sourceVar.recalculate(newValue)
  }
}

object Assignment {

  // Implicit defs allow for Var.set(myVar1 -> value1, myVar2 -> value2) syntax

  implicit def tupleToAssignment[A](tuple: (Var[A], A)): Assignment[A] = {
    new Assignment(sourceVar = tuple._1, newValue = tuple._2)
  }

  implicit def tuplesToAssignments[A](tuples: Seq[(Var[A], A)]): Seq[Assignment[A]] = {
    tuples.map(tupleToAssignment)
  }
}
