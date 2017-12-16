package com.raquo.laminar.streams

/** Read docs for [[MergeWriteBus]] first.
  *
  * MergeBus exposes a stream of events (see [[EventBus.$]]) that it receives as a MergeWriteBus.
  */
class MergeBus [A] extends EventBus[A] with MergeWriteBus[A] {

  // TODO[API] This is not foolproof. Someone could match {} this back up to a MergeBus.
  /** Typically when passing a MergeBus instance down to child components you only want those components
    * to add events to the bus, not to read all of the events on the bus (some of those events could have
    * been sent by other children that the given child has no business communicating with).
    *
    * This method is an easy way to express this intent in code.
    */
  @inline def asWriteBus: MergeWriteBus[A] = this
}
