package com.raquo.laminar.inserters

import com.raquo.airstream.core.Observable
import com.raquo.laminar.modifiers.RenderableText
import com.raquo.laminar.nodes.TextNode

import scala.scalajs.js

object ChildTextInserter {

  def apply[Component](
    textSource: Observable[Component],
    renderable: RenderableText[Component]
  ): DynamicInserter = {
    new DynamicInserter(
      insertFn = (ctx, owner) => {
        var maybeTextNode: js.UndefOr[TextNode] = js.undefined
        textSource.foreach { newValue =>
          val newText = renderable.asString(newValue)
          maybeTextNode.fold {
            val newTextNode = new TextNode(newText)
            ChildInserter.switchToChild(
              maybeLastSeenChild = (),
              newChildNodeOpt = newTextNode,
              ctx = ctx,
              slotName = () // text nodes are never slotted
            )
            maybeTextNode = newTextNode
            ()
          } { textNode =>
            // Subsequent events: update the textContent of the existing TextNode in place
            // (the text node sits right after the sentinel as a content node).
            // #Note: we do not auto-distinct here because reading the current text value
            //  from the DOM takes more CPU time than setting it.
            textNode.ref.textContent = newText
          }
        }(using owner)
      },
      slotName = ()
    )
  }

  def option[Component](
    textSource: Observable[Option[Component]],
    renderable: RenderableText[Component]
  ): DynamicInserter = {
    new DynamicInserter(
      insertFn = (ctx, owner) => {
        var maybeTextNode: js.UndefOr[TextNode] = js.undefined
        textSource.foreach { newValueOpt =>
          newValueOpt.fold {
            // Note: `maybeTextNode` is reset on re-mount, so we can't rely entirely on that:
            // `ctx.contentMap` on the other hand is reliable as long as nothing else steals
            // the TextNode that we create here. For now this is a safe assumption because we
            // don't expose the internally created TextNode, but the safest approach is to
            // OR the two conditions, so that is what we do.
            if (maybeTextNode.nonEmpty || ctx.contentMap.size > 0) {
              // Switching from something to None – clear content
              ChildInserter.switchToChild(
                maybeLastSeenChild = maybeTextNode,
                newChildNodeOpt = (),
                ctx = ctx,
                slotName = () // text nodes are never slotted
              )
              maybeTextNode = js.undefined
            } else {
              // Switching from None to None – no action needed
            }
          } { newValue =>
            // Switching to Some(textLike)
            val newText = renderable.asString(newValue)
            maybeTextNode.fold {
              // Switching from None to Some(textLike) – create the text node
              val newTextNode = new TextNode(newText)
              ChildInserter.switchToChild(
                maybeLastSeenChild = (),
                newChildNodeOpt = newTextNode,
                ctx = ctx,
                slotName = () // text nodes are never slotted
              )
              maybeTextNode = newTextNode
              ()
            } { textNode =>
              // Switching from Some(textLike1) to Some(textLike2) –
              // update the textContent of the existing TextNode in-place.
              // #Note: we do not auto-distinct here because reading the current text value
              //  from the DOM takes more CPU time than setting it.
              // #Note: unlike ChildInserter.switchToChild we don't re-validate that this
              //  node is still under the sentinel before writing – that DOM read isn't
              //  worth it on this hot path. Safe under the same "nothing steals our
              //  un-exposed TextNode" assumption as the None branch above; this branch
              //  is unreachable right after a (re)mount (maybeTextNode is empty).
              textNode.ref.textContent = newText
            }
          }
        }(using owner)
      },
      slotName = () // text nodes are never slotted
    )
  }
}
