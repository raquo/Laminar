package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar
import com.raquo.laminar.api.L.children
import com.raquo.laminar.inserters.DynamicInserter
import com.raquo.laminar.nodes.ChildNode

class LockedChildrenReceiver(
  _nodes: => laminar.Seq[ChildNode.Base]
) {

  @inline def nodes: laminar.Seq[ChildNode.Base] = _nodes

  /** If `include` is true, the nodes will be added. */
  @inline def apply(include: Boolean): laminar.Seq[ChildNode.Base] = {
    this := include
  }

  /** If `include` is true, the nodes will be added. */
  def :=(include: Boolean): laminar.Seq[ChildNode.Base] = {
    if (include) nodes else laminar.Seq.empty
  }

  /** If `includeSource` emits true, node will be added. Otherwise, it will be removed. */
  def <--(includeSource: Source[Boolean]): DynamicInserter = {
    children <-- includeSource.toObservable.map(if (_) nodes else laminar.Seq.empty)
  }

}
