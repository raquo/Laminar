package com.raquo.laminar

import com.raquo.interfaces.objectAssign
import com.raquo.snabbdom.collections.Builders
import com.raquo.snabbdom.nodes.{Hooks, Node}
import com.raquo.xstream.Subscription

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined


// @TODO consider using a builder pattern for snabbdom vnodes?

/** Reactive Node */
@ScalaJSDefined
class RNode(tagName: js.UndefOr[String]) extends Node[RNode](tagName) {

  override def copy(builders: Builders[RNode]): RNode = {
    val newNode = super.copy(builders)
    this.data.subscriptions.foreach { subscriptions =>
      newNode.data.subscriptions = copySubscriptions(subscriptions)
    }
    newNode
  }

  def copySubscriptions(subscriptions: Subscriptions): Subscriptions = {
    val newSubscriptions = new Subscriptions(subscriptions.node)
    subscriptions.attrs.foreach { attrs =>
      newSubscriptions.attrs = objectAssign(js.Dictionary[Subscription[_, Nothing]](), attrs)
    }
    subscriptions.on.foreach { on =>
      newSubscriptions.on = objectAssign(js.Dictionary[Subscription[_, Nothing]](), on)
    }
    subscriptions.props.foreach { props =>
      newSubscriptions.props = objectAssign(js.Dictionary[Subscription[_, Nothing]](), props)
    }
    subscriptions.styles.foreach { styles =>
      newSubscriptions.styles = objectAssign(js.Dictionary[Subscription[_, Nothing]](), styles)
    }
    newSubscriptions
  }

  // @TODO also convert VNodeData into RichVNodeData?

  // @TODO Also convert children to RNode?

  // @TODO If we make the laminar render method expect an RNode, is that enough?
  // @TODO Seems like not really because the conversion of children will only be called once?
  // @TODO Like what about the child receiver for example, maybe that one should also expect an RNode?

  if (data.hooks.isEmpty) {
    data.hooks = new Hooks[RNode]()
  }

  data.hooks.get
    .addCreateHook(createHook)
    .addPrePatchHook(prePatchHook)
    .addDestroyHook(destroyHook)

  def createHook(emptyVNode: RNode, vnode: RNode): Unit = {
    // @TODO See below
  }

  def prePatchHook(oldVNode: RNode, vnode: RNode): Unit = {

    // @TODO Make the following changes:
    // - Receiver should add a subscription request into .subscriptionRequests
    // - in both createHook and prePatchHook we should check .subscriptionRequests
    //   - and actually create those subscriptions, and add them to .subscriptions, and remove the request
    // - then we can keep track of currentNode in here, which would make most of what's written below completely moot.
    // - the receiver would just need to provide a function that does the subscription
    // - before we start coding, what's the best way to store subscriptions?
    // @TODO ^^^ SEE ABOVE

    // Compare subscriptions on old node and new node
    // Unsubscribe from any old subscriptions that are not present on the new node

    // Receivers need to always have a fresh reference to currentNode.
    // This is not trivial because only the hooks of the newly patched node are called.
    // The hooks of the old node are not called when it is being patched.

    // Here's how we guarantee that currentNode is always fresh:

    // If the same Receiver modifier is applied to both old and new nodes independently,
    // those would be two different subscriptions (created in the modifier's applyTo method),
    // so we would unsubscribe from the old one here. So it wouldn't matter that its currentNode
    // reference would have gotten out of date.

    // On the other hand, if this hook is called because the node on which a subscription was applied
    // is being updated by some other Receiver on that node, then the new node will have the same
    // subscription as the prev node because we copy nodes including subscriptions when receivers
    // receive events. This means that the new node also has all the hooks of the old node, including
    // the one that updates currentNode in our subscription.

    // Lastly, if we're getting this event because the node is being patched by the very subscription
    // in question, the subscription has already updated its currentNode itself, and additionally the
    // part about copying from the previous paragraph applies as well.

    // So there is no way to patch a node while having currentNode in a subscription
    // on the previous node out of sync.

    // @TODO implement generic logic for – check ALL subscriptions, without referencing attr

//    val maybeOldSubscriptions = oldVNode.data.subscriptions
//
//    maybeOldSubscriptions.foreach { oldSubscriptions =>
//      oldSubscriptions.attrs.get.get(attr.name).foreach { oldSubscription =>
//        val newSubscriptions = vnode.data.subscriptions
//        if (newSubscriptions.isEmpty) {
//          dom.console.log(s"$commonPrefix: Unsubscribe old attr `${attr.name}` subscription because new subscriptions are not defined at all")
//          oldSubscription.unsubscribe()
//        } else {
//          val newSubscription = newSubscriptions.get.attrs.get.get(attr.name)
//          if (newSubscription.isEmpty || newSubscription.get.stream != oldSubscription.stream) {
//            dom.console.log(s"$commonPrefix: Unsubscribe old attr `${attr.name}` subscription because new subscription is missing or does not match")
//            oldSubscription.unsubscribe()
//          } else {
//            dom.console.log(s"$commonPrefix: Keep attr `${attr.name}` subscription because it's the same stream")
//          }
//        }
//      }
//    }
  }

  def destroyHook(vnode: RNode): Unit = {
    // @TODO unsubscribe from all subscriptions
    ???
  }
}
