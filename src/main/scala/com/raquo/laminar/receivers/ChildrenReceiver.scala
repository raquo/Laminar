package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.inserters.{ChildrenInserter, DynamicInserter}
import com.raquo.laminar.modifiers.{RenderableInserter, RenderableSeq}

import scala.scalajs.js

object ChildrenReceiver {

  val command: ChildrenCommandReceiver.type = ChildrenCommandReceiver

  implicit class RichChildrenReceiver(private val self: ChildrenReceiver.type) extends AnyVal {

    /** `children <-- observableOfListOfItems`
      *
      * Each item is converted to an [[com.raquo.laminar.inserters.Inserter]] via
      * [[RenderableInserter]]. In practice an item can be:
      *  - a Laminar node, or any component with a `RenderableNode` instance
      *    (rendered as a single static node, allocation-cheap – no sentinel);
      *  - a dynamic inserter such as `child <-- ...` or `children <-- ...`, which
      *    can be nested directly among the children (Laminar issue #157), e.g.
      *    returning `child <-- ...` from inside a `split` callback.
      */
    def <--[Collection[_], Component](
      childrenSource: Source[Collection[Component]]
    )(implicit
      renderableInserter: RenderableInserter[Component],
      renderableSeq: RenderableSeq[Collection]
    ): DynamicInserter = {
      ChildrenInserter(
        childrenSource.toObservable,
        renderableSeq,
        renderableInserter,
        initialSlotName = ()
      )
    }
  }

}
