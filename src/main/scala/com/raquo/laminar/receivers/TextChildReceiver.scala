package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.nodes.{ReactiveElement, TextNode}
import com.raquo.laminar.modifiers.{ChildInserter, Inserter}

object TextChildReceiver {

  def <--($node: Observable[String]): Inserter[ReactiveElement.Base] = {
    ChildInserter[ReactiveElement.Base](_ => $node.map(new TextNode(_)), initialInsertContext = None)
  }
}
