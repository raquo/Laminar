package com.raquo.laminar.nodes

import com.raquo.laminar.domapi.DomApi
import org.scalajs.dom

import scala.scalajs.js.|

class CommentNode(initialText: String) extends ChildNode[dom.Comment] {

  final override val ref: dom.Comment = DomApi.createCommentNode(initialText)

  final def text: String = ref.data

  // Comment nodes are never slotted
  override private[laminar] def applySlot(
    debugParent: ParentNode.Base,
    slotName: String | Unit
  ): Unit = ()
}
