package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.ChildrenInserter.Child
import com.raquo.laminar.modifiers.{ChildInserter, Inserter}
import com.raquo.laminar.nodes.{CommentNode, ReactiveElement}

object MaybeChildReceiver {

  def <--($maybeChildNode: Source[Option[Child]]): Inserter[ReactiveElement.Base] = {
    val emptyNode = new CommentNode("")
    ChildInserter[ReactiveElement.Base](
      _ => $maybeChildNode.toObservable.map(_.getOrElse(emptyNode)),
      initialInsertContext = None
    )
  }
}
