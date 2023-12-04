package com.raquo.laminar.inserters

import com.raquo.airstream.core.Transaction
import com.raquo.laminar.modifiers.RenderableNode
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement}

import scala.collection.immutable
import scala.scalajs.js

/**
  * Inserter for multiple static nodes.
  * This can also insert a single nodes, just a bit less efficiently
  * than SingleStaticInserter.
  */
class StaticChildrenInserter(
  nodes: collection.Seq[ChildNode.Base],
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
    ChildrenInserter.switchToChildren(nodes.toList, ctx, hooks)
  }

  override def withHooks(addHooks: InserterHooks): StaticChildrenInserter = {
    new StaticChildrenInserter(nodes, addHooks.appendTo(hooks))
  }

}

object StaticChildrenInserter {

  def noHooks(
    nodes: collection.Seq[ChildNode.Base],
  ): StaticChildrenInserter = {
    new StaticChildrenInserter(nodes, hooks = js.undefined)
  }

  def noHooksC[Component](
    components: immutable.Seq[Component]
  )(
    implicit renderable: RenderableNode[Component]
  ): StaticChildrenInserter = {
    new StaticChildrenInserter(renderable.asNodeSeq(components), hooks = js.undefined)
  }

}
