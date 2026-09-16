package com.raquo.laminar.inserters

import com.raquo.airstream.core.Transaction
import com.raquo.ew.JsMap
import com.raquo.laminar
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.modifiers.{RenderableInserter, RenderableNode, RenderableSeq}
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement}
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
  * This is the only [[Inserter]] that isn't a [[DiffableInserter]] –
  * when using it in the `updateChildren` logic, we inject all of its
  * content elements into the map directly instead of injecting this
  * inserter. See [[SlottableChildrenInserter.addToInsertersMap]].
  *
  * Note that [[mutableNodes]] MIGHT be mutable – we [[nodesToRender]].
  */
class SlottableChildrenInserter(
  mutableNodes: laminar.Seq[ChildNode.Base],
  slotName: String | Unit
) extends StaticInserter with Slottable[SlottableChildrenInserter] {

  /** We don't want to depend arbitrarily on [[mutableNodes]]
    * potentially mutating from under us, so we take a snapshot upfront.
    */
  private val nodesToRender: laminar.Seq[ChildNode.Base] = {
    mutableNodes.immutableSnapshot
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

  override private[laminar] def renderInContext(ctx: InsertContext): Unit = {
    // A node is its own Inserter, so it renders as its own list item directly.
    ChildrenInserter.switchToChildren(
      nextItems = nodesToRender,
      renderable = RenderableInserter.inserterRenderable,
      ctx = ctx,
      slotName = slotName
    )
  }

  /** [[SlottableChildrenInserter]] does not have a unique identity that we can use in the
    * `updateChildren` algorithm (no sentinel node), so it is not a [[DiffableInserter]],
    * and so it stays transparent to the children diffing algorithm: it writes its nodes
    * individually into the map instead of itself. And we already know how to diff
    * individual nodes.
    */
  override private[laminar] def addToInsertersMap(contentMap: JsMap[dom.Node, DiffableInserter]): Unit = {
    nodesToRender.foreach { node =>
      val item = {
        if (slotName.isDefined)
          new SlottableChildInserter(node, slotName)
        else
          node
      }
      item.addToInsertersMap(contentMap)
    }
  }

  override def withSlotName(newSlotName: String): SlottableChildrenInserter = {
    new SlottableChildrenInserter(mutableNodes, newSlotName)
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
