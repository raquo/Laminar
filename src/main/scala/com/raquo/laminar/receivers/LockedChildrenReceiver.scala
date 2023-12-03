package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.api.L.children
import com.raquo.laminar.inserters.DynamicInserter
import com.raquo.laminar.nodes.ChildNode

import scala.collection.immutable

class LockedChildrenReceiver(
  val nodes: immutable.Seq[ChildNode.Base]
) {

  /** If `include` is true, the nodes will be added. */
  @inline def apply(include: Boolean): immutable.Seq[ChildNode.Base] = {
    this := include
  }

  /** If `include` is true, the nodes will be added. */
  def :=(include: Boolean): immutable.Seq[ChildNode.Base] = {
    if (include) nodes else Nil
  }

  /** If `includeSource` emits true, node will be added. Otherwise, it will be removed. */
  def <--(includeSource: Source[Boolean]): DynamicInserter = {
    children <-- includeSource.toObservable.map(if (_) nodes else Nil)
  }

}
