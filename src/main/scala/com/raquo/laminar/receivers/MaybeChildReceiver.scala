package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.modifiers.{ChildInserter, Inserter}
import com.raquo.laminar.modifiers.ChildrenInserter.Child
import com.raquo.laminar.nodes.{CommentNode, ReactiveElement}

object MaybeChildReceiver {

  def <--($maybeChildNode: Observable[Option[Child]]): Inserter[ReactiveElement.Base] = {
    val emptyNode = new CommentNode("")
    ChildInserter[ReactiveElement.Base](_ => $maybeChildNode.map(_.getOrElse(emptyNode)), initialInsertContext = None)
  }
}
