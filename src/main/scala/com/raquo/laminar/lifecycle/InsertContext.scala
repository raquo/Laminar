package com.raquo.laminar.lifecycle

import com.raquo.laminar.nodes.{ChildNode, CommentNode, ParentNode, ReactiveElement}

import scala.collection.immutable

// @Note only parentNode and sentinelNode are used by all Inserter-s.
//  - Other fields may remain un-updated if they are not needed for a particular implementation.
class InsertContext[+El <: ReactiveElement.Base](
  val parentNode: El,
  var sentinelNode: ChildNode.Base,
  var extraNodeCount: Int, // This is separate from `extraNodes` for performance
  var extraNodes: immutable.Seq[ChildNode.Base]
)

object InsertContext {

  /** Reserve the spot for when we actually insert real nodes later */
  def reserveSpotContext[El <: ReactiveElement.Base](parentNode: El): InsertContext[El] = {
    val sentinelNode = new CommentNode("")

    ParentNode.appendChild(parent = parentNode, child = sentinelNode)

    new InsertContext[El](
      parentNode = parentNode,
      sentinelNode = sentinelNode,
      extraNodeCount = 0,
      extraNodes = Nil
    )
  }
}
