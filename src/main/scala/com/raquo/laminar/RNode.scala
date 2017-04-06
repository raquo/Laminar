package com.raquo.laminar

import com.raquo.laminar.subscriptions.{DynamicNodeList, DynamicEventListener, DynamicEventSubscription}
import com.raquo.snabbdom.nodes.Node
import com.raquo.xstream.{Subscription, XStream}
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined


/** Reactive Node */
@ScalaJSDefined
class RNode(tagName: js.UndefOr[String]) extends Node[RNode, RNodeData](tagName) {

  // @TODO[API] Should this live here, or in DynamicEventListener?
  var activeParentNode: js.UndefOr[RNode] = js.undefined

  /** If this field is defined, this node is part of this [[DynamicNodeList]] list */
  var maybeNodeList: js.UndefOr[DynamicNodeList] = js.undefined

  // @TODO[API] Remove this – only used for debug.
  var _debugNodeNumber: String = ""

  @inline def isMounted: Boolean = elm.isDefined

  def onNextEvent[V](
    onNext: (V, RNode) => RNode
  )(
    value: V,
    activeNode: RNode,
    thisListener: DynamicEventListener[V]
  ): Unit = {
    val newNode = onNext(value, activeNode)

    if (!activeNode.isMounted) {
      thisListener.setActiveNode(newNode)
    } else {
      newNode._debugNodeNumber = activeNode._debugNodeNumber + "."
      dom.console.log(s"#${activeNode._debugNodeNumber}: sub:onNext - $value - (next node: #${newNode._debugNodeNumber})")

      // Snabbdom will only do the patching if it has two difference nodes to compare.
      // If `onNext` returns the same node that was provided to it AND that node is already mounted,
      // we assume that `onNext` does not want to make any changes this time, or makes the changes
      // itself.
      if (activeNode != newNode) {
        patch(activeNode, newNode)
        thisListener.setActiveNode(newNode)
      }
    }
  }

  // @TODO[Docs] Document what the onNext needs to do (check isMounted, etc.)
  // @TODO[Docs] Document what those RNodes are / should be (activeNode and newNode if xxx)
  // @TODO[Integrity] Improve types. Use shapeless?
  def subscribe[T](
    $value: XStream[T],
    onNext: (T, RNode) => RNode
  ): DynamicEventSubscription[T] = {
    dom.console.log(s"#${_debugNodeNumber}: addSubscription")

    val listener = new DynamicEventListener[T](
      activeNode = this,
      onNext = onNextEvent(onNext)
    )
    // @TODO[XStream] Add a .subscribe method to XStream that does not need to explicitly specify Error Type
    val newSubscription = $value.subscribe[T, Nothing](listener)
    data.subscriptions.push(newSubscription.asInstanceOf[Subscription[Any, Nothing]])
    new DynamicEventSubscription(newSubscription)
  }

  def hasSubscription[T](
    subscription: DynamicEventSubscription[T]
  ): Boolean = {
    // @TODO[Performance] use a native JS method
    data.subscriptions.contains(subscription.subscription)
  }

  override def addChild(child: RNode): Unit = {
    super.addChild(child)
    // @TODO[Integrity] Add safeguards against updating activeParentNode at inappropriate times (which are..?)
    child.activeParentNode = this
  }

  override def copyInto(node: RNode): Unit = {
    super.copyInto(node)
    node.activeParentNode = activeParentNode
    node.maybeNodeList = maybeNodeList
  }
}
