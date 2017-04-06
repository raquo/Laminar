package com.raquo.laminar

import com.raquo.laminar.subscriptions.DynamicEventSubscription
import com.raquo.laminar.utils.GlobalCounter
import com.raquo.snabbdom.hooks.ModuleHooks
import org.scalajs.dom

import scala.scalajs.js

object ReactiveModule {

  def apply(): ModuleHooks[RNode, RNodeData] = {
    new ModuleHooks[RNode, RNodeData]()
      .addCreateHook(createHook)
      .addUpdateHook(updateHook)
      .addDestroyHook(destroyHook)
  }

  def createHook(emptyNode: js.Object, newNode: RNode): Unit = {
    // @TODO[Performance] Should we run this only on non-text nodes?
    newNode._debugNodeNumber = GlobalCounter.next().toString
    newNode.maybeNodeList.foreach(nodeList => nodeList.onNodeUpdate(newNode))
    dom.console.log(s"#${newNode._debugNodeNumber}: hook:create")
  }

  def destroyHook(node: RNode): Unit = {
    // @TODO[Integrity] Our Snabbdom wrapper is unsound here. `node` could actually be an empty node created from within snabbdom
    // @TODO[Integrity] Probably same for other hooks. Need to either make types less specific, or add a wrapper around snabbdom's init method
    if (node.isInstanceOf[RNode]) {
      dom.console.log(s"#${node._debugNodeNumber}: hook:destroy")
      node.data.subscriptions.foreach(_.unsubscribe())
    }
  }

  def updateHook(oldNode: RNode, newNode: RNode): Unit = {
    dom.console.log(s"#${newNode._debugNodeNumber}: hook:patch")

    newNode.maybeNodeList.foreach(nodeList => nodeList.onNodeUpdate(newNode))

    // @TODO[Performance] We could reuse subscriptions in some cases...
    val removedSubscriptions = oldNode.data.subscriptions.filter { oldSubscription =>
      !newNode.data.subscriptions.contains(oldSubscription)
    }
    removedSubscriptions.foreach(_.unsubscribe())

    newNode.data.subscriptions.foreach { subscription =>
      val listener = new DynamicEventSubscription[Any](subscription).dynamicListener
      listener.setActiveNode(newNode)
    }
  }
}
