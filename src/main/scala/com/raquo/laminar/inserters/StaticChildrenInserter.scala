package com.raquo.laminar.inserters

import com.raquo.airstream.core.Transaction
import com.raquo.laminar
import com.raquo.laminar.modifiers.{RenderableNode, RenderableSeq}
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement}

import scala.scalajs.js

/**
  * Inserter for multiple static nodes.
  * This can also insert a single nodes, just a bit less efficiently
  * than SingleStaticInserter.
  */
class StaticChildrenInserter(
  nodes: laminar.Seq[ChildNode.Base],
  hooks: js.UndefOr[InserterHooks]
) extends StaticInserter with Hookable[StaticChildrenInserter] {

  override def apply(element: ReactiveElement.Base): Unit = {
    Transaction.onStart.shared {
      nodes.foreach { node =>
        hooks.foreach(_.onWillInsertNode(parent = element, child = node))
        node(element) // append node to element
      }
    }
  }

  override def renderInContext(ctx: InsertContext): Unit = {
    ChildrenInserter.switchToChildren(nodes, ctx, hooks)
  }

  override def withHooks(addHooks: InserterHooks): StaticChildrenInserter = {
    new StaticChildrenInserter(nodes, addHooks.appendTo(hooks))
  }

}

object StaticChildrenInserter {

  def noHooks[Collection[_], Component](
    components: Collection[Component],
    renderableSeq: RenderableSeq[Collection],
    renderableNode: RenderableNode[Component]
  ): StaticChildrenInserter = {
    val children = renderableNode.asNodeSeq(renderableSeq.toSeq(components))
    new StaticChildrenInserter(children, hooks = js.undefined)
  }

}
