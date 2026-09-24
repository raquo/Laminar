package com.raquo.laminar.inserters

import com.raquo.airstream.ownership.{Owner, Subscription}
import com.raquo.ew
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.Modifier
import com.raquo.laminar.nodes.{ChildNode, CommentNode, ParentNode, ReactiveElement}
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.|

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

  private[laminar] def addToInsertersMap(contentMap: ew.JsMap[dom.Node, DiffableInserter]): Unit

  /** Used by `onMountInsert`. Returns a subscription to cancel on unmount when the inserter
    * manages ongoing state (dynamic inserters), or `None` when it renders static content once.
    */
  private[laminar] def renderOnMount(context: InsertContext, owner: Owner): Option[Subscription]
}

/** Diffable inserters are used in `children <--` diffing and tracking logic.
  *
  * [[SlottableChildrenInserter]] is the only inserter thst ISN'T diffable,
  * and that is because we can't implement a `stableFirstNode` for it –
  * it has no sentinel, and its first content node is not unique,
  * a ChildNode inserter would have the same identity, confusing the
  * diffing algorithm which looks up inserters by their `stableFirstNode`
  * identity.
  *
  * To work around this limitation, [[SlottableChildrenInserter]] inserts
  * its contents one-by-one into `contentMap`, to be managed by the diffing
  * algorithm.
  */
trait DiffableInserter extends Inserter {

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
    listSlotName: String | Unit
  ): Unit

  /** This is the inserter equivalent of calling `setParent(None)` for each of its nodes:
    * This inserter's nodes are removed from the parent, their subscriptions cleaned up,
    * etc.
    *
    * Pre-requisite: you must have called [[addToDynamicList]]
    *                with the same parent before calling this.
    *
    * @param keepNestedItem Items nested INSIDE this inserter that must be left in place
    *                       rather than removed – see [[InsertContext.removeContentMapNodesFromDom]].
    *                       #Warning: The caller is responsible for checking whether THIS
    *                        inserter's own `stableFirstNode` against `keepItem`.
    */
  private[laminar] def removeFromDynamicList(
    parent: ReactiveElement.Base,
    keepNestedItem: dom.Node => Boolean
  ): Unit

  /** No mount / re-mount, just a lateral move.
    *
    * Note: Note: [[DynamicInserter]] overrides this with a special implementation.
    *
    * Pre-requisite: you must have called [[addToDynamicList]]
    *                with the same parent before calling this.
    */
  private[laminar] def moveWithinDynamicList(
    parent: ReactiveElement.Base,
    afterRef: dom.Node,
    listSlotName: String | Unit
  ): Unit = {
    // Re-affirm the list's slot: this is a same-list reorder, so the item stays in its slot.
    addToDynamicList(
      parent = parent,
      afterRef = afterRef,
      listSlotName = listSlotName
    )
  }

  /** Applies to the element(s) currently in this inserter.
    *
    * DynamicInserter also records the name on its context, so elements it inserts
    * LATER (as its observable emits) are slotted the same way – its slot must
    * persist, not just apply to the current content.
    */
  private[laminar] def applySlot(
    debugParent: ParentNode.Base,
    slotName: String | Unit
  ): Unit

  /** The first DOM node of this item's span (valid after [[addToDynamicList]]).
    *  - Must always return the same node for the same inserter
    *    - Implementations must override this with a `val` or `lazy val`.
    *  - For single static nodes (e.g. [[ChildNode]]), this is the node itself.
    *  - For dynamic inserters, it's the leading sentinel.
    *
    * [[SlottableChildInserter.stableFirstNode]] is an abstraction leak that,
    * strictly speaking, does not fit the requirements, but we work around it.
    *
    * [[SlottableChildrenInserter]] does not have this at all – see its scaladoc.
    */
  private[laminar] def stableFirstNode: dom.Node

  /** The last DOM node of this item's span.
    *
    * For some inserters, it's the trailing sentinel node.
    * It can be `eq` to [[stableFirstNode]], e.g. for single static node inserters.
    */
  private[laminar] def lastNode: dom.Node

  override private[laminar] def addToInsertersMap(
    contentMap: ew.JsMap[dom.Node, DiffableInserter]
  ): Unit = {
    contentMap.set(stableFirstNode, this)
  }
}

trait Slottable[+Self <: Inserter] { this: Inserter =>

  /** Create a copy of the inserter that slots its content into `slotName`.
    * Used by [[com.raquo.laminar.nodes.Slot]] to slot content into web components.
    */
  def withSlotName(slotName: String): Self with Slottable[Self]
}

trait StaticInserter extends Inserter {

  private[laminar] def renderInContext(ctx: InsertContext): Unit

  /** Note: Static content is rendered once and keeps no ongoing state, so there is nothing to unsubscribe. */
  override private[laminar] def renderOnMount(context: InsertContext, owner: Owner): Option[Subscription] = {
    renderInContext(context)
    None
  }
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
  slotName: String | Unit
) extends DiffableInserter with Slottable[DynamicInserter] {

  /** Owner typically comes from MountContext of the InsertContext parentNode.
    *
    * #Warning: make sure to cancel the returned subscription when it's time.
    */
  private[laminar] def renderIntoSharedContext(
    insertContext: InsertContext,
    owner: Owner
  ): Subscription = {
    insertContext.setCurrentSlotName(slotName) // prepare the shared context for this inserter
    insertFn(insertContext, owner)
  }

  override private[laminar] def renderOnMount(
    context: InsertContext,
    owner: Owner
  ): Option[Subscription] = {
    Some(renderIntoSharedContext(context, owner))
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
          initialSlotName = slotName // as-is, because a plain `element` parent adds no slot
        )
      }
    ) { group =>
      // This inserter instance already lives somewhere (applied to another element, or as a
      // `children <--` list item). Applying it here (e.g. `element.amend(inserter)`) MOVES it
      // seamlessly (no re-mounting).
      group.moveToParent(
        newParent = element,
        afterRefOpt = afterRefOpt,
        newSlotName = slotName // as-is, because a plain `element` parent adds no slot
      )
    }
  }

  override def withSlotName(newSlotName: String): DynamicInserter = {
    new DynamicInserter(insertFn, newSlotName)
  }

  override private[laminar] def applySlot(
    debugParent: ParentNode.Base,
    listSlotName: String | Unit
  ): Unit = {
    // Own slot wins over the destination list's slot (innermost `Slot` wins).
    nestedGroupOpt.foreach(_.applySlot(slotName.orElse(listSlotName)))
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
    listSlotName: String | Unit
  ): Unit = {
    // Own slot wins over the destination list's slot (innermost `Slot` wins).
    val newSlotName = slotName.orElse(listSlotName)
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
          initialSlotName = newSlotName
        )
      }
    ) { group =>
      // This inserter instance already lives as a group somewhere else.
      // Add trailing sentinel for proper tracking inside `children <--`,
      // then move it seamlessly to its new location.
      // Note: The move takes the group's span out of the previous list's walked
      //       region, so when that list later reconciles it never revisits this
      //       inserter – it does NOT call `removeFromDynamicList` on it. This new
      //       list manages the inserter now.
      group.ensureTrailingSentinel()
      group.moveToParent(
        newParent = parent,
        afterRefOpt = afterRef,
        newSlotName = newSlotName
      )
    }
  }

  override private[laminar] def removeFromDynamicList(
    parent: ReactiveElement.Base,
    keepNestedItem: dom.Node => Boolean
  ): Unit = {
    val group = nestedGroupOpt.getOrElse(
      throw new Exception("Can not removeFromDynamicList: nested group not found (addToDynamicList was not called first). This is a bug in Laminar.")
    )
    if (group.leadingSentinel.ref.parentNode == parent.ref) {
      // This list still hosts the group's span – a genuine removal – #Note: probably – see below
      group.removeFromParent(keepNestedItem)
      nestedGroupOpt = js.undefined
    } else {
      // The group was already moved to a different parent, stolen by its new host, so there is
      // nothing to remove here (like DomApi.removeChild is a no-op when the parent no longer
      // contains the child).
      // #Note: stealing can also happen without changing parent!
      //  1. Thieving dynamic inserter could be a sibling of the original parent dynamic inserter
      //  2. Thieving DYNAMIC inserter could be a CHILD of the original parent dynamic inserter
      //      - even though there's logical (inserter) nesting, in the DOM, there is no extra
      //        nesting, so the parents match.
      // In both cases, we avoid the problem by not calling `removeFromDynamicList`:
      //  1. The stolen node would be out of bounds of the original parent inserter in the DOM,
      //     so we would not reach it through iteration in `updateChildren`... I think...
      //  2. In `updateChildren` we basically only walk over the `stableFirstNode`-s of inserters,
      //     calling those inserters' methods to manage their nodes inside. So we wouldn't call
      //     `removeFromDynamicList` directly from `updateChildren` on a node that now sits inside
      //     a child dynamic inserter – managing that node is now delegated to that child.
    }
  }

  /** Note: overrides default implementation */
  override private[laminar] def moveWithinDynamicList(
    parent: ReactiveElement.Base,
    afterRef: dom.Node,
    listSlotName: String | Unit
  ): Unit = {
    nestedGroupOpt.fold(
      ifEmpty = {
        // This inserter was previously stolen, then the thief removed this inserter,
        // and now the list that originally tracked this inserter in its `contentMap`
        // is re-emitting it now again.
        // The nodes of this inserter were removed from the DOM, so re-insert + re-mount.
        // Note: The list's item count already accounted for this inserter (we were in
        //       its previous contentMap), so this move does not affect `currentItemCount`.
        addToDynamicList(parent, afterRef, listSlotName)
      }
    ) { group =>
      if (group.leadingSentinel.ref.parentNode != parent.ref) {
        // Cross-parent re-steal: another list stole this group, and now the
        // previous list re-emits with this inserter again, and steals it back.
        // We need a full `moveToParent` here to bring it back,
        // transfer subscription ownership, and update the slot.
        group.ensureTrailingSentinel()
        group.moveToParent(
          newParent = parent,
          afterRefOpt = afterRef,
          newSlotName = slotName.orElse(listSlotName) // innermost `Slot` wins
        )
      } else {
        // Same DOM parent. Reachable in two cases:
        //  1. Reordering within the same dynamic list
        //  2. Stealing back an item that was previously stolen into a sibling parent inserter
        //     (e.g. two `children <--`, possibly in different `Slot`s, under one element)
        val lastRef = lastNode // trailing sentinel
        var node = stableFirstNode // leading sentinel
        var reference = afterRef
        var continue = true
        while (continue) {
          val nextNode = node.nextSibling // capture before `insertAfter` moves `node` away
          continue = node ne lastRef
          // #Note: raw move – bypasses willSetMount / setMount / slot reconcile
          //  – slot handled explicitly just below.
          DomApi.raw.insertAfter(
            parent = parent.ref,
            newChild = node,
            referenceChild = reference
          )
          reference = node
          node = nextNode
        }
        // In case #2 above, we do need to reaffirm the slot.
        // In case #1, this is a no-op.
        applySlot(parent, listSlotName)
      }
    }
  }
}
