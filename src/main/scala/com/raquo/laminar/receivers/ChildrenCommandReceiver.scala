package com.raquo.laminar.receivers

import com.raquo.airstream.eventstream.EventStream
import com.raquo.laminar.modifiers.{ChildrenCommandInserter, Inserter}
import com.raquo.laminar.modifiers.ChildrenCommandInserter.ChildrenCommand
import com.raquo.laminar.nodes.ReactiveElement

object ChildrenCommandReceiver {

  def <--(commandStream: EventStream[ChildrenCommand]): Inserter[ReactiveElement.Base] = {
    ChildrenCommandInserter[ReactiveElement.Base](_ => commandStream, initialInsertContext = None)
  }
}
