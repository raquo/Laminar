package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.{ChildInserter, Inserter}
import com.raquo.laminar.nodes.{ReactiveElement, TextNode}

object TextChildReceiver {

  // @TODO[Performance] We create new text nodes. Should we update the node's text instead? Also check how we treat comments.

  def <--[T]($node: Source[T])(implicit ev: T => TextNode): Inserter[ReactiveElement.Base] = {
    ChildInserter[ReactiveElement.Base](
      _ => $node.toObservable.map(ev(_)),
      initialInsertContext = None
    )
  }
}
