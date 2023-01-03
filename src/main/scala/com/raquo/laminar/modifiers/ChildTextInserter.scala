package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.nodes.{ParentNode, ReactiveElement, TextNode}

import scala.scalajs.js

object ChildTextInserter {

  def apply[A, El <: ReactiveElement.Base] (
    textSource: Observable[A],
    renderable: Renderable[A]
  ): Inserter[El] = {
    new Inserter[El](
      preferStrictMode = false,
      insertFn = (ctx, owner) => {
        var maybeLastSeenChild: js.UndefOr[TextNode] = js.undefined
        textSource.foreach { newValue =>
          maybeLastSeenChild.fold {
            // First event: inserting the child for the first time: replace sentinel comment node with new TextNode
            val newTextNode = renderable.asTextNode(newValue)
            ParentNode.replaceChild(parent = ctx.parentNode, oldChild = ctx.sentinelNode, newChild = newTextNode)
            maybeLastSeenChild = newTextNode

            ctx.sentinelNode = newTextNode
            if (ctx.strictMode) {
              ctx.strictMode = false
              // We've just replaced the sentinel node with newTextNode,
              // so any remaining old child nodes must be directly under it.
              ctx.removeOldChildNodesFromDOM(after = newTextNode)
              // In loose mode, the child content node replaces the sentinel, and is not tracked in "extraNodes".
              // This is different from strict mode where the sentinel node is independent, to allow for moving
              // of elements in between different inserters (otherwise Laminar would lose track of the reserved
              // spot in such cases).
              ctx.extraNodesMap.clear()
              ctx.extraNodes = Nil
              ctx.extraNodeCount = 0
            }
            ()
          } { lastSeenChild =>
            // Subsequent events: updating the textContent field of the existing TextNode (which is also the sentinel node).
            val newText = renderable.asString(newValue)
            if (lastSeenChild.text != newText) { // #Note: auto-distinction
              lastSeenChild.ref.textContent = newText
            }
          }
        }(owner)
      }
    )
  }
}
