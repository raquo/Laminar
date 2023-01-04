package com.raquo.laminar.nodes

import com.raquo.laminar.DomApi
import org.scalajs.dom

class CommentNode(initialText: String) extends ChildNode[dom.Comment] {

  final override val ref: dom.Comment = DomApi.createCommentNode(initialText)

  final def text: String = ref.data
}

