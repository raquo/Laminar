package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.lifecycle.MountContext
import com.raquo.laminar.nodes.{ParentNode, ReactiveElement, TextNode}

import scala.scalajs.js

object TextInserter {

  def apply[T] (
    $text: MountContext[ReactiveElement.Base] => Observable[T],
    render: T => TextNode
  ): Inserter[ReactiveElement.Base] = new Inserter[ReactiveElement.Base](
    insertFn = (ctx, owner) => {
      val mountContext = new MountContext[ReactiveElement.Base](
        thisNode = ctx.parentNode,
        owner = owner
      )
      var lastSeenText: js.UndefOr[T] = js.undefined
      $text(mountContext).foreach { newText =>
        if (!lastSeenText.contains(newText)) { // #Note: auto-distinction
          lastSeenText = newText
          val newChildNode = render(newText)
          ParentNode.replaceChild(parent = ctx.parentNode, oldChild = ctx.sentinelNode, newChild = newChildNode)
          ctx.sentinelNode = newChildNode
        }
      }(owner)
    }
  )
}
