package com.raquo.laminar.inserters

import com.raquo.airstream.ownership._
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.nodes.{CommentNode, ReactiveElement}
import org.scalajs.dom

import scala.scalajs.js

final class NestedGroup(
  // #Note: this is the INITIAL parent, used only to set up the span. The current parent (which
  //  changes if this group is moved between parents, see [[moveToParent]]) is owned by
  //  [[nestedInsertContext]] – read it via `nestedInsertContext.currentParentNode`.
  initialParent: ReactiveElement.Base,
  afterRefOpt: js.UndefOr[dom.Node],
  sentinelNode: CommentNode,
  insertFn: (InsertContext, Owner, js.UndefOr[InserterHooks]) => Subscription,
  hooks: js.UndefOr[InserterHooks],
  withTrailingSentinel: Boolean // true is required if rendering this nested inside `children <--`
) {

  private[laminar] lazy val leadingSentinel: CommentNode = sentinelNode

  /** The group's own trailing sentinel that brackets the END of its span. Present only while this
    * group lives (or has lived) as a `children <--` list item – see [[ensureTrailingSentinel]].
    * A group applied only to plain elements never has one, keeping its DOM minimal.
    */
  private var trailingSentinelOpt: js.UndefOr[CommentNode] = js.undefined

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

  insertOrAppendChild(
    parent = initialParent,
    newChild = leadingSentinel,
    afterRefOpt = afterRefOpt
  )

  private val nestedInsertContext: InsertContext = {
    InsertContext.unsafeMakeReservedSpotContext(
      initialParentNode = initialParent,
      sentinelNode = leadingSentinel
    )
  }

  if (withTrailingSentinel) {
    ensureTrailingSentinel()
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

  /** The last DOM node of this group's span.
    *  - As a list item (has a trailing sentinel), that sentinel – a stable end marker even when
    *    the content is empty.
    *  - As a lightweight static mount, the actual end of the current content (the inner trailing
    *    sentinel for `children <--`, else the last content node, else the leading sentinel).
    */
  private[laminar] def lastNode: dom.Node = {
    // The group's own trailing sentinel (an outer bracket, present only as a list item) is the end
    // of the whole span. Without it (a lightweight static mount), the end is the content's own end –
    // which the context knows. Note: the context's sentinelNode IS our [[leadingSentinel]], so its
    // empty-content fallback coincides with ours.
    trailingSentinelOpt.map(_.ref).getOrElse(nestedInsertContext.lastNode)
  }

  /** Make sure this group has a trailing sentinel bracketing the end of its span, creating and
    * inserting it if needed. Called when a lightweight (static) group is added to a `children <--`
    * list and thus needs a stable end marker. No-op if the group already has one.
    */
  private[laminar] def ensureTrailingSentinel(): Unit = {
    if (trailingSentinelOpt.isEmpty) {
      val trailingSentinel = new CommentNode("")
      DomApi.insertChildAfter(
        parent = nestedInsertContext.currentParentNode,
        newChild = trailingSentinel,
        referenceChildRef = nestedInsertContext.lastNode,
        hooks = hooks
      )
      trailingSentinelOpt = trailingSentinel
    }
  }

  /** Move this whole group (its content AND its lifecycle ownership) to a new parent,
    * right after `afterRefOpt` (or appended at the end of `newParent` if `afterRefOpt`
    * is absent) WITHOUT re-mounting.
    *
    * Seamlessly transfers its contents and subscriptions to the new parent, mirroring
    * how a plain element can be moved between two parents.
    *
    * Called when the SAME dynamic inserter instance, already placed as a group,
    * is placed somewhere else:
    *  - added to another `children <--` list (see [[DynamicInserter.addToDynamicList]]), or
    *  - applied to a plain element, e.g. `element.amend(inserter)` (see [[DynamicInserter.apply]]).
    */
  private[laminar] def moveToParent(
    newParent: ReactiveElement.Base,
    afterRefOpt: js.UndefOr[dom.Node]
  ): Unit = {

    // Move sentinels to the new place.
    insertOrAppendChild(
      parent = newParent,
      newChild = leadingSentinel,
      afterRefOpt = afterRefOpt
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

    trailingSentinelOpt.foreach { trailingSentinel =>
      val lastMovedSentinelRef =
        nestedInsertContext
          .trailingSentinelNodeOpt
          .getOrElse(leadingSentinel)
          .ref
      DomApi.insertChildAfter(
        parent = newParent,
        newChild = trailingSentinel,
        referenceChildRef = lastMovedSentinelRef,
        hooks = hooks
      )
    }

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
    trailingSentinelOpt.foreach { trailingSentinel =>
      DomApi.removeChild(parent = currentParent, child = trailingSentinel)
    }
  }

  private def insertOrAppendChild(
    parent: ReactiveElement.Base,
    newChild: CommentNode,
    afterRefOpt: js.UndefOr[dom.Node]
  ): Unit = {
    afterRefOpt.fold(
      ifEmpty = {
        DomApi.appendChild(
          parent = parent,
          child = newChild,
          hooks = hooks
        )
      }
    ) { afterRef =>
      DomApi.insertChildAfter(
        parent = parent,
        newChild = newChild,
        referenceChildRef = afterRef,
        hooks = hooks
      )
    }
  }
}
