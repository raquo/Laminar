package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.{ChildInserter, Inserter}
import com.raquo.laminar.nodes.{ReactiveElement, TextNode}

object IntChildReceiver {

  def <--($node: Source[Int]): Inserter[ReactiveElement.Base] = {
    ChildInserter[ReactiveElement.Base](
      _ => $node.toObservable.map(int => new TextNode(int.toString)),
      initialInsertContext = None
    )
  }
}
