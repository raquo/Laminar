package com.raquo.laminar.inserters

import com.raquo.airstream.ownership.{DynamicSubscription, Owner, Subscription}
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.Modifier
import com.raquo.laminar.nodes.{ChildNode, CommentNode, ReactiveElement}
import org.scalajs.dom

import scala.scalajs.js

/** Inserter is a class that can insert child nodes into [[InsertContext]].
  *
  * This is needed in `onMountInsert`, or when rendering dynamic children
  * with `child <-- ...`, `children <-- ...`, etc.
  *
  * If you don't have a [[InsertContext]], you can render the Inserter
  * just by calling its `apply` method. It will work the same way as
  * rendering any other child node, static or dynamic, would.
  *
  * So, you can use [[Inserter]] essentially as a supertype of regular Laminar
  * nodes and dynamic inserters like `children <-- ...`. We use it this way in
  * `onMountInsert`, for example.
  */
sealed trait Inserter extends Modifier[ReactiveElement.Base] {

  /** This is the inserter equivalent of calling `setParent(Some(parent))` on each of
    * its nodes: This inserter's nodes are added to parent, their subscriptions
    * are set up, etc.
    *
    * Should be paired with [[removeFromDynamicList]].
    *
    * @param afterRef the raw DOM node under `parent` after which this inserter's nodes
    *                 should be inserted.
    */
  private[laminar] def addToDynamicList(
    parent: ReactiveElement.Base,
    afterRef: dom.Node,
    hooks: js.UndefOr[InserterHooks]
  ): Unit

  /** This is the inserter equivalent of calling `setParent(None)` for each of its nodes:
    * This inserter's nodes are removed from the parent, their subscriptions cleaned up,
    * etc.
    *
    * Pre-requisite: you must have called [[addToDynamicList]]
    *                with the same parent before calling this.
    */
  private[laminar] def removeFromDynamicList(parent: ReactiveElement.Base): Unit

  /** No mount / re-mount, just a lateral move.
    *
    * Note: Note: [[DynamicInserter]] overrides this with a special implementation.
    *
    * Pre-requisite: you must have called [[addToDynamicList]]
    *                with the same parent before callng this.
    */
  private[laminar] def moveWithinDynamicList(
    parent: ReactiveElement.Base,
    afterRef: dom.Node
  ): Unit = {
    addToDynamicList(parent = parent, afterRef = afterRef, hooks = js.undefined)
  }

  /** The first DOM node of this item's span (valid after [[addToDynamicList]]).
    *  - Must always return the same node for the same inserter. Don't change it to `def`!
    *  - For single static nodes (e.g. [[ChildNode]]), this is the node itself.
    *  - For dynamic inserters, it's the leading sentinel.
    */
  private[laminar] val stableFirstNode: dom.Node

  /** The last DOM node of this item's span.
    *
    * For some inserters, it's the trailing sentinel node.
    * It can be `eq` to [[stableFirstNode]], e.g. for single static node inserters.
    */
  private[laminar] def lastNode: dom.Node
}

trait Hookable[+Self <: Inserter] { this: Inserter =>

  /** Create a copy of the inserter that will apply these
    * additional hooks after the original inserter's hooks.
    */
  def withHooks(addHooks: InserterHooks): Self with Hookable[Self]
}

trait StaticInserter extends Inserter {

  def renderInContext(ctx: InsertContext): Unit
}

// @TODO[API] Inserter really wants to extend Binder. And yet.

/** Inserter is a modifier that lets you insert child node(s) on mount.
  * When used with onMountInsert, it "immediately" reserves an insertion
  * spot and then on every mount it inserts the node(s) into the same spot.
  *
  * Note: As a Modifier this is not idempotent, but overall
  * it behaves as you would expect. See docs for more details.
  *
  * Note: If you DO provide initialContext, its parentNode MUST always
  * be the same `element` that you apply this Modifier to.
  *
  * @param insertFn Called every time this inserter is mounted.
  */
class DynamicInserter(
  insertFn: (InsertContext, Owner, js.UndefOr[InserterHooks]) => Subscription,
  hooks: js.UndefOr[InserterHooks] = js.undefined
) extends Inserter with Hookable[DynamicInserter] {

  def bind(element: ReactiveElement.Base): DynamicSubscription = {
    // #Note we want to remember this context even after subscription is deactivated.
    //  Yes, we expect the subscription to re-activate with this initial state
    //  because it would match the state of the DOM upon reactivation
    //  (unless some of the managed child elements were externally removed from the DOM,
    //  which Laminar should be able to recover from).
    val insertContext = InsertContext.reserveSpotContext(
      parentNode = element,
      hooks = hooks
    )

    ReactiveElement.bindSubscriptionUnsafe(element) { mountContext =>
      insertFn(insertContext, mountContext.owner, hooks)
    }
  }

  /** Owner typically comes from MountContext of the InsertContext parentNode. */
  def subscribe(
    insertContext: InsertContext,
    owner: Owner
  ): Subscription = {
    insertFn(insertContext, owner, hooks)
  }

  override def apply(element: ReactiveElement.Base): Unit = {
    bind(element)
  }

  override def withHooks(addHooks: InserterHooks): DynamicInserter = {
    val newHooks = addHooks.appendTo(hooks)
    new DynamicInserter(insertFn, newHooks)
  }

  // -- Nested groups support --

  private var nestedGroupOpt: js.UndefOr[NestedGroup] = js.undefined

  private val sentinelNode: CommentNode = new CommentNode("")

  override private[laminar] val stableFirstNode: dom.Node = sentinelNode.ref

  override private[laminar] def lastNode: dom.Node = {
    nestedGroupOpt
      .map(_.trailingSentinel.ref)
      .getOrElse(throw new Exception("Can not get lastNode: nested group not found. This is a bug in Laminar."))
  }

  override private[laminar] def addToDynamicList(
    parent: ReactiveElement.Base,
    afterRef: dom.Node,
    hooks: js.UndefOr[InserterHooks]
  ): Unit = {
    if (nestedGroupOpt.nonEmpty) {
      // #TODO[nested-dyn] handle the case when the same inserter instance is moved from one dynamic list to another
      //  - in that case, we should transfer the subscription and move nodes to the new parent without re-mounting,
      //    similarly to how we can transfer elements
      throw new Exception("Called addToDynamicList where nestedGroupOpt already exists.")
    }
    nestedGroupOpt = new NestedGroup(
      parent = parent, afterRef = afterRef, sentinelNode, insertFn, hooks
    )
  }

  override private[laminar] def removeFromDynamicList(parent: ReactiveElement.Base): Unit = {
    nestedGroupOpt
      .getOrElse(
        throw new Exception("Can not removeFromDynamicList: nested group not found (addToDynamicList was not called first). This is a bug in Laminar.")
      )
      .removeFromParent()
    nestedGroupOpt = js.undefined
  }

  /** Note: overrides default implementation */
  override private[laminar] def moveWithinDynamicList(
    parent: ReactiveElement.Base,
    afterRef: dom.Node
  ): Unit = {
    val lastRef = lastNode // trailing sentinel
    var node = stableFirstNode // leading sentinel
    var reference = afterRef
    var continue = true
    while (continue) {
      val nextNode = node.nextSibling // capture before `insertAfter` moves `node` away
      continue = node ne lastRef
      // #Note: this calls `raw`, thus bypasses willSetParent / setParent / hooks.
      //  - This is fine because it's a lateral move within the same parent, so nothing needs to happen anyway.
      DomApi.raw.insertAfter(
        parent = parent.ref,
        newChild = node,
        referenceChild = reference
      )
      reference = node
      node = nextNode
    }
  }
}
