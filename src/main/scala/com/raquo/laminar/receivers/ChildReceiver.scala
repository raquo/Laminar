package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.modifiers.{ChildInserter, Inserter}
import com.raquo.laminar.modifiers.ChildrenInserter.Child

object ChildReceiver {

  val maybe: MaybeChildReceiver.type = MaybeChildReceiver

  val text: TextChildReceiver.type = TextChildReceiver

  def <--($node: Observable[Child]): Inserter[ReactiveElement.Base] = {
    ChildInserter[ReactiveElement.Base](_ => $node, initialInsertContext = None)
  }
}
