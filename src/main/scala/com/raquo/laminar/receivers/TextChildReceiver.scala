package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.{ChildInserter, Inserter}
import com.raquo.laminar.nodes.{ReactiveElement, TextNode}

object TextChildReceiver {

  def <--($node: Source[String]): Inserter[ReactiveElement.Base] = {
    ChildInserter[ReactiveElement.Base](
      _ => $node.toObservable.map(new TextNode(_)),
      initialInsertContext = None
    )
  }
}
