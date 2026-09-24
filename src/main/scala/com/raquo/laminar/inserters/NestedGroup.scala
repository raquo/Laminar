package com.raquo.laminar.inserters

import com.raquo.airstream.ownership._
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.nodes.{CommentNode, ReactiveElement}
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.|

final class NestedGroup(
  sentinelNode: CommentNode,
  insertFn: (InsertContext, Owner) => Subscription,
)(
  initialParent: ReactiveElement.Base, // This group can be moved. See `nestedInsertContext.currentParentNode` for current value.
  initiallyPlaceAfterRefOpt: js.UndefOr[dom.Node], // initial sentinel node mounting point
  initiallyRequiresTrailingSentinel: Boolean, // true if rendering this group nested inside `children <--`
  initialSlotName: String | Unit
) {

  private[laminar] lazy val leadingSentinel: CommentNode = sentinelNode

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
    afterRefOpt = initiallyPlaceAfterRefOpt,
    slotName = initialSlotName
  )

  private val nestedInsertContext: InsertContext = {
    val ctx = InsertContext.unsafeMakeReservedSpotContext(
      sentinelNode = leadingSentinel,
      initialParentNode = initialParent,
      initialSlotName = initialSlotName
    )
    ctx
  }

  if (initiallyRequiresTrailingSentinel) {
    ensureTrailingSentinel()
  }

  // B. Create subscription to run this inserter's logic

  private val nestedSubscription: DynamicSubscription = {
    DynamicSubscription.unsafe(
      nestedDynamicOwner,
      activate = owner => insertFn(nestedInsertContext, owner)
    )
  }

  // C. Wire nesting group lifecycle to follow parent's lifecycle

  nestedPilotSubscription.setOwner(
    nestedInsertContext.currentParentNode.dynamicOwner
  )

  // --

  /** The last DOM node of this group's span.
    *  - As a `children <--` list item, its trailing sentinel – a stable end marker even when the
    *    content is empty.
    *  - As a lightweight static mount (no list-item trailing sentinel), the actual end of the
    *    current content (the trailing sentinel for `children <--`, else the single content node,
    *    else the sentinelNode, which IS our [[leadingSentinel]]).
    */
  private[laminar] def lastNode: dom.Node = nestedInsertContext.lastNodeInDom

  /** Call this if / when this group is rendered as a `children <--` list item. */
  private[laminar] def ensureTrailingSentinel(): Unit = {
    nestedInsertContext.forceTrailingSentinel()
  }

  /** Place this already-rendered group right after `afterRefOpt` in `newParent` (or append
    * it, if `afterRefOpt` is absent) WITHOUT re-mounting, mirroring how a plain element can
    * be moved – within its parent, or to a different parent.
    *
    * This is the single entry point for every re-placement of an existing group.
    *
    * Whatever the group's previous placement, it ends up in the shape and lifecycle order
    * that it would have had if it was created at its new place.
    *
    * @param newSlotName already resolved against the group's own slot (innermost wins)
    * @param insertAsListItem  true if the new place is a `children <--` list
    */
  private[laminar] def moveTo(
    newParent: ReactiveElement.Base,
    afterRefOpt: dom.Node | Unit,
    newSlotName: String | Unit,
    insertAsListItem: Boolean
  ): Unit = {
    if (insertAsListItem) {
      ensureTrailingSentinel()
    }
    if (newParent eq nestedInsertContext.currentParentNode) {
      moveWithinParent(afterRefOpt, newSlotName)
    } else {
      moveToParent(newParent, afterRefOpt, newSlotName)
    }
  }

  /** Reposition this group's span within its current parent. The Laminar parent (and so,
    * the dynamic owner) of the group and its content does not change, so this is a raw DOM
    * move: there is no lifecycle to update.
    */
  private def moveWithinParent(
    afterRefOpt: dom.Node | Unit,
    newSlotName: String | Unit
  ): Unit = {
    val parentRef = nestedInsertContext.currentParentNode.ref
    // Our leading sentinel is in this parent, so `lastChild` is never null.
    var reference = afterRefOpt.getOrElse(parentRef.lastChild)
    val lastRef = lastNode
    var node: dom.Node = leadingSentinel.ref
    var continue = true
    while (continue) {
      val nextNode = node.nextSibling // capture before `insertAfter` moves `node` away
      continue = node ne lastRef
      DomApi.raw.insertAfter(
        parent = parentRef,
        newChild = node,
        referenceChild = reference
      )
      reference = node
      node = nextNode
    }
    // Re-affirm slot, following last-write-wins principle, same as `moveToParent`.
    applySlot(newSlotName)
  }

  /** Move this whole group (its content AND its lifecycle ownership) to a new parent,
    * seamlessly transferring its contents and subscriptions.
    */
  private def moveToParent(
    newParent: ReactiveElement.Base,
    afterRefOpt: dom.Node | Unit,
    newSlotName: String | Unit
  ): Unit = {

    // Transfer lifecycle ownership BEFORE moving the content:
    //  - Within the new parent's dynamic owner, this group's pilot subscription must precede
    //    those of its content (as it does when the group is created there), so that
    //    on activation, the inner inserter updates its content before that content mounts.
    //  - If this activates the group, the inner inserter updates its content while it's still
    //    in the old (inactive) parent, so no stale content ever mounts in the new parent.
    //  - If this deactivates the group, the inner inserter stops before its content unmounts,
    //    mirroring `removeFromParent`.
    // Live transfers (active to active) are seamless, without re-mounting.
    nestedPilotSubscription.setOwner(newParent.dynamicOwner)

    // Compile a list of inserters matching the actual nodes in the DOM.
    // We want to move actual de-facto DOM content, without re-stealing anything.
    // #Note: this must be done AFTER the pilot transfer above, which can update the content.
    val contentInserters = nestedInsertContext.currentContentInsertersFromDom

    // Move the leading and trailing sentinels to the new place.
    insertOrAppendChild(
      parent = newParent,
      newChild = leadingSentinel,
      afterRefOpt = afterRefOpt,
      slotName = newSlotName
    )

    nestedInsertContext.trailingSentinelNodeOpt.foreach { trailingSentinel =>
      DomApi.insertChildAfter(
        parent = newParent,
        newChild = trailingSentinel,
        referenceChildRef = leadingSentinel.ref,
        slotName = newSlotName
      )
    }

    // Move content nodes (without unnecessary re-mounting)
    var lastRef: dom.Node = leadingSentinel.ref
    contentInserters.forEach { inserter =>
      // Note: this calls `moveTo` internally if this nested inserter is dynamic.
      inserter.addToDynamicList(newParent, afterRef = lastRef, newSlotName)
      lastRef = inserter.lastNode
    }

    // Commit the group's new parent and slot on the context. This is what redirects the
    // inner inserter's future emissions (which read `currentParentNode` and `currentSlotName`)
    // to the new destination.
    nestedInsertContext.setCurrentParentNode(newParent)
    nestedInsertContext.setCurrentSlotName(newSlotName)
  }

  private[laminar] def applySlot(newSlotName: String | Unit): Unit = {
    val parent = nestedInsertContext.currentParentNode
    // Only the actual de-facto DOM content: nodes stolen out of our span are not ours to slot.
    nestedInsertContext.currentContentInsertersFromDom.forEach { inserter =>
      inserter.applySlot(parent, newSlotName)
    }
    nestedInsertContext.setCurrentSlotName(newSlotName)
  }

  /** @param keepItem Content items to leave in the parent's DOM – see
    *                 [[InsertContext.removeContentMapNodesFromDom]].
    */
  private[laminar] def removeFromParent(keepItem: dom.Node => Boolean): Unit = {
    // #Note: order of operations mirrors that in willSetParent(None) of ReactiveElement
    //  - first, disown the subscription, then, update the DOM.

    // D. Disconnect subscription first (kills the inner inserter's subscription if active).
    nestedPilotSubscription.clearOwner()
    nestedSubscription.kill()

    // Then remove the inner inserter's content from the DOM (whatever shape it took –
    // a `children <--` list, a `children.command` span, or a plain `child <--` / `text <--`
    // node), tearing down any per-item lifecycle. The context knows its own content type,
    // so this one call picks the right teardown strategy.
    // Kept items are left in the parent, exactly where this group's span was.
    nestedInsertContext.clearPreviousInserterContent(
      replaceContentMapWithSingleNode = js.undefined,
      nextInserterType = js.undefined,
      keepItem = keepItem
    )

    // A. Remove the sentinel nodes from the parent DOM. Use the CURRENT parent
    //    (the group may have been moved to a different list since it was created).
    val currentParent = nestedInsertContext.currentParentNode
    DomApi.removeChild(parent = currentParent, child = nestedInsertContext.sentinelNode)
    nestedInsertContext.trailingSentinelNodeOpt.foreach { trailingSentinel =>
      DomApi.removeChild(parent = currentParent, child = trailingSentinel)
    }
  }

  private def insertOrAppendChild(
    parent: ReactiveElement.Base,
    newChild: CommentNode,
    afterRefOpt: dom.Node | Unit,
    slotName: String | Unit
  ): Unit = {
    afterRefOpt.fold(
      ifEmpty = {
        DomApi.appendChild(
          parent = parent,
          child = newChild,
          slotName = slotName
        )
      }
    ) { afterRef =>
      DomApi.insertChildAfter(
        parent = parent,
        newChild = newChild,
        referenceChildRef = afterRef,
        slotName = slotName
      )
    }
  }
}
