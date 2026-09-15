package com.raquo.laminar.inserters

import com.raquo.airstream.ownership.{Owner, Subscription}
import com.raquo.ew
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
    listHooks: js.UndefOr[InserterHooks]
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
    addToDynamicList(
      parent = parent,
      afterRef = afterRef,
      listHooks = ()
    )
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
  insertFn: (InsertContext, Owner) => Subscription,
  hooks: js.UndefOr[InserterHooks] = js.undefined
) extends Inserter with Hookable[DynamicInserter] {

  /** Owner typically comes from MountContext of the InsertContext parentNode.
    *
    * #Warning: make sure to cancel the returned subscription when it's time.
    */
  def renderIntoSharedContext(
    insertContext: InsertContext,
    owner: Owner
  ): Subscription = {
    insertContext.setCurrentHooks(hooks) // prepare the shared context for this inserter
    insertFn(insertContext, owner)
  }

  /** Because [[DynamicInserter]]-s can use [[NestedGroup]]-s
    * when they're rendered as items in a `children <--` list,
    * we need to also use [[NestedGroup]]-s when rendering them
    * plainly, to keep a consistent rendering path that manages
    * state and subscriptions in one place. This ensures that
    * these inserters remain moveable between arbitrary contexts,
    * specifically between nested `children <--` contexts and
    * plain child contexts.
    */
  override def apply(element: ReactiveElement.Base): Unit = {
    // Append to the end of the element.
    val afterRefOpt: js.UndefOr[dom.Node] = {
      val lastChild = element.ref.lastChild
      ew.Null.asUndefined(lastChild)
    }
    nestedGroupOpt.fold(
      ifEmpty = {
        // First placement of this inserter
        nestedGroupOpt = new NestedGroup(
          sentinelNode = sentinelNode,
          insertFn = insertFn
        )(
          initialParent = element,
          initiallyPlaceAfterRefOpt = afterRefOpt,
          initiallyRequiresTrailingSentinel = false, // trailing sentinel is not needed until/unless we move this inserter into `children <--` – call .forceTrailingSentinel() then.
          initialHooks = hooks // hooks as-is, because plain `element` parent does not add any hooks
        )
      }
    ) { group =>
      // This inserter instance already lives somewhere (applied to another element, or as a
      // `children <--` list item). Applying it here (e.g. `element.amend(inserter)`) MOVES it
      // seamlessly (no re-mounting).
      group.moveToParent(
        newParent = element,
        afterRefOpt = afterRefOpt,
        newCombinedHooks = hooks // hooks as-is, because plain `element` parent does not add any hooks
      )
    }
  }

  override def withHooks(addHooks: InserterHooks): DynamicInserter = {
    val newHooks = InserterHooks.concat(hooks, addHooks)
    new DynamicInserter(insertFn, newHooks)
  }

  // -- Nested groups support --

  private var nestedGroupOpt: js.UndefOr[NestedGroup] = js.undefined

  private val sentinelNode: CommentNode = new CommentNode("")

  override private[laminar] val stableFirstNode: dom.Node = sentinelNode.ref

  override private[laminar] def lastNode: dom.Node = {
    nestedGroupOpt
      .map(_.lastNode)
      .getOrElse(throw new Exception("Can not get lastNode: nested group not found. This is a bug in Laminar."))
  }

  override private[laminar] def addToDynamicList(
    parent: ReactiveElement.Base,
    afterRef: dom.Node,
    listHooks: js.UndefOr[InserterHooks]
  ): Unit = {
    val combinedHooks = InserterHooks.concat(listHooks, hooks)
    nestedGroupOpt.fold(
      ifEmpty = {
        // First placement of this inserter
        nestedGroupOpt = new NestedGroup(
          sentinelNode = sentinelNode,
          insertFn = insertFn
        )(
          initialParent = parent,
          initiallyPlaceAfterRefOpt = afterRef,
          initiallyRequiresTrailingSentinel = true, // required for nested inserters
          initialHooks = combinedHooks
        )
      }
    ) { group =>
      // This inserter instance already lives as a group somewhere else.
      // Add trailing sentinel for proper tracking inside `children <--`,
      // then move it seamlessly to its new location.
      // Note: If / when the previous dynamic list that hosted this inserter
      //       decides to remove this inserter, it will call
      //       `thisInserter.removeFromDynamicList(oldInserterParent)`
      //       (see below), which will be a no-op due to parent mismatch,
      //       so all is good – this new list manages this inserter now.
      group.ensureTrailingSentinel()
      group.moveToParent(
        newParent = parent,
        afterRefOpt = afterRef,
        newCombinedHooks = combinedHooks
      )
    }
  }

  override private[laminar] def removeFromDynamicList(parent: ReactiveElement.Base): Unit = {
    val group = nestedGroupOpt.getOrElse(
      throw new Exception("Can not removeFromDynamicList: nested group not found (addToDynamicList was not called first). This is a bug in Laminar.")
    )
    if (group.leadingSentinel.ref.parentNode == parent.ref) {
      // This list still hosts the group's span – a genuine removal.
      group.removeFromParent()
      nestedGroupOpt = js.undefined
    } else {
      // This group was already moved to a different parent,
      // stolen by its new host, so nothing else needs to be done.
      // This is similar to DomApi.removeChild being a no-op if
      // the parent doesn't contain the child anymore.
    }
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
