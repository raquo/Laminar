package com.raquo.laminar.inserters

import com.raquo.laminar.nodes.{ReactiveElement, TextNode}

/** Inserter for a single static node */
class StaticTextInserter(
  text: String
) extends StaticInserter {

  override def apply(element: ReactiveElement.Base): Unit = {
    (new TextNode(text))(element) // append text node to element
  }

  def renderInContext(ctx: InsertContext): Unit = {
    ChildTextInserter.switchToChild(new TextNode(text), ctx)
  }

}
