package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source.EventSource
import com.raquo.laminar.inserters.{ChildrenCommandInserter, CollectionCommand, DynamicInserter}
import com.raquo.laminar.modifiers.RenderableNode

import scala.scalajs.js

object ChildrenCommandReceiver {

  def <--[Component](
    commands: EventSource[CollectionCommand[Component]]
  )(
    implicit renderableNode: RenderableNode[Component]
  ): DynamicInserter = {
    ChildrenCommandInserter(commands.toObservable, renderableNode, initialHooks = js.undefined)
  }
}
