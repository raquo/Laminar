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

  def createHook(emptyNode: RNode, newNode: RNode): Unit = {
    isMounted = true
    // @TODO[Performance] this can be in createHook (can be shared!)
    // @TODO[Performance] Should we run this only on non-text nodes?
    newNode._debugNodeNumber = GlobalCounter.next().toString
    dom.console.log(s"#${newNode._debugNodeNumber}: hook:create")
  }

  def destroyHook(node: RNode): Unit = {
    isMounted = false
    dom.console.log(s"#${node._debugNodeNumber}: hook:destroy")
    node.data.subscriptions.foreach(_.unsubscribe())
  }

  def prePatchHook(oldNode: RNode, newNode: RNode): Unit = {

    newNode.isMounted = true

    dom.console.log(s"#${newNode._debugNodeNumber}: hook:patch")

    // @TODO[Performance] We could reuse subscriptions if we could compare them by stream+attr equality
    val removedSubscriptions = oldNode.data.subscriptions.filter { oldSubscription =>
      !newNode.data.subscriptions.contains(oldSubscription)
    }

    removedSubscriptions.foreach(_.unsubscribe())

    newNode.data.subscriptions.foreach { subscription =>
      val listener = new DynamicEventSubscription[Any](subscription).dynamicListener
      listener.setActiveNode(newNode)
    }
  }

  data.hooks = new NodeHooks[RNode, RNodeData]()
    .addCreateHook(createHook)
    .addPrePatchHook(prePatchHook)
    .addDestroyHook(destroyHook)

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
