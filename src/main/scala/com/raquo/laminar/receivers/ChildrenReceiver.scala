package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.inserters.{ChildrenInserter, ChildrenSeq, DynamicInserter}
import com.raquo.laminar.modifiers.{RenderableNode, RenderableSeq}
import com.raquo.laminar.nodes.ChildNode

import scala.scalajs.js

object ChildrenReceiver {

  val command: ChildrenCommandReceiver.type = ChildrenCommandReceiver

  /** Example usages:
    *     children(node1, node2) <-- signalOfBoolean
    *     children(component1, component2) <-- signalOfBoolean
    */
  def apply(nodes: ChildNode.Base*): LockedChildrenReceiver = {
    new LockedChildrenReceiver(ChildrenSeq.from(nodes))
  }

  implicit class RichChildrenReceiver(val self: ChildrenReceiver.type) extends AnyVal {

    /** Example usages:
      *     children(listOfNodes) <-- signalOfBoolean
      *     children(arrayOfComponents) <-- signalOfBoolean
      */
    def apply[Collection[_], Component](
      components: Collection[Component]
    )(
      implicit renderableNode: RenderableNode[Component],
      renderableSeq: RenderableSeq[Collection]
    ): LockedChildrenReceiver = {
      val nodes = renderableNode.asNodeChildrenSeq(renderableSeq.toChildrenSeq(components))
      new LockedChildrenReceiver(nodes)
    }

    def <--(
      childrenSource: Source[Seq[ChildNode.Base]]
    ): DynamicInserter = {
      ChildrenInserter(
        childrenSource.toObservable,
        RenderableSeq.collectionSeqRenderable,
        RenderableNode.nodeRenderable,
        initialHooks = js.undefined
      )
    }

    def <--[Collection[_], Component](
      childrenSource: Source[Collection[Component]]
    )(
      implicit renderableNode: RenderableNode[Component],
      renderableSeq: RenderableSeq[Collection]
    ): DynamicInserter = {
      ChildrenInserter(
        childrenSource.toObservable,
        renderableSeq,
        renderableNode,
        initialHooks = js.undefined
      )
    }
  }

}
