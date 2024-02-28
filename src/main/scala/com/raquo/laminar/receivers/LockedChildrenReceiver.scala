package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.api.L.children
import com.raquo.laminar.inserters.{ChildrenSeq, DynamicInserter}
import com.raquo.laminar.nodes.ChildNode

class LockedChildrenReceiver(
  val nodes: ChildrenSeq[ChildNode.Base]
) {

  /** If `include` is true, the nodes will be added. */
  @inline def apply(include: Boolean): ChildrenSeq[ChildNode.Base] = {
    this := include
  }

  /** If `include` is true, the nodes will be added. */
  def :=(include: Boolean): ChildrenSeq[ChildNode.Base] = {
    if (include) nodes else ChildrenSeq.empty
  }

  /** If `includeSource` emits true, node will be added. Otherwise, it will be removed. */
  def <--(includeSource: Source[Boolean]): DynamicInserter = {
    children <-- includeSource.toObservable.map(if (_) nodes else ChildrenSeq.empty)
  }

}
