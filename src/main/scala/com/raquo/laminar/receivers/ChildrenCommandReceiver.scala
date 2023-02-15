package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source.EventSource
import com.raquo.laminar.CollectionCommand
import com.raquo.laminar.modifiers.{ChildrenCommandInserter, Inserter, RenderableNode}

object ChildrenCommandReceiver {

  def <--[Component](
    commands: EventSource[CollectionCommand[Component]]
  )(
    implicit renderableNode: RenderableNode[Component]
  ): Inserter.Base = {
    ChildrenCommandInserter(commands.toObservable, renderableNode)
  }
}
