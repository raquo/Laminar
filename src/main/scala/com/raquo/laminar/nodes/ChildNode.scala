package com.raquo.laminar.nodes

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.inserters.{ChildInserter, InsertContext, StaticInserter}
import org.scalajs.dom

import scala.annotation.tailrec
import scala.scalajs.js
import scala.scalajs.js.|

trait ChildNode[+Ref <: dom.Node]
extends ReactiveNode[Ref]
with StaticInserter {

  private var _maybeParent: Option[ParentNode.Base] = None

  def maybeParent: Option[ParentNode.Base] = _maybeParent

  /**  - Note: Make sure to call [[willSetParent]] before calling this method manually.
    *  - Note: This can be called even if moving element within the same parent.
    *  - Note: This is overridden in [[ReactiveElement]].
    */
  private[laminar] def setParent(maybeNextParent: Option[ParentNode.Base]): Unit = {
    _maybeParent = maybeNextParent
  }

  /** This is called as a notification, BEFORE changes to the real DOM or to the Scala DOM tree are applied.
    *  - Corollary: When this is called, this node's maybeParent reference has not been updated yet.
    *
    * Default implementation is a noop. It is overridden in [[ReactiveElement]].
    *
    * Note: This can be called even if moving element within the same parent.
    *
    * Note: This method is NOT automatically called inside [[setParent]] because [[setParent]] is called
    *       AFTER the real DOM was modified. Therefore, IF you call [[setParent]] directly, you need to
    *       also call [[willSetParent]] before that, if you plan to implement that method. However, if you
    *       only call [[setParent]] indirectly, via the methods defined in [[ParentNode]], those methods
    *       take care of calling [[willSetParent]] for you.
    *
    * @param maybeNextParent  `None` means this node is about to be detached form its parent
    */
  @inline private[laminar] def willSetParent(maybeNextParent: Option[ParentNode.Base]): Unit = ()

  /** Reconcile this node's `slot` attribute to the slot of the position it is being inserted
    * into. Called on every insert / move. `slotName` is the destination's slot, if any.
    *
    * Default is a no-op (comment nodes are never slotted). Overridden in [[ReactiveElement]]
    * (set / clear the attribute) and in [[TextNode]] (report – text can not be slotted).
    *
    * Comment nodes don't report warnings because technically we insert sentinel comment nodes
    * with slots when their inserter is slotted – exempting them would require more complications.
    */
  private[laminar] def applySlot(
    parent: ParentNode.Base,
    newSlotName: String | Unit
  ): Unit = ()

  override def apply(parentNode: ReactiveElement.Base): Unit = {
    DomApi.appendChild(parent = parentNode, child = this, slotName = ())
  }

  // -- Inserter methods --

  override private[laminar] val stableFirstNode: dom.Node = ref

  override private[laminar] def lastNode: dom.Node = ref

  override private[laminar] def addToDynamicList(
    parent: ReactiveElement.Base,
    afterRef: dom.Node,
    listSlotName: String | Unit
  ): Unit = {
    DomApi.insertChildAfter(
      parent = parent,
      newChild = this,
      referenceChildRef = afterRef,
      slotName = listSlotName
    )
  }

  override private[laminar] def removeFromDynamicList(
    parent: ReactiveElement.Base
  ): Unit = {
    DomApi.removeChild(parent = parent, child = this)
  }

  override def renderInContext(ctx: InsertContext): Unit = {
    ChildInserter.switchToChild(
      maybeLastSeenChild = (),
      newChildNodeOpt = this,
      ctx = ctx,
      slotName = () // no slot of its own – compare to other renderInContext impls
    )
  }

}

object ChildNode {

  type Base = ChildNode[dom.Node]

  /** Note: This walks up Laminar's element tree, not the real DOM tree.
    * See [[com.raquo.laminar.domapi.DomApi.raw.isDescendantOf]] if you want to check the real DOM tree.
    */
  @tailrec final def isDescendantOf(
    child: ChildNode.Base,
    parent: ParentNode.Base
  ): Boolean = {
    child.maybeParent match {
      case Some(`parent`) => true
      case Some(intermediateParent: ChildNode.Base) => isDescendantOf(intermediateParent, parent)
      case _ => false
    }
  }
}
