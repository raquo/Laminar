package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.ChildrenInserter.Children
import com.raquo.laminar.modifiers.{ChildrenInserter, Inserter}
import com.raquo.laminar.nodes.ReactiveElement

object ChildrenReceiver {

  val command: ChildrenCommandReceiver.type = ChildrenCommandReceiver

  def <--($children: Source[Children]): Inserter[ReactiveElement.Base] = {
    ChildrenInserter[ReactiveElement.Base](_ => $children.toObservable)
  }
}
