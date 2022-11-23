package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.ChildrenInserter.Child
import com.raquo.laminar.modifiers.{ChildInserter, Inserter}
import com.raquo.laminar.nodes.ReactiveElement

object ChildReceiver {

  val maybe: ChildOptionReceiver.type = ChildOptionReceiver

  val text: ChildTextReceiver.type = ChildTextReceiver

  def <--($node: Source[Child]): Inserter[ReactiveElement.Base] = {
    ChildInserter($node.toObservable)
  }
}
