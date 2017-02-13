package com.raquo.laminar

import com.raquo.snabbdom.{Modifier, VNode}
import com.raquo.snabbdom.nodes.{Hooks, VNodeOps}
import com.raquo.snabbdom.setters.{Style, StyleSetter}
import com.raquo.xstream.{Listener, Subscription, XStream}
import org.scalajs.dom

import scala.util.Random

class StyleReceiver[V](val style: Style[V]) extends AnyVal {

  def <--($value: XStream[V]): Modifier = {
    val commonPrefix = Random.nextInt(99).toString

    var currentNode: VNode = null
    var subscription: Subscription[V, Nothing] = null

    new Modifier {
      override def applyTo(vnode: VNode): Unit = {
        dom.console.log(s"$commonPrefix: APPLY STYLE ${style.name} VALUE TO")
        dom.console.log(vnode)
        dom.console.log(vnode.elm)
        //          js.debugger()

        val hooks = vnode.data.hooks.getOrElse(new Hooks)

        hooks.addCreateHook { (emptyVNode: VNode, vnode: VNode) =>
          //          hooks.addInsertHook { vnode: VNode =>
          currentNode = vnode
          dom.console.log(s"$commonPrefix: +++STYLE SUB+++")

          subscription = $value.subscribe(Listener(onNext = (value: V) => {
            //              if (false && subscription == null) {
            //                dom.console.log(commonPrefix + ": xx IGNORING xx NEW STYLE VALUE – " + value.toString)
            ////                js.debugger()
            //              } else {
            dom.console.log(s"$commonPrefix NEW STYLE VALUE: " + value.toString)
            val newNode = VNodeOps.copy(currentNode)
            new StyleSetter[V](style, value).applyTo(newNode)

            //              js.debugger()

            currentNode = patch(currentNode, newNode)
            //              }
          }))
        }.addPostPatchHook { (oldNode: VNode, node: VNode) =>
          // We need this in case some other StreamModifier updates the vnode (we will get this event)
          dom.console.warn(commonPrefix + ": STYLE PREPATCH")
          dom.console.log("text:", oldNode.text, node.text)
          dom.console.log("styles:", oldNode.data.styles, node.data.styles)
          //            js.debugger()
          currentNode = node
        }.addDestroyHook { vnode =>
          dom.console.log(s"$commonPrefix: --- STYLE UNSUB ---")
          subscription.unsubscribe()
        }

        vnode.data.hooks = hooks
      }
    }
  }
}
