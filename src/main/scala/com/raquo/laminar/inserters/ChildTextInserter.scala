package com.raquo.laminar.inserters

import com.raquo.airstream.core.Observable
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.RenderableText
import com.raquo.laminar.nodes.{ChildNode, CommentNode, TextNode}

import scala.scalajs.js

object ChildTextInserter {

  def apply[Component](
    textSource: Observable[Component],
    renderable: RenderableText[Component]
  ): DynamicInserter = {
    new DynamicInserter(
      insertFn = (ctx, owner, _) => {
        var maybeTextNode: js.UndefOr[TextNode] = js.undefined
        textSource.foreach { newValue =>
          val newText = renderable.asString(newValue)
          maybeTextNode.fold {
            val newTextNode = new TextNode(newText)
            switchToChild(newTextNode, ctx)
            maybeTextNode = newTextNode
            ()
          } { textNode =>
            // Subsequent events: updating the textContent field of the existing TextNode (which is also the sentinel node).
            // #Note: we do not auto-distinct here because reading the current text value
            //  from the DOM takes more CPU time than setting it.
            textNode.ref.textContent = newText
          }
        }(owner)
      },
      hooks = ()
    )
  }

  def option[Component](
    textSource: Observable[Option[Component]],
    renderable: RenderableText[Component]
  ): DynamicInserter = {
    lazy val emptyNode = new CommentNode("")
    new DynamicInserter(
      insertFn = (ctx, owner, _) => {
        var maybeTextNode: js.UndefOr[TextNode] = js.undefined
        textSource.foreach { newValueOpt =>
          newValueOpt.fold {
            if (maybeTextNode.nonEmpty) {
              switchToChild(emptyNode, ctx)
              maybeTextNode = js.undefined
            }
          } { newValue =>
            val newText = renderable.asString(newValue)
            maybeTextNode.fold {
              val newTextNode = new TextNode(newText)
              switchToChild(newTextNode, ctx)
              maybeTextNode = newTextNode
              ()
            } { textNode =>
              // Subsequent events: updating the textContent field of the existing TextNode (which is also the sentinel node).
              // #Note: we do not auto-distinct here because reading the current text value
              //  from the DOM takes more CPU time than setting it.
              textNode.ref.textContent = newText
            }
          }
        }(owner)
      },
      hooks = ()
    )
  }

  // #Note: this is ALSO used in StaticTextInserter
  def switchToChild(
    newTextOrCommentNode: ChildNode.Base, // #TODO[Scala3] Should be TextNode | CommentNode ideally.
    ctx: InsertContext
  ): Unit = {
    // First event: inserting the child for the first time: replace sentinel comment node with new TextNode
    DomApi.replaceChild(
      parent = ctx.parentNode,
      oldChild = ctx.sentinelNode,
      newChild = newTextOrCommentNode,
      hooks = ()
    )

    ctx.sentinelNode = newTextOrCommentNode
    if (ctx.strictMode) {
      ctx.strictMode = false
      // We've just replaced the sentinel node with newTextNode,
      // so any remaining old child nodes must be directly under it.
      ctx.removeOldChildNodesFromDOM(after = newTextOrCommentNode)
      // In loose mode, the child content node replaces the sentinel, and is not tracked in "extraNodes".
      // This is different from strict mode where the sentinel node is independent, to allow for moving
      // of elements in between different inserters (otherwise Laminar would lose track of the reserved
      // spot in such cases).
      ctx.extraNodesMap.clear()
      // ctx.extraNodes = ChildrenSeq.empty
      ctx.extraNodeCount = 0
    }
  }
}
