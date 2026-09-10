package com.raquo.laminar.inserters

import com.raquo.airstream.core.Transaction
import com.raquo.ew.JsArray
import com.raquo.laminar
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.{RenderableInserter, RenderableNode, RenderableSeq}
import com.raquo.laminar.nodes.{ChildNode, CommentNode, ReactiveElement}
import org.scalajs.dom

import scala.scalajs.js

/** Static inserter for a static list of nodes, with optional hooks.
  *
  * Used when:
  *  - onMountInsert callback returns a Seq, or
  *  - when passing Seq[Node] as one of the items to Web Components `Slot.apply`.
  *
  * See also comments in [[HookableChildInserter]] and `componentSeqToInserter` implicit.
  *
  * Note that [[mutableNodes]] MIGHT be mutable – we [[nodesToRender]].
  */
class HookableChildrenInserter(
  mutableNodes: laminar.Seq[ChildNode.Base],
  hooks: js.UndefOr[InserterHooks]
) extends StaticInserter with Hookable[HookableChildrenInserter] {

  /** We don't want to depend arbitrarily on [[mutableNodes]]
    * potentially mutating from under us, so we take a snapshot upfront.
    *
    * Also, this is guaranteed to be NON-EMPTY, which is a requirement
    * for our [[stableFirstNode]] and [[lastNode]] logic. We create a
    * fake comment node if the source [[mutableNodes]] is empty.
    */
  private val nodesToRender: laminar.Seq[ChildNode.Base] = {
    if (mutableNodes.nonEmpty)
      mutableNodes.immutableSnapshot
    else
      laminar.Seq.from(
        JsArray(new CommentNode(""))
      )
  }

  override def apply(element: ReactiveElement.Base): Unit = {
    Transaction.onStart.shared {
      nodesToRender.foreach { node =>
        DomApi.appendChild(
          parent = element,
          child = node,
          hooks = hooks
        )
      }
    }
  }

  override def renderInContext(ctx: InsertContext): Unit = {
    // A node is its own Inserter, so it renders as its own list item directly.
    ChildrenInserter.switchToChildren(
      nextItems = nodesToRender,
      renderable = RenderableInserter.inserterRenderable,
      ctx = ctx,
      hooks = hooks
    )
  }

  override private[laminar] val stableFirstNode: dom.Node = nodesToRender.head.ref

  override private[laminar] def lastNode: dom.Node = nodesToRender.last.ref

  override private[laminar] def addToDynamicList(
    parent: ReactiveElement.Base,
    afterRef: dom.Node,
    hooks: js.UndefOr[InserterHooks]
  ): Unit = {
    var insertAfter = afterRef
    nodesToRender.foreach { node =>
      DomApi.insertChildAfter(
        parent = parent,
        newChild = node,
        referenceChildRef = insertAfter,
        hooks = hooks
      )
      insertAfter = node.ref
    }
  }

  override private[laminar] def removeFromDynamicList(parent: ReactiveElement.Base): Unit = {
    nodesToRender.foreach { node =>
      DomApi.removeChild(parent, node)
    }
  }

  override def withHooks(addHooks: InserterHooks): HookableChildrenInserter = {
    new HookableChildrenInserter(mutableNodes, addHooks.appendTo(hooks))
  }

}

object HookableChildrenInserter {

  def noHooks[Collection[_], Component](
    components: Collection[Component],
    renderableSeq: RenderableSeq[Collection],
    renderableNode: RenderableNode[Component]
  ): HookableChildrenInserter = {
    val children = renderableNode.asNodeSeq(renderableSeq.toSeq(components))
    new HookableChildrenInserter(children, hooks = js.undefined)
  }

}
