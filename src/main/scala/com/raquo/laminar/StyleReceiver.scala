package com.raquo.laminar

import com.raquo.laminar
import com.raquo.snabbdom.Modifier
import com.raquo.snabbdom.nodes.Hooks
import com.raquo.snabbdom.setters.{Style, StyleSetter}
import com.raquo.xstream.{Listener, Subscription, XStream}
import org.scalajs.dom

import scala.util.Random

class StyleReceiver[V](val style: Style[V, RNode, RNodeData]) extends AnyVal {

  def <--($value: XStream[V]): Modifier[RNode, RNodeData] = {
    val commonPrefix = Random.nextInt(99).toString

    var currentNode: RNode = null
    var subscription: Subscription[V, Nothing] = null

    new Modifier[RNode, RNodeData] {
      override def applyTo(vnode: RNode): Unit = {
        dom.console.log(s"$commonPrefix: APPLY STYLE ${style.name} VALUE TO")
        dom.console.log(vnode)
        dom.console.log(vnode.elm)
        //          js.debugger()

        val hooks = vnode.data.hooks.getOrElse(new Hooks)

        hooks.addCreateHook { (emptyVNode: RNode, vnode: RNode) =>
          //          hooks.addInsertHook { vnode: VNode =>
          currentNode = vnode
          dom.console.log(s"$commonPrefix: +++STYLE SUB+++")

          subscription = $value.subscribe(Listener(onNext = (value: V) => {
            //              if (false && subscription == null) {
            //                dom.console.log(commonPrefix + ": xx IGNORING xx NEW STYLE VALUE – " + value.toString)
            ////                js.debugger()
            //              } else {
            dom.console.log(s"$commonPrefix NEW STYLE VALUE: " + value.toString)
            val newNode = currentNode.copy()
            new StyleSetter[V, RNode, RNodeData](style, value).applyTo(newNode)

            //              js.debugger()

            currentNode = patch(currentNode, newNode)
            //              }
          }))
        }.addPostPatchHook { (oldNode: RNode, node: RNode) =>
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
