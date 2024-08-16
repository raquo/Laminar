package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar
import com.raquo.laminar.inserters.{ChildrenInserter, DynamicInserter}
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
    new LockedChildrenReceiver(laminar.Seq.from(nodes))
  }

  implicit class RichChildrenReceiver(val self: ChildrenReceiver.type) extends AnyVal {

    /** Example usages:
      *     children(listOfNodes) <-- signalOfBoolean
      *     children(arrayOfComponents) <-- signalOfBoolean
      */
    def apply[Collection[_], Component](
      components: Collection[Component]
    )(implicit
      renderableNode: RenderableNode[Component],
      renderableSeq: RenderableSeq[Collection]
    ): LockedChildrenReceiver = {
      val nodes = renderableNode.asNodeSeq(renderableSeq.toSeq(components))
      new LockedChildrenReceiver(nodes)
    }

    // #TODO[UX] Can I remove this method, to improve error messages, get rid of "none of the overloaded alternatives" error?
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
    )(implicit
      renderableNode: RenderableNode[Component],
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
