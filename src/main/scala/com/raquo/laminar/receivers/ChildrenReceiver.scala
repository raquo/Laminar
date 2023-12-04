package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.inserters.{ChildrenInserter, DynamicInserter}
import com.raquo.laminar.modifiers.RenderableNode
import com.raquo.laminar.nodes.ChildNode

import scala.collection.immutable
import scala.scalajs.js

object ChildrenReceiver {

  val command: ChildrenCommandReceiver.type = ChildrenCommandReceiver

  /** Example usage: children(listOfNodes) <-- signalOfBoolean */
  def apply(nodes: immutable.Seq[ChildNode.Base]): LockedChildrenReceiver = {
    new LockedChildrenReceiver(nodes)
  }

  /** Example usage: children(listOfComponents) <-- signalOfBoolean */
  def apply[Component](
    components: immutable.Seq[Component]
  )(
    implicit renderable: RenderableNode[Component]
  ): LockedChildrenReceiver = {
    new LockedChildrenReceiver(renderable.asNodeSeq(components))
  }

  // Note: currently this <-- method requires an observable of an
  // **immutable** Seq, but if needed, I might be able to implement
  // a version that works with arrays and mutable Seq-s too.
  // Let me know if you have a compelling use case for this.

  def <--(childrenSource: Source[immutable.Seq[ChildNode.Base]]): DynamicInserter = {
    ChildrenInserter(childrenSource.toObservable, RenderableNode.nodeRenderable, initialHooks = js.undefined)
  }

  def <--[Component](
    childrenSource: Source[immutable.Seq[Component]]
  )(
    implicit renderableNode: RenderableNode[Component]
  ): DynamicInserter = {
    ChildrenInserter(childrenSource.toObservable, renderableNode, initialHooks = js.undefined)
  }

  implicit class RichChildrenReceiver(val self: ChildrenReceiver.type) extends AnyVal {

    /** Example usage: children(node1, node2) <-- signalOfBoolean */
    def apply(nodes: ChildNode.Base*): LockedChildrenReceiver = {
      // #TODO[Scala 2.12] - toList is only needed because in Scala 2.12 varargs are (non-immutable) Seq
      new LockedChildrenReceiver(nodes.toList)
    }

    /** Example usage: children(component1, component2) <-- signalOfBoolean */
    def apply[Component](
      components: Component*
    )(
      implicit renderable: RenderableNode[Component]
    ): LockedChildrenReceiver = {
      // #TODO[Scala 2.12] - toList is only needed because in Scala 2.12 varargs are (non-immutable) Seq
      new LockedChildrenReceiver(renderable.asNodeSeq(components.toList))
    }
  }
}
