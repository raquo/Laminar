package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.ChildrenInserter.Child
import com.raquo.laminar.modifiers.{ChildInserter, Inserter}
import com.raquo.laminar.nodes.ReactiveElement

object ChildReceiver {

  val maybe: MaybeChildReceiver.type = MaybeChildReceiver

  val text: TextChildReceiver.type = TextChildReceiver

  val int: IntChildReceiver.type = IntChildReceiver

  def <--($node: Source[Child]): Inserter[ReactiveElement.Base] = {
    ChildInserter[ReactiveElement.Base](
      _ => $node.toObservable,
      initialInsertContext = None
    )
  }
}
