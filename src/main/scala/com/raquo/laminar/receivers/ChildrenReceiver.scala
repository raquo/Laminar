package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.{ChildrenInserter, Inserter, RenderableNode}

import scala.collection.immutable

object ChildrenReceiver {

  val command: ChildrenCommandReceiver.type = ChildrenCommandReceiver

  // Note: currently this <-- method requires an observable of an
  // **immutable** Seq, but if needed, I might be able to implement
  // a version that works with arrays and mutable Seq-s too.
  // Let me know if you have a compelling use case for this.

  def <--[Component](
    childrenSource: Source[immutable.Seq[Component]]
  )(
    implicit renderableNode: RenderableNode[Component]
  ): Inserter.Base = {
    ChildrenInserter(childrenSource.toObservable, renderableNode)
  }

  // #TODO[API] I disabled this method because the more general <-- method below
  //  covers this use case as well. Retaining both methods eliminates the need for
  //  implicit resolution (user code compilation should be a bit faster), however
  //  it also degrades user experience with hard-to-parse "overloaded method with
  //  alternatives..." compiler errors when the user provides the wrong type of
  //  `childrenSource`.
  // def <--(childrenSource: Source[Children]): Inserter.Base = {
  //   ChildrenInserter(childrenSource.toObservable, RenderableNode.nodeRenderable)
  // }
}
