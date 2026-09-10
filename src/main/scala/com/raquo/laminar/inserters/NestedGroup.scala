package com.raquo.laminar.inserters

import com.raquo.airstream.ownership._
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.nodes.{CommentNode, ReactiveElement}
import org.scalajs.dom

import scala.scalajs.js

final class NestedGroup(
  // #Note: this is the INITIAL parent, used only to set up the span. The current parent (which
  //  changes if this group is moved between `children <--` lists, see [[moveToParent]]) is owned
  //  by [[nestedInsertContext]] – read it via `nestedInsertContext.currentParentNode`.
  initialParent: ReactiveElement.Base,
  afterRef: dom.Node,
  sentinelNode: CommentNode,
  insertFn: (InsertContext, Owner, js.UndefOr[InserterHooks]) => Subscription,
  hooks: js.UndefOr[InserterHooks]
) {

  private[laminar] lazy val leadingSentinel: CommentNode = {
    // new CommentNode("")
    sentinelNode
  }

  private[laminar] lazy val trailingSentinel: CommentNode = {
    new CommentNode("")
  }

  private lazy val nestedDynamicOwner = new DynamicOwner(() => {
    throw new Exception(
      "Attempting to use owner of an unmounted nested inserter in `children <--`."
    )
  })

  /** This subscription activates and deactivates this inserter's
    * subscriptions when mounting and unmounting this element.
    */
  private lazy val nestedPilotSubscription = new TransferableSubscription(
    activate = () => nestedDynamicOwner.activate(),
    deactivate = () => nestedDynamicOwner.deactivate()
  )

  // -- Init sequence --

  // #Note: order of operations mirrors that in setParent(Some) of ReactiveElement
  //  - first, prepare everything, then own the subscription at the end

  // A. Reserve insertion spot

  DomApi.insertChildAfter(
    parent = initialParent,
    newChild = leadingSentinel,
    referenceChildRef = afterRef,
    hooks = hooks
  )

  DomApi.insertChildAfter(
    parent = initialParent,
    newChild = trailingSentinel,
    referenceChildRef = leadingSentinel.ref,
    hooks = hooks
  )

  private val nestedInsertContext: InsertContext = {
    InsertContext.unsafeMakeReservedSpotContext(
      initialParentNode = initialParent,
      sentinelNode = leadingSentinel
    )
  }

  // B. Create subscription to run this inserter's logic

  private val nestedSubscription: DynamicSubscription = {
    DynamicSubscription.unsafe(
      nestedDynamicOwner,
      activate = owner => insertFn(nestedInsertContext, owner, hooks)
    )
  }

  // C. Wire nesting group lifecycle to follow parent's lifecycle

  nestedPilotSubscription.setOwner(
    nestedInsertContext.currentParentNode.dynamicOwner
  )

  // --

  /** Move this whole group (its content AND its lifecycle ownership)
    * to a new parent, right after `afterRef`, WITHOUT re-mounting.
    *
    * Seamlessly transfer its contents and subscriptions to the new parent,
    * mirroring how a plain element can be moved between two `children <--` lists.
    *
    * Called when the SAME nested inserter instance, already present in a
    * `children <--` list, is added to a different `children <--` list before
    * it is removed from the first (see [[DynamicInserter.addToDynamicList]]).
    */
  private[laminar] def moveToParent(
    newParent: ReactiveElement.Base,
    afterRef: dom.Node
  ): Unit = {

    // Move sentinels to the new place
    DomApi.insertChildAfter(
      parent = newParent,
      newChild = leadingSentinel,
      referenceChildRef = afterRef,
      hooks = hooks
    )

    nestedInsertContext.trailingSentinelNodeOpt.foreach { innerTrailingSentinel =>
      // A multi-node inner inserter (e.g. a nested `children <--`) keeps its own trailing
      // sentinel between its content and this group's trailing sentinel; move it too.
      DomApi.insertChildAfter(
        parent = newParent,
        newChild = innerTrailingSentinel,
        referenceChildRef = leadingSentinel.ref,
        hooks = hooks
      )
    }

    val lastSentinelRef =
      nestedInsertContext
        .trailingSentinelNodeOpt
        .getOrElse(leadingSentinel)
        .ref

    DomApi.insertChildAfter(
      parent = newParent,
      newChild = trailingSentinel,
      referenceChildRef = lastSentinelRef,
      hooks = hooks
    )

    // Move content nodes (without unnecessary re-mounting)
    var lastRef: dom.Node = leadingSentinel.ref
    nestedInsertContext.contentMap.forEach { (inserter, _) =>
      // Note: this calls `moveToParent` internally if this nested inserter is dynamic.
      inserter.addToDynamicList(newParent, afterRef = lastRef, hooks)
      lastRef = inserter.lastNode
    }

    // Commit the group's new parent on the context. This is what redirects the inner
    // inserter's future emissions (which read `currentParentNode`) to `newParent`.
    nestedInsertContext.setCurrentParentNode(newParent)

    // Transfer the subscription to the new parent's owner.
    // This is seamless, without unnecessary re-mounting.
    nestedPilotSubscription.setOwner(newParent.dynamicOwner)
  }

  def removeFromParent(): Unit = {
    // #Note: order of operations mirrors that in willSetParent(None) of ReactiveElement
    //  - first, disown the subscription, then, update the DOM.

    // D. Disconnect subscription first (kills the inner inserter's subscription if active).
    nestedPilotSubscription.clearOwner()
    nestedSubscription.kill()

    // Then remove the inner inserter's content from the DOM (whatever shape it took –
    // a `children <--` list, a `children.command` span, or a plain `child <--` / `text <--`
    // node), tearing down any per-item lifecycle. The context knows its own content type,
    // so this one call picks the right teardown strategy.
    nestedInsertContext.clearPreviousInserterContent(
      replaceContentMapWithSingleNode = js.undefined,
      nextInserterType = js.undefined
    )

    // A. Remove the sentinel nodes from the parent DOM. Use the CURRENT parent (the group may
    //    have been moved to a different list since it was created).
    val currentParent = nestedInsertContext.currentParentNode
    DomApi.removeChild(parent = currentParent, child = nestedInsertContext.sentinelNode)
    DomApi.removeChild(parent = currentParent, child = trailingSentinel)
  }
}
