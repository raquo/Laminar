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

  def createSubscriptions(modifyExistingNode: Boolean): Unit = {
    dom.console.log(s"#${_debugNodeNumber}: createSubscriptions")

    // On subscription, XStream's Memory stream immediately (synchronously) emits
    // its latest value to the new listener, which would have normally triggered
    // an unwanted patch() call while subscriptionRequests have not been fully
    // processed yet.
    // However, we avoid this by processing such patches in a batch.
    var isMemoryStreamInitialValue = true
    val memoryStreamPatches: js.Array[RNode => Unit] = js.Array()

    val newSubscriptions = latestNode.data.subscriptionRequests.map { request =>
      val listener = Listener(onNext = (value: Any) => {
        if (isMemoryStreamInitialValue) {
          memoryStreamPatches.push(nodeToPatch => request.onNext(value, nodeToPatch))
        } else {
          val newNode = latestNode.copy()
          newNode._debugNodeNumber = _debugNodeNumber + "."
          dom.console.log(s"#${_debugNodeNumber}: sub:onNext - $value - (next node: #${newNode._debugNodeNumber})")

          request.onNext(value, newNode)
          latestNode = patch(latestNode, newNode)
        }
      })
      // @TODO[XStream] Add a .subscribe method to XStream that does not need to explicitly specify Error Type
      request.$value.subscribe[Any, Nothing](listener)
    }

    isMemoryStreamInitialValue = false

    latestNode.data.subscriptionRequests = js.Array()
    latestNode.data.subscriptions = latestNode.data.subscriptions.concat(newSubscriptions)

    if (memoryStreamPatches.nonEmpty) {
      val newNode = latestNode.copy()
      newNode._debugNodeNumber = _debugNodeNumber + "."
      dom.console.log(s"#${_debugNodeNumber}: batchPatch - (next node: #${newNode._debugNodeNumber})")

      if (modifyExistingNode) {
        memoryStreamPatches.foreach(patch => patch(latestNode))
      } else {
        memoryStreamPatches.foreach(patch => patch(newNode))
        latestNode = patch(latestNode, newNode)
      }
    }
  }

  def initHook(newNode: RNode): Unit = {
    // @TODO[Performance] Should we run this only on non-text nodes?
    _debugNodeNumber = GlobalCounter.next().toString
    dom.console.log(s"#${_debugNodeNumber}: hook:create")
    latestNode = newNode
    createSubscriptions(modifyExistingNode = true)
  }

  def destroyHook(node: RNode): Unit = {
    dom.console.log(s"#${_debugNodeNumber}: hook:destroy")
    node.data.subscriptions.foreach(_.unsubscribe())
  }

  def prePatchHook(oldNode: RNode, newNode: RNode): Unit = {
    dom.console.log(s"#${_debugNodeNumber}: hook:patch")
    latestNode = newNode
    createSubscriptions(modifyExistingNode = false)

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
    .addInitHook(initHook)
    .addPrePatchHook(prePatchHook)
    .addDestroyHook(destroyHook)
}
