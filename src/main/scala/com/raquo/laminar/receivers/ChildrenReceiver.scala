package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.modifiers.{ChildrenInserter, Inserter}
import com.raquo.laminar.modifiers.ChildrenInserter.Children

object ChildrenReceiver {

  val command: ChildrenCommandReceiver.type = ChildrenCommandReceiver

  def <--($children: Observable[Children]): Inserter[ReactiveElement.Base] = {
    ChildrenInserter[ReactiveElement.Base](_ => $children, initialInsertContext = None)
  }
}
