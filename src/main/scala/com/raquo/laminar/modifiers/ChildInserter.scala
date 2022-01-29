package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.lifecycle.{InsertContext, MountContext}
import com.raquo.laminar.nodes.{ChildNode, ParentNode, ReactiveElement}

import scala.scalajs.js

object ChildInserter {

  def apply[El <: ReactiveElement.Base] (
    $child: MountContext[El] => Observable[ChildNode.Base]
  ): Inserter[El] = new Inserter[El](
    insertFn = (ctx, owner) => {
      val mountContext = new MountContext[El](
        thisNode = ctx.parentNode,
        owner = owner
      )
      var lastSeenChild: js.UndefOr[ChildNode.Base] = js.undefined
      $child(mountContext).foreach { newChildNode =>
        if (!lastSeenChild.exists(_ eq newChildNode)) { // #Note: auto-distinction
          lastSeenChild = newChildNode
          ParentNode.replaceChild(parent = ctx.parentNode, oldChild = ctx.sentinelNode, newChild = newChildNode)
          ctx.sentinelNode = newChildNode
        }
      }(owner)
    }
  )
}
