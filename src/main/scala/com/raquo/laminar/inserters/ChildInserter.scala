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
    initialSlotName: String | Unit
  ): DynamicInserter = {
    new DynamicInserter(
      insertFn = (ctx, owner) => {
        var maybeLastSeenChild: js.UndefOr[ChildNode.Base] = js.undefined
        childSource.foreach { newComponent =>
          val newChildNode = renderable.asNode(newComponent)
          switchToChild(
            maybeLastSeenChild = maybeLastSeenChild,
            newChildNodeOpt = newChildNode,
            ctx = ctx,
            slotName = ctx.currentSlotName
          )
          maybeLastSeenChild = newChildNode
        }(using owner)
      },
      slotName = initialSlotName
    )
  }

  def switchToChild(
    maybeLastSeenChild: ChildNode.Base | Unit,
    newChildNodeOpt: ChildNode.Base | Unit,
    ctx: InsertContext,
    slotName: String | Unit
  ): Unit = {
    // In every case the outgoing node(s) unmount BEFORE the incoming one mounts, so a `child <--`
    // swap has a single, predictable lifecycle ordering regardless of what it's switching from.
    newChildNodeOpt.fold {
      // Nothing to render – just clean up whatever was there before.
      ctx.clearPreviousInserterContent(
        replaceContentMapWithSingleNode = js.undefined,
        nextInserterType = InserterType.ChildType
      )
    } { newChildNode =>
      maybeLastSeenChild
        .filter(_.ref == ctx.sentinelNode.ref.nextSibling) // Assert that the prev child node was not moved. Note: nextSibling could be null
        .fold {
          // Anything that does exist in the DOM, is not ours. Clean it up first.
          // The previous content may already contain newChildNode (even nested inside
          // one of its dynamic items) – if so, it's left in place, not unmounted.
          ctx.clearPreviousInserterContent(
            replaceContentMapWithSingleNode = newChildNode,
            nextInserterType = InserterType.ChildType
          )
          // Render the new child (or move it into place, if it was retained above)
          DomApi.insertChildAfter(
            parent = ctx.currentParentNode,
            newChild = newChildNode,
            referenceChildRef = ctx.sentinelNode.ref,
            slotName = slotName
          )
          ()
        } { lastSeenChild =>
          // We found the last seen child where we left it in the DOM. Replace it with the new child.
          // #Note: auto-distinction inside (`replaceChild` is a no-op if the nodes are equal)
          DomApi.replaceChild(
            parent = ctx.currentParentNode,
            oldChild = lastSeenChild,
            newChild = newChildNode,
            slotName = slotName
          )
          // Clear any other stale tracked nodes.
          // Usually there are none, There could be some if switching
          // from a multi-node inserter like `children <--` to `child <--`.
          // #TODO - would be nice if we could remove those before inserting the new node...
          ctx.clearPreviousInserterContent(
            replaceContentMapWithSingleNode = newChildNode,
            nextInserterType = InserterType.ChildType
          )
        }
    }
  }

}
