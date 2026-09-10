package com.raquo.laminar.inserters

import com.raquo.airstream.core.Observable
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.RenderableNode
import com.raquo.laminar.nodes.ChildNode

import scala.scalajs.js
import scala.scalajs.js.|

object ChildInserter {

  def apply[Component](
    childSource: Observable[Component],
    renderable: RenderableNode[Component],
    initialHooks: js.UndefOr[InserterHooks]
  ): DynamicInserter = {
    new DynamicInserter(
      insertFn = (ctx, owner, hooks) => {
        var maybeLastSeenChild: js.UndefOr[ChildNode.Base] = js.undefined
        childSource.foreach { newComponent =>
          val newChildNode = renderable.asNode(newComponent)
          switchToChild(
            maybeLastSeenChild = maybeLastSeenChild,
            newChildNodeOpt = newChildNode,
            ctx = ctx,
            hooks = hooks
          )
          maybeLastSeenChild = newChildNode
        }(using owner)
      },
      hooks = initialHooks
    )
  }

  def switchToChild(
    maybeLastSeenChild: ChildNode.Base | Unit,
    newChildNodeOpt: ChildNode.Base | Unit,
    ctx: InsertContext,
    hooks: InserterHooks | Unit
  ): Unit = {
    // A. Regardless of what the previous context / DOM state was,
    //    insert the `newChildNode` right after the sentinel node,
    //    where it belongs.
    newChildNodeOpt.foreach { newChildNode =>
      maybeLastSeenChild
        .filter(_.ref == ctx.sentinelNode.ref.nextSibling) // Assert that the prev child node was not moved. Note: nextSibling could be null
        .fold {
          // Inserting the child for the first time, OR after the previous child was externally moved / removed.
          DomApi.insertChildAfter(
            parent = ctx.parentNode,
            newChild = newChildNode,
            referenceChildRef = ctx.sentinelNode.ref,
            hooks = hooks
          )
          ()
        } { lastSeenChild =>
          // We found the last seen child where we left it in the DOM. Replace it with the new child.
          // #Note: auto-distinction inside (`replaceChild` is a no-op if the nodes are equal)
          DomApi.replaceChild(
            parent = ctx.parentNode,
            oldChild = lastSeenChild,
            newChild = newChildNode,
            hooks = hooks
          )
          ()
        }
    }

    // B. Update the context to match the sole `newChildNode` element that
    //    we've just put into the DOM, and clear any previous context's other
    //    nodes from the DOM.
    //    Note:
    //     - Such implementation is intended to keep `newChildNode` in the DOM
    //       without unnecessarily re-mounting it, even if it was previously
    //       rendered by a different inserter.
    //     - We do this AFTER the DOM updates above because ideally we want to
    //       `replace` the previous child in one shot, not remove+insert.
    ctx.clearPreviousInserterContent(
      replaceContentMapWithSingleNode = newChildNodeOpt,
      nextInserterType = InserterType.ChildType
    )
  }

}
