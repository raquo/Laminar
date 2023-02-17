package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.{ChildInserter, Inserter, RenderableNode}
import com.raquo.laminar.nodes.ChildNode

object ChildReceiver {

  val maybe: ChildOptionReceiver.type = ChildOptionReceiver

  val text: ChildTextReceiver.type = ChildTextReceiver

  def <--(childSource: Source[ChildNode.Base]): Inserter.Base = {
    ChildInserter(childSource.toObservable, RenderableNode.nodeRenderable)
  }

  implicit class RichChildReceiver(val self: ChildReceiver.type) extends AnyVal {

    def <--[Component](childSource: Source[Component])(implicit renderable: RenderableNode[Component]): Inserter.Base = {
      ChildInserter(childSource.toObservable, renderable)
    }
  }

}
