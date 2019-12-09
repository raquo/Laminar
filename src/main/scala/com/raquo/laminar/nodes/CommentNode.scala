package com.raquo.laminar.nodes

import com.raquo.domtypes
import com.raquo.laminar.DomApi
import org.scalajs.dom

class CommentNode(val text: String)
  extends ChildNode[dom.Comment]
  with domtypes.generic.nodes.Comment {

  final override val ref: dom.Comment = DomApi.createCommentNode(text)
}

