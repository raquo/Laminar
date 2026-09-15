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
            if (maybeTextNode.nonEmpty) {
              ChildInserter.switchToChild(
                maybeLastSeenChild = maybeTextNode,
                newChildNodeOpt = (),
                ctx = ctx,
                slotName = () // text nodes are never slotted
              )
              maybeTextNode = js.undefined
            }
          } { newValue =>
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
              // Subsequent events: update the textContent of the existing TextNode in-place
              // (the text node sits right after the sentinel as a content node).
              // #Note: we do not auto-distinct here because reading the current text value
              //  from the DOM takes more CPU time than setting it.
              textNode.ref.textContent = newText
            }
          }
        }(using owner)
      },
      slotName = () // text nodes are never slotted
    )
  }
}
