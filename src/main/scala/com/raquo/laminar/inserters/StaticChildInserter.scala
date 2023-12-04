package com.raquo.laminar.inserters

import com.raquo.laminar.modifiers.RenderableNode
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement}

import scala.scalajs.js

/** Inserter for a single static node */
class StaticChildInserter(
  child: ChildNode.Base,
  hooks: js.UndefOr[InserterHooks]
) extends StaticInserter with Hookable[StaticChildInserter] {

  override def apply(element: ReactiveElement.Base): Unit = {
    hooks.foreach(_.onWillInsertNode(parent = element, child = child))
    child(element) // append child to element
  }

  def renderInContext(ctx: InsertContext): Unit = {
    ChildInserter.switchToChild(
      maybeLastSeenChild = js.undefined,
      newChildNode = child,
      ctx,
      hooks
    )
  }

  override def withHooks(addHooks: InserterHooks): StaticChildInserter = {
    new StaticChildInserter(child, addHooks.appendTo(hooks))
  }
}

object StaticChildInserter {

  def noHooks(
    node: ChildNode.Base,
  ): StaticChildInserter = {
    new StaticChildInserter(node, hooks = js.undefined)
  }

  def noHooksC[Component](
    component: Component
  )(
    implicit renderable: RenderableNode[Component]
  ): StaticChildInserter = {
    new StaticChildInserter(renderable.asNode(component), hooks = js.undefined)
  }
}
