package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.ChildrenInserter.Child
import com.raquo.laminar.modifiers.{ChildInserter, Inserter}
import com.raquo.laminar.nodes.{CommentNode, ReactiveElement}

object ChildOptionReceiver {

  def <--(maybeChildSource: Source[Option[Child]]): Inserter[ReactiveElement.Base] = {
    val emptyNode = new CommentNode("")
    ChildInserter(maybeChildSource.toObservable.map(_.getOrElse(emptyNode)))
  }
}
