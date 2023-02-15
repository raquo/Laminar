package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.{ChildInserter, Inserter, RenderableNode}

object ChildReceiver {

  val maybe: ChildOptionReceiver.type = ChildOptionReceiver

  val text: ChildTextReceiver.type = ChildTextReceiver

  def <--[Component](childSource: Source[Component])(implicit renderable: RenderableNode[Component]): Inserter.Base = {
    ChildInserter(childSource.toObservable, renderable)
  }

  // #TODO[API] I disabled this method because the more general <-- method below
  //  covers this use case as well. Retaining both methods eliminates the need for
  //  implicit resolution (user code compilation should be a bit faster), however
  //  it also degrades user experience with hard-to-parse "overloaded method with
  //  alternatives..." compiler errors when the user provides the wrong type of
  //  `childSource`.

  // def <--(childSource: Source[Child]): Inserter.Base = {
  //   ChildInserter(childSource.toObservable, RenderableNode.nodeRenderable)
  // }
}
