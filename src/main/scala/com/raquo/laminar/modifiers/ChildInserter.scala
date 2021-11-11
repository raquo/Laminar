package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.lifecycle.{InsertContext, MountContext}
import com.raquo.laminar.nodes.{ChildNode, ParentNode, ReactiveElement}

// @TODO[Naming] hrm
object ChildInserter {

  def apply[El <: ReactiveElement.Base] (
    $child: MountContext[El] => Observable[ChildNode.Base],
    initialInsertContext: Option[InsertContext[El]] // #TODO[API] This param appears to always be `None`. Didn't I make this for some edge case requiring a Some()? I think tha logic is in `Inserter`
  ): Inserter[El] = new Inserter[El](
    initialInsertContext,
    insertFn = (c, owner) => {
      val mountContext = new MountContext[El](
        thisNode = c.parentNode,
        owner = owner
      )
      $child(mountContext).foreach { newChildNode =>
        ParentNode.replaceChild(parent = c.parentNode, oldChild = c.sentinelNode, newChild = newChildNode)
        c.sentinelNode = newChildNode
      }(owner)
    }
  )
}
