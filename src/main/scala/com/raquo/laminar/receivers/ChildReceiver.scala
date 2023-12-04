package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.inserters.{ChildInserter, DynamicInserter}
import com.raquo.laminar.modifiers.RenderableNode
import com.raquo.laminar.nodes.ChildNode

import scala.scalajs.js

object ChildReceiver {

  val maybe: ChildOptionReceiver.type = ChildOptionReceiver

  val text: ChildTextReceiver.type = ChildTextReceiver

  /** Usage example: child(element) <-- signalOfBoolean */
  def apply(node: ChildNode.Base): LockedChildReceiver = {
    new LockedChildReceiver(node)
  }

  def <--(childSource: Source[ChildNode.Base]): DynamicInserter = {
    ChildInserter(childSource.toObservable, RenderableNode.nodeRenderable, initialHooks = js.undefined)
  }

  implicit class RichChildReceiver(val self: ChildReceiver.type) extends AnyVal {

    /** Usage example: child(component) <-- signalOfBoolean */
    def apply[Component](
      component: Component
    )(
      implicit renderable: RenderableNode[Component]
    ): LockedChildReceiver = {
      new LockedChildReceiver(renderable.asNode(component))
    }

    def <--[Component](
      childSource: Source[Component]
    )(
      implicit renderable: RenderableNode[Component]
    ): DynamicInserter = {
      ChildInserter(childSource.toObservable, renderable, initialHooks = js.undefined)
    }
  }

}
