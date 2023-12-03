package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source.EventSource
import com.raquo.laminar.CollectionCommand
import com.raquo.laminar.modifiers.{ChildrenCommandInserter, DynamicInserter, RenderableNode}

object ChildrenCommandReceiver {

  def <--[Component](
    commands: EventSource[CollectionCommand[Component]]
  )(
    implicit renderableNode: RenderableNode[Component]
  ): DynamicInserter = {
    ChildrenCommandInserter(commands.toObservable, renderableNode)
  }
}
