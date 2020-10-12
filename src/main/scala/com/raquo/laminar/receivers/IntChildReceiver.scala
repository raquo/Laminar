package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.nodes.{ReactiveElement, TextNode}
import com.raquo.laminar.modifiers.{ChildInserter, Inserter}

object IntChildReceiver {

  def <--($node: Observable[Int]): Inserter[ReactiveElement.Base] = {
    ChildInserter[ReactiveElement.Base](_ => $node.map(int => new TextNode(int.toString)), initialInsertContext = None)
  }
}
