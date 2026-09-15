package com.raquo.laminar.inserters

import com.raquo.airstream.core.Transaction
import com.raquo.ew.JsArray
import com.raquo.laminar
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.{RenderableInserter, RenderableNode, RenderableSeq}
import com.raquo.laminar.nodes.{ChildNode, CommentNode, ParentNode, ReactiveElement}
import org.scalajs.dom

import scala.scalajs.js.|

/** Static inserter for a static list of nodes, optionally slotted.
  *
  * Used when:
  *  - onMountInsert callback returns a Seq, or
  *  - when passing Seq[Node] as one of the items to Web Components `Slot.apply`.
  *
  * See also comments in [[SlottableChildInserter]] and `componentSeqToInserter` implicit.
  *
  * Note that [[mutableNodes]] MIGHT be mutable – we [[nodesToRender]].
  */
class SlottableChildrenInserter(
  mutableNodes: laminar.Seq[ChildNode.Base],
  slotName: String | Unit
) extends StaticInserter with Slottable[SlottableChildrenInserter] {

  /** We don't want to depend arbitrarily on [[mutableNodes]]
    * potentially mutating from under us, so we take a snapshot upfront.
    *
    * Also, this is guaranteed to be NON-EMPTY, which is a requirement
    * for our [[stableFirstNode]] and [[lastNode]] logic. We create a
    * fake comment node if the source [[mutableNodes]] is empty.
    */
  private val nodesToRender: laminar.Seq[ChildNode.Base] = {
    if (mutableNodes.nonEmpty)
      mutableNodes.immutableSnapshot
    else
      laminar.Seq.from(
        JsArray(new CommentNode(""))
      )
  }

  override def apply(element: ReactiveElement.Base): Unit = {
    Transaction.onStart.shared {
      nodesToRender.foreach { node =>
        DomApi.appendChild(
          parent = element,
          child = node,
          slotName = slotName
        )
      }
    }
  }

  override def renderInContext(ctx: InsertContext): Unit = {
    // A node is its own Inserter, so it renders as its own list item directly.
    ChildrenInserter.switchToChildren(
      nextItems = nodesToRender,
      renderable = RenderableInserter.inserterRenderable,
      ctx = ctx,
      slotName = slotName
    )
  }

  override private[laminar] val stableFirstNode: dom.Node = nodesToRender.head.ref

  override private[laminar] lazy val lastNode: dom.Node = nodesToRender.last.ref

  override private[laminar] def addToDynamicList(
    parent: ReactiveElement.Base,
    afterRef: dom.Node,
    listSlotName: String | Unit
  ): Unit = {
    var insertAfter = afterRef
    // Own slot wins over the destination list's slot (innermost `Slot` wins).
    val effectiveSlotName = slotName.orElse(listSlotName)
    nodesToRender.foreach { node =>
      DomApi.insertChildAfter(
        parent = parent,
        newChild = node,
        referenceChildRef = insertAfter,
        slotName = effectiveSlotName
      )
      insertAfter = node.ref
    }
  }

  override private[laminar] def removeFromDynamicList(parent: ReactiveElement.Base): Unit = {
    nodesToRender.foreach { node =>
      DomApi.removeChild(parent, node)
    }
  }

  override def withSlotName(newSlotName: String): SlottableChildrenInserter = {
    new SlottableChildrenInserter(mutableNodes, newSlotName)
  }

  override private[laminar] def applySlot(
    debugParent: ParentNode.Base,
    listSlotName: String | Unit
  ): Unit = {
    // Own slot wins over the destination list's slot, mirroring `addToDynamicList`.
    val effectiveSlotName = slotName.orElse(listSlotName)
    nodesToRender.foreach { node =>
      node.applySlot(debugParent, slotName = effectiveSlotName)
    }
  }

}

object SlottableChildrenInserter {

  def noSlotName[Collection[_], Component](
    components: Collection[Component],
    renderableSeq: RenderableSeq[Collection],
    renderableNode: RenderableNode[Component]
  ): SlottableChildrenInserter = {
    val children = renderableNode.asNodeSeq(renderableSeq.toSeq(components))
    new SlottableChildrenInserter(children, slotName = ())
  }

}
