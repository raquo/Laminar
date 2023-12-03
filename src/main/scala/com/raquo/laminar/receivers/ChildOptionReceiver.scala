package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.api.L.child
import com.raquo.laminar.inserters.DynamicInserter
import com.raquo.laminar.modifiers.RenderableNode
import com.raquo.laminar.nodes.{ChildNode, CommentNode}

object ChildOptionReceiver {

  def <--(maybeChildSource: Source[Option[ChildNode.Base]]): DynamicInserter = {
    val emptyNode = new CommentNode("")
    child <-- maybeChildSource.toObservable.map(_.getOrElse(emptyNode))
  }

  implicit class RichChildOptionReceiver(val self: ChildOptionReceiver.type) extends AnyVal {

    def <--[Component](
      maybeChildSource: Source[Option[Component]]
    )(
      implicit renderable: RenderableNode[Component]
    ): DynamicInserter = {
      val emptyNode = new CommentNode("")
      child <-- {
        maybeChildSource
          .toObservable
          .map(renderable.asNodeOption(_).getOrElse(emptyNode))
      }
    }
  }
}
