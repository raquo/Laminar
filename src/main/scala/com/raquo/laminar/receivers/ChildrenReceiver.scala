package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.inserters.{ChildrenInserter, DynamicInserter}
import com.raquo.laminar.modifiers.{RenderableNode, RenderableSeq}
import com.raquo.laminar.nodes.ChildNode

import scala.scalajs.js

object ChildrenReceiver {

  val command: ChildrenCommandReceiver.type = ChildrenCommandReceiver

  implicit class RichChildrenReceiver(val self: ChildrenReceiver.type) extends AnyVal {

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
      // Route the Option case to simpler more efficient child.maybe receiver.
      if (renderableSeq == RenderableSeq.optionRenderable) {
        ChildOptionReceiver <-- childrenSource.asInstanceOf[Source[Option[Component]]]
        // #TODO child.maybe can't handle js.UndefOr yet
        // } else if (renderableSeq == RenderableSeq.jsUndefOrRenderable) {
        //   ChildOptionReceiver <-- childrenSource.asInstanceOf[Source[js.UndefOr[Component]]]
      } else {
        ChildrenInserter(
          childrenSource.toObservable,
          renderableSeq,
          renderableNode,
          initialHooks = js.undefined
        )
      }
    }
  }

}
