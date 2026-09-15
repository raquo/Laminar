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
  private[laminar] def lastNode: dom.Node = nestedInsertContext.lastNode

  /** Call this if / when this group is rendered as a `children <--` list item. */
  private[laminar] def ensureTrailingSentinel(): Unit = {
    nestedInsertContext.forceTrailingSentinel()
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
    afterRefOpt: dom.Node | Unit,
    newSlotName: String | Unit
  ): Unit = {

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
    nestedInsertContext.contentMap.forEach { (inserter, _) =>
      // Note: this calls `moveToParent` internally if this nested inserter is dynamic.
      inserter.addToDynamicList(newParent, afterRef = lastRef, newSlotName)
      lastRef = inserter.lastNode
    }

    // Commit the group's new parent and slot on the context. This is what redirects the
    // inner inserter's future emissions (which read `currentParentNode` and `currentSlotName`)
    // to the new destination.
    nestedInsertContext.setCurrentParentNode(newParent)
    nestedInsertContext.setCurrentSlotName(newSlotName)

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
