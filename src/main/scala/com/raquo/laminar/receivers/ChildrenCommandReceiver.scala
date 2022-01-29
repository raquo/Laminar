package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source.EventSource
import com.raquo.laminar.modifiers.ChildrenCommandInserter.ChildrenCommand
import com.raquo.laminar.modifiers.{ChildrenCommandInserter, Inserter}
import com.raquo.laminar.nodes.ReactiveElement

object ChildrenCommandReceiver {

  def <--($command: EventSource[ChildrenCommand]): Inserter[ReactiveElement.Base] = {
    ChildrenCommandInserter[ReactiveElement.Base](_ => $command.toObservable)
  }
}
