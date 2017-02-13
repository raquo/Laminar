package com.raquo.laminar

import com.raquo.snabbdom.setters.{Attr, AttrSetter}
import com.raquo.snabbdom.nodes.{Hooks, VNodeOps}
import com.raquo.snabbdom.Modifier
import com.raquo.snabbdom.VNode
import com.raquo.xstream.{Listener, Subscription, XStream}
import org.scalajs.dom

import scala.util.Random

class AttrReceiver[V](val attr: Attr[V]) extends AnyVal {

  def <--($value: XStream[V]): Modifier = {
    val commonPrefix = Random.nextInt(99).toString

    var currentNode: VNode = null
    var subscription: Subscription[V, Nothing] = null

    new Modifier {
      override def applyTo(vnode: VNode): Unit = {
        dom.console.log(s"$commonPrefix: APPLY ATTR ${attr.name} VALUE TO")
        dom.console.log(vnode)
        dom.console.log(vnode.elm)
        //          js.debugger()

        val hooks = vnode.data.hooks.getOrElse(new Hooks)

        hooks.addCreateHook { (emptyVNode: VNode, vnode: VNode) =>
          //          hooks.addInsertHook { vnode: VNode =>
          currentNode = vnode
          dom.console.log(s"$commonPrefix: +++ATTR SUB+++")

          subscription = $value.subscribe(Listener(onNext = (value: V) => {
            //              if (false && subscription == null) {
            //                dom.console.log(commonPrefix + ": xx IGNORING xx NEW ATTR VALUE – " + value.toString)
            ////                js.debugger()
            //              } else {
            dom.console.log(s"$commonPrefix NEW ATTR VALUE: " + value.toString)
            val newNode = VNodeOps.copy(currentNode)
            new AttrSetter[V](attr, value).applyTo(newNode)

            //              js.debugger()

            currentNode = patch(currentNode, newNode)
            //              }
          }))
        }.addPostPatchHook { (oldNode: VNode, node: VNode) =>
          // We need this in case some other StreamModifier updates the vnode (we will get this event)
          dom.console.warn(commonPrefix + ": ATTR PREPATCH")
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
