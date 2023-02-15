package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.nodes.{ChildNode, ParentNode, ReactiveElement}

import scala.scalajs.js

object ChildInserter {

  def apply[Component] (
    childSource: Observable[Component],
    renderable: RenderableNode[Component]
  ): Inserter.Base = {
    new Inserter[ReactiveElement.Base](
      preferStrictMode = true,
      insertFn = (ctx, owner) => {
        if (!ctx.strictMode) {
          ctx.forceSetStrictMode()
        }

        var maybeLastSeenChild: js.UndefOr[ChildNode.Base] = js.undefined

        childSource.foreach { newComponent =>
          val newChildNode = renderable.asNode(newComponent)
          var remainingOldExtraNodeCount = ctx.extraNodeCount

          maybeLastSeenChild
            .filter(_.ref == ctx.sentinelNode.ref.nextSibling) // Assert that the prev child node was not moved. Note: nextSibling could be null
            .fold {
              // Inserting the child for the first time, OR after the previous child was externally moved / removed.
              val sentinelNodeIndex = ParentNode.indexOfChild(ctx.parentNode, ctx.sentinelNode)
              ParentNode.insertChild(parent = ctx.parentNode, newChildNode, atIndex = sentinelNodeIndex + 1)
              ()
            } { lastSeenChild =>
              // We found the existing child in the right place in the DOM
              // Just need to check that the new child is actually different from the old one
              // Replace the child with new one.
              // #Note: auto-distinction inside (`lastSeenChild ne newChildNode` filter)
              val replaced = ParentNode.replaceChild(
                parent = ctx.parentNode,
                oldChild = lastSeenChild,
                newChild = newChildNode
              )
              if (replaced || (lastSeenChild eq newChildNode)) { // #TODO[Performance,Integrity] Not liking this redundant auto-distinction
                // The only time we DON'T decrement this is when replacing fails for unexpected reasons.
                // - If lastSeenChild == newChildNode, then it's not an "old" node anymore, so we decrement
                // - If replaced == true, then lastSeenChild was removed from the DOM, so we decrement
                remainingOldExtraNodeCount -= 1
              }
              ()
            }

          // We've just inserted newChildNode after the sentinel, or replaced the first old node with newChildNode,
          // so any remaining old child nodes must be directly under it.
          ctx.removeOldChildNodesFromDOM(after = newChildNode)

          ctx.extraNodesMap.clear()
          ctx.extraNodesMap.set(newChildNode.ref, newChildNode)
          ctx.extraNodes = newChildNode :: Nil
          ctx.extraNodeCount = 1

          maybeLastSeenChild = newChildNode
        }(owner)
      }
    )
  }
}
