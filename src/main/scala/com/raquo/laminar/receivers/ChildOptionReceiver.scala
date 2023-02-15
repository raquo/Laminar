package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.api.Laminar.child
import com.raquo.laminar.modifiers.{Inserter, RenderableNode}
import com.raquo.laminar.nodes.CommentNode

object ChildOptionReceiver {

  def <--[Component](
    maybeChildSource: Source[Option[Component]]
  )(
    implicit renderable: RenderableNode[Component]
  ): Inserter.Base = {
    val emptyNode = new CommentNode("")
    child <-- {
      maybeChildSource
        .toObservable
        .map(renderable.asNodeOption(_).getOrElse(emptyNode))
    }
  }

  // #TODO[API] I disabled this method because the more general <-- method below
  //  covers this use case as well. Retaining both methods eliminates the need for
  //  implicit resolution (user code compilation should be a bit faster), however
  //  it also degrades user experience with hard-to-parse "overloaded method with
  //  alternatives..." compiler errors when the user provides the wrong type of
  //  `maybeChildSource`.

  // def <--(maybeChildSource: Source[Option[Child]]): Inserter.Base = {
  //   val emptyNode = new CommentNode("")
  //   child <-- maybeChildSource.toObservable.map(_.getOrElse(emptyNode))
  // }
}
