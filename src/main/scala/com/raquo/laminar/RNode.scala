package com.raquo.laminar

import com.raquo.laminar.utils.GlobalCounter
import com.raquo.snabbdom.nodes.{Hooks, Node}
import com.raquo.xstream.{Listener, XStream}
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined


/** Reactive Node */
@ScalaJSDefined
class RNode(tagName: js.UndefOr[String]) extends Node[RNode, RNodeData](tagName) {

  // @TODO[Docs] explain this concept in more detail for our collective sanity.
  /** "Last RNode that is sameAs this one that has been added to the DOM" */
  private var latestNode: RNode = _


  // @TODO[API] Remove this – only used for debug.
  var _debugNodeNumber: String = ""

  def registerSubscriptionRequest[V]($value: XStream[V], onNext: (V, RNode) => Unit): Unit = {
    data.subscriptionRequests.push(SubscriptionRequest($value, onNext))
  }

  def createSubscriptions(): Unit = {
    dom.console.log(s"#${_debugNodeNumber}: createSubscriptions")
    val newSubscriptions = latestNode.data.subscriptionRequests.map { request =>
      // @TODO[XStream] Add a .subscribe method to XStream that does not need to explicitly specify Error Type
      val onNext = (value: Any) => {
        val newNode = latestNode.copy()
        newNode._debugNodeNumber = _debugNodeNumber + "."
        dom.console.log(s"#${_debugNodeNumber}: sub:onNext (next node: #${newNode._debugNodeNumber})")

        request.onNext(value, newNode)
        latestNode = patch(latestNode, newNode)
      }
      request.$value.subscribe[Any, Nothing](Listener(onNext = onNext))
    }
    latestNode.data.subscriptions.push(newSubscriptions: _*) // @TODO[Performance] Check that generated code is optimal
    latestNode.data.subscriptionRequests = js.Array()
  }

  def createHook(emptyNode: RNode, newNode: RNode): Unit = {
    _debugNodeNumber = GlobalCounter.next().toString
    dom.console.log(s"#${_debugNodeNumber}: hook:create")
    latestNode = newNode
    createSubscriptions()
  }

  def destroyHook(node: RNode): Unit = {
    dom.console.log(s"#${_debugNodeNumber}: hook:destroy")
    node.data.subscriptions.foreach(_.unsubscribe())
  }

  def prePatchHook(oldNode: RNode, newNode: RNode): Unit = {
    dom.console.log(s"#${_debugNodeNumber}: hook:patch")
    latestNode = newNode
    createSubscriptions()

    // @TODO[Performance] Can we reuse subscriptions
    val removedSubscriptions = oldNode.data.subscriptions.filter { oldSubscription =>
      !newNode.data.subscriptions.contains(oldSubscription)
    }
    removedSubscriptions.foreach(_.unsubscribe())
  }

  if (data.hooks.isEmpty) {
    data.hooks = new Hooks[RNode]()
  }

  data.hooks.get
    .addCreateHook(createHook)
    .addPrePatchHook(prePatchHook)
    .addDestroyHook(destroyHook)
}
