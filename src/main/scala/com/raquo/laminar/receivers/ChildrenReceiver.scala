package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.ChildrenInserter.Children
import com.raquo.laminar.modifiers.{ChildrenInserter, Inserter}
import com.raquo.laminar.nodes.ReactiveElement

object ChildrenReceiver {

  val command: ChildrenCommandReceiver.type = ChildrenCommandReceiver

  // Note: currently this method requires an observable of an **immutable** Seq,
  // but if needed, I might be able to implement a version that works with
  // arrays and mutable Seq-s too.
  // Let me know if you have a compelling use case for this.
  def <--($children: Source[Children]): Inserter[ReactiveElement.Base] = {
    ChildrenInserter($children.toObservable)
  }
}
