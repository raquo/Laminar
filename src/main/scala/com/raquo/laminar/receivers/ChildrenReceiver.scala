package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.{ChildrenInserter, Inserter, RenderableNode}
import com.raquo.laminar.nodes.ChildNode

import scala.collection.immutable

object ChildrenReceiver {

  val command: ChildrenCommandReceiver.type = ChildrenCommandReceiver

  // Note: currently this <-- method requires an observable of an
  // **immutable** Seq, but if needed, I might be able to implement
  // a version that works with arrays and mutable Seq-s too.
  // Let me know if you have a compelling use case for this.

  def <--(childrenSource: Source[immutable.Seq[ChildNode.Base]]): Inserter.Base = {
    ChildrenInserter(childrenSource.toObservable, RenderableNode.nodeRenderable)
  }

  def <--[Component](
    childrenSource: Source[immutable.Seq[Component]]
  )(
    implicit renderableNode: RenderableNode[Component]
  ): Inserter.Base = {
    ChildrenInserter(childrenSource.toObservable, renderableNode)
  }
}
