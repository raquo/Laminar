package com.raquo.laminar

import com.raquo.laminar.subscriptions.{DynamicEventListener, DynamicEventSubscription}
import com.raquo.laminar.utils.GlobalCounter
import com.raquo.snabbdom.hooks.NodeHooks
import com.raquo.snabbdom.nodes.Node
import com.raquo.xstream.{Subscription, XStream}
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined


/** Reactive Node */
@ScalaJSDefined
class RNode(tagName: js.UndefOr[String]) extends Node[RNode, RNodeData](tagName) {

  var isMounted = false

  // @TODO[API] Remove this – only used for debug.
  var _debugNodeNumber: String = ""

  def onNextEvent[T](
    onNext: (T, RNode) => Unit
  )(
    value: T,
    activeNode: RNode,
    thisListener: DynamicEventListener[T]
  ): Unit = {
    if (!activeNode.isMounted) {
      onNext(value, activeNode)
    } else {
      val newNode = activeNode.copy()
      newNode._debugNodeNumber = activeNode._debugNodeNumber + "."
      dom.console.log(s"#${activeNode._debugNodeNumber}: sub:onNext - $value - (next node: #${newNode._debugNodeNumber})")

      onNext(value, newNode)
      thisListener.setActiveNode(patch(activeNode, newNode))
    }
  }

  // @TODO[Integrity] Improve types. Use shapeless?
  def addSubscription[T](
    $value: XStream[T],
    onNext: (T, RNode) => Unit
  ): Unit = {
    dom.console.log(s"#${_debugNodeNumber}: addSubscription")

    val listener = new DynamicEventListener[T](
      activeNode = this,
      onNext = onNextEvent(onNext)
    )
    // @TODO[XStream] Add a .subscribe method to XStream that does not need to explicitly specify Error Type
    val newSubscription = $value.subscribe[T, Nothing](listener).asInstanceOf[Subscription[Any, Nothing]]
    data.subscriptions.push(newSubscription)
  }
}
