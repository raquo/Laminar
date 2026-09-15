package com.raquo.laminar.inserters

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.RenderableNode
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement}
import org.scalajs.dom

import scala.scalajs.js

/** Static inserter for a single static node, with optional hooks.
  *
  * Used for Web Components `Slot.apply`, which needs to
  * `setAttribute("slot", slot.name)` in the `onWillInsertNode` hook.
  *
  * In theory, ReactiveElement could become Hookable to avoid the
  * need for this wrapper, however this seems undesirable as hooks
  * are a property of the insertion point / mechanism, not the node.
  * However, currently hooks are not very principled either, for example
  * we use them to set the `slot` attribute, but we never unset this
  * attribute (e.g. if we move the element / inserter to a different place).
  *
  * Also, note that for slots, text nodes should not be allowed as they
  * can not be slotted, but this restriction is implemented in the implicit
  * conversion layer, not here, which is not very principled.
  *
  * See also `componentToInserter` implicit.
  */
class HookableChildInserter(
  child: ChildNode.Base,
  hooks: js.UndefOr[InserterHooks]
) extends StaticInserter with Hookable[HookableChildInserter] {

  private[laminar] override val stableFirstNode: dom.Node = child.ref

  private[laminar] override def lastNode: dom.Node = child.ref

  override def apply(element: ReactiveElement.Base): Unit = {
    DomApi.appendChild(
      parent = element,
      child = child,
      hooks = hooks
    )
  }

  override def renderInContext(ctx: InsertContext): Unit = {
    ChildInserter.switchToChild(
      maybeLastSeenChild = (),
      newChildNodeOpt = child,
      ctx = ctx,
      hooks = hooks
    )
  }

  private[laminar] override def addToDynamicList(
    parent: ReactiveElement.Base,
    afterRef: dom.Node,
    listHooks: js.UndefOr[InserterHooks]
  ): Unit = {
    // Cheap common case: a single static node is its own anchor + end, no sentinel.
    DomApi.insertChildAfter(
      parent = parent,
      newChild = child,
      referenceChildRef = afterRef,
      hooks = InserterHooks.concat(listHooks, hooks)
    )
  }

  private[laminar] override def removeFromDynamicList(parent: ReactiveElement.Base): Unit = {
    DomApi.removeChild(parent = parent, child = child)
  }

  override def withHooks(addHooks: InserterHooks): HookableChildInserter = {
    new HookableChildInserter(child, InserterHooks.concat(hooks, addHooks))
  }
}

object HookableChildInserter {

  def noHooks(
    node: ChildNode.Base,
  ): HookableChildInserter = {
    new HookableChildInserter(node, hooks = ())
  }

  def noHooksC[Component](
    component: Component
  )(implicit
    renderable: RenderableNode[Component]
  ): HookableChildInserter = {
    new HookableChildInserter(renderable.asNode(component), hooks = js.undefined)
  }
}
