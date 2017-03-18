package com.raquo.laminar

import com.raquo.laminar
import com.raquo.snabbdom.setters.{Attr, AttrSetter}
import com.raquo.snabbdom.nodes.Hooks
import com.raquo.snabbdom.Modifier
import com.raquo.xstream.{Listener, Subscription, XStream}
import org.scalajs.dom

import scala.util.Random

class AttrReceiver[V](val attr: Attr[V, RNode]) extends AnyVal {

  def <--($value: XStream[V]): Modifier[RNode] = {
    val commonPrefix = Random.nextInt(99).toString

    var currentNode: RNode = null
    var subscription: Subscription[V, Nothing] = null

    new Modifier[RNode] {
      override def applyTo(vnode: RNode): Unit = {

        // >>>

        // @TODO
        // PROBLEM #1
        // - So it turns out that postPatch hook is called on the new node, not the oldnode
        // - But the new node could be anything, including an empty node which wouldn't have that postPatch hook
        //
        // PROBLEM #2
        // - We only create a subscription in the create hook
        // - but what if a new node is sameAs old one, and is first inserted into the dom via a patch
        // - then we would actually never subscribe... right?
        //
        // So basically the node can be either init-ed or destroyed in a way that we don't expect
        // and other subscription initialization or destuction will not run in that case
        //
        // SOLUTION
        // - Add generic subscription-cleanup code to every vnode in postPatch (@TODO what exactly...)
        // - ...maybe also add a generic unsubscribe hook on destroy?
        // - initialize subscription differently in applyTo() - register the sub if it's not there yet... @TODO how exactly?
        // - and I guess the create hook should also be a generic one to start up all subscriptions... or...?


        // @TODO We should keep track of subscriptions on the vnode...
        // and then in postPatch
        // - compare old and new nodes
        // - unsubscribe any subscriptions present in old node but not present on the new node
        // - subscribe to any subscriptions in the new node that were not present in old node
        //   - wait WTF that was already done

        // @TODO ^^^ Once we have it, unit-test all of this HEAVILY, all the edge cases

        dom.console.log(s"$commonPrefix: APPLY ATTR ${attr.name} VALUE TO")
        dom.console.log(vnode)
        dom.console.log(vnode.elm)
        //          js.debugger()

        val hooks = vnode.data.hooks.getOrElse(new Hooks)

        hooks.addCreateHook { (emptyVNode: RNode, vnode: RNode) =>
          //          hooks.addInsertHook { vnode: VNode =>
          currentNode = vnode
          dom.console.log(s"$commonPrefix: +++ATTR SUB+++")

          subscription = $value.subscribe(Listener(onNext = (value: V) => {
            //              if (false && subscription == null) {
            //                dom.console.log(commonPrefix + ": xx IGNORING xx NEW ATTR VALUE – " + value.toString)
            ////                js.debugger()
            //              } else {
            dom.console.log(s"$commonPrefix NEW ATTR VALUE: " + value.toString)

            val newNode = currentNode.copy(laminar.builders)

            new AttrSetter[V, RNode](attr, value).applyTo(newNode)

            //              js.debugger()

            currentNode = patch(currentNode, newNode)
            //              }
          }))

          if (currentNode.data.subscriptions.isEmpty) {
            currentNode.data.subscriptions = new Subscriptions(currentNode)
          }
          currentNode.data.subscriptions.get.addAttrSubscription(attr, subscription)


        }.addPostPatchHook { (oldNode: RNode, node: RNode) =>
          // We need this in case some other StreamModifier updates the vnode (we will get this event)
          dom.console.log(commonPrefix + ": ATTR PREPATCH")
          dom.console.log("text:", oldNode.text, node.text)
          dom.console.log("attrs:", oldNode.data.attrs, node.data.attrs)
          //            js.debugger()

          currentNode = node
        }.addDestroyHook { vnode =>
          dom.console.log(s"$commonPrefix: --- ATTR UNSUB ---")

          subscription.unsubscribe()
        }

        vnode.data.hooks = hooks
      }
    }
  }
}
