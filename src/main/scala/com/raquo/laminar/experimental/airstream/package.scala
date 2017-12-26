package com.raquo.laminar.experimental

package object airstream {

  def batchUpdate(assignments: Assignment[_]*): Unit = {
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
