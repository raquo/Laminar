package com.raquo.laminar

import com.raquo.laminar.subscriptions.DynamicEventSubscription
import com.raquo.laminar.utils.GlobalCounter
import com.raquo.snabbdom.hooks.ModuleHooks
import org.scalajs.dom

object ReactiveModule {

  def apply(): ModuleHooks[RNode, RNodeData] = {
    new ModuleHooks[RNode, RNodeData]()
      .addCreateHook(createHook)
      .addUpdateHook(updateHook)
      .addDestroyHook(destroyHook)
  }

  def createHook(emptyNode: RNode, newNode: RNode): Unit = {
    newNode.isMounted = true
    // @TODO[Performance] this can be in createHook (can be shared!)
    // @TODO[Performance] Should we run this only on non-text nodes?
    newNode._debugNodeNumber = GlobalCounter.next().toString
    dom.console.log(s"#${newNode._debugNodeNumber}: hook:create")
  }

  def destroyHook(node: RNode): Unit = {
    // @TODO[Integrity] Our Snabbdom wrapper is unsound here. `node` could actually be an empty node created from within snabbdom
    // @TODO[Integrity] Probably same for other hooks. Need to either make types less specific, or add a wrapper around snabbdom's init method
    if (node.isInstanceOf[RNode]) {
      node.isMounted = false
      dom.console.log(s"#${node._debugNodeNumber}: hook:destroy")
      node.data.subscriptions.foreach(_.unsubscribe())
    }
  }

  def updateHook(oldNode: RNode, newNode: RNode): Unit = {
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
}
