package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source.EventSource
import com.raquo.laminar.modifiers.ChildrenCommandInserter.ChildrenCommand
import com.raquo.laminar.modifiers.{ChildrenCommandInserter, Inserter}
import com.raquo.laminar.nodes.ReactiveElement

object ChildrenCommandReceiver {

  def <--(commands: EventSource[ChildrenCommand]): Inserter[ReactiveElement.Base] = {
    ChildrenCommandInserter[ReactiveElement.Base](commands.toObservable)
  }
}
