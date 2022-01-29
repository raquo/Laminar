package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.{Inserter, TextInserter}
import com.raquo.laminar.nodes.{ReactiveElement, TextNode}

object TextChildReceiver {

  // @TODO[Performance] We create new text nodes for every event. Should we update the node's text instead?

  def <--[T]($node: Source[T])(implicit render: T => TextNode): Inserter[ReactiveElement.Base] = {
    TextInserter[T](_ => $node.toObservable, render)
  }
}
