package com.raquo.laminar.inserters

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.RenderableNode
import com.raquo.laminar.nodes.{ChildNode, ParentNode, ReactiveElement}
import org.scalajs.dom
import org.scalajs.dom.Node

import scala.scalajs.js.|

/** Static inserter for a single static node, optionally slotted.
  *
  * Used for Web Components ([[com.raquo.laminar.nodes.Slot.apply]]),
  * which needs to set the `slot` attribute on the node it wraps
  * (see [[com.raquo.laminar.nodes.ReactiveElement]]).
  *
  * In theory, ReactiveElement could become Slottable to avoid the need for
  * this wrapper, however this seems undesirable as the slot is a property
  * of the insertion point / mechanism, not the node. And Slottable-s are
  * copyable via [[Slottable.withSlotName]], which the elements can't be.
  *
  * Note that for slots, text nodes should not be allowed as they can not
  * be slotted. That restriction is implemented in the implicit conversion
  * layer, not here, which is not very principled.
  *
  * See also `componentToInserter` implicit.
  */
class SlottableChildInserter(
  child: ChildNode.Base,
  slotName: String | Unit
)
extends StaticInserter
with DiffableInserter
with Slottable[SlottableChildInserter] {

  /** Note: abstraction leak. This `stableFirstNode` is not a unique
    * identifier of this inserter – the plain ChildNode `child` also
    * has the same `ref` for this identifier.
    *
    * But, this is fine. Just fine. The only difference is in the slot,
    * and in `updateChildren`'s same-inserter branch, we specifically
    * apply the new inserter's slot for exactly this reason.
    *
    * Need to be careful if using `stableFirstNode` for any other logic.
    */
  override private[laminar] val stableFirstNode: Node = child.ref

  override private[laminar] def lastNode: dom.Node = child.ref

  override def apply(element: ReactiveElement.Base): Unit = {
    DomApi.appendChild(
      parent = element,
      child = child,
      slotName = slotName
    )
  }

  override private[laminar] def renderInContext(ctx: InsertContext): Unit = {
    ChildInserter.switchToChild(
      maybeLastSeenChild = (),
      newChildNodeOpt = child,
      ctx = ctx,
      slotName = slotName
    )
  }

  override private[laminar] def addToDynamicList(
    parent: ReactiveElement.Base,
    afterRef: dom.Node,
    listSlotName: String | Unit
  ): Unit = {
    // Cheap common case: a single static node is its own anchor + end, no sentinel.
    DomApi.insertChildAfter(
      parent = parent,
      newChild = child,
      referenceChildRef = afterRef,
      slotName = slotName.orElse(listSlotName) // own slot wins over the destination list's
    )
  }

  override private[laminar] def removeFromDynamicList(parent: ReactiveElement.Base): Unit = {
    DomApi.removeChild(parent = parent, child = child)
  }

  override def withSlotName(newSlotName: String): SlottableChildInserter = {
    new SlottableChildInserter(child, newSlotName)
  }

  override private[laminar] def applySlot(
    debugParent: ParentNode.Base,
    listSlotName: String | Unit
  ): Unit = {
    // Own slot wins over the destination list's slot, mirroring `addToDynamicList`.
    child.applySlot(
      debugParent = debugParent,
      slotName = slotName.orElse(listSlotName)
    )
  }
}

object SlottableChildInserter {

  def noSlotName(
    node: ChildNode.Base,
  ): SlottableChildInserter = {
    new SlottableChildInserter(node, slotName = ())
  }

  def noSlotNameC[Component](
    component: Component
  )(implicit
    renderable: RenderableNode[Component]
  ): SlottableChildInserter = {
    new SlottableChildInserter(renderable.asNode(component), slotName = ())
  }
}
