package com.raquo.laminar.inserters

import com.raquo.airstream.ownership.{DynamicOwner, DynamicSubscription, Owner, Subscription, TransferableSubscription}
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.nodes.{CommentNode, ReactiveElement}
import org.scalajs.dom

import scala.scalajs.js
import scala.util.chaining.scalaUtilChainingOps

final class NestedGroup(
  parent: ReactiveElement.Base,
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
    parent = parent,
    newChild = leadingSentinel,
    referenceChildRef = afterRef,
    hooks = hooks
  )

  DomApi.insertChildAfter(
    parent = parent,
    newChild = trailingSentinel,
    referenceChildRef = leadingSentinel.ref,
    hooks = hooks
  )

  private val nestedInsertContext: InsertContext = {
    InsertContext.unsafeMakeReservedSpotContext(
      parentNode = parent,
      sentinelNode = leadingSentinel
    )//.tap(_.trailingSentinelNode = trailingSentinel)
  }

  // B. Create subscription to run this inserter's logic

  private val nestedSubscription: DynamicSubscription = {
    DynamicSubscription.unsafe(
      nestedDynamicOwner,
      activate = owner => insertFn(nestedInsertContext, owner, hooks)
    )
  }

  // C. Wire nesting group lifecycle to follow parent's lifecycle

  nestedPilotSubscription.setOwner(parent.dynamicOwner)

  // --

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

    // A. Remove the sentinel nodes from the parent DOM
    DomApi.removeChild(parent = parent, child = nestedInsertContext.sentinelNode)
    DomApi.removeChild(parent = parent, child = trailingSentinel)
  }
}
