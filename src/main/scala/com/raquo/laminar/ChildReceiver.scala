package com.raquo.laminar

import com.raquo.snabbdom.nodes.{Hooks, VNode}
import com.raquo.snabbdom.tags.div
import com.raquo.snabbdom.utils.HookLogger
import com.raquo.xstream.{Listener, Subscription, XStream}
import org.scalajs.dom

import scala.util.Random

object ChildReceiver {

  def <--($node: XStream[VNode]): VNode = {

    val commonPrefix = Random.nextInt(99).toString

    // @TODO add .key here (and copy it into newly returned nodes to ensure node is destroyed?
    var currentNode = div()
    var subscription: Subscription[VNode, Nothing] = null
    // @TODO do we need to patch this node's parent node when we update it?

    def addChildHooks(hooks: Hooks): Hooks = {
      hooks.addCreateHook { (emptyNode: VNode, vnode: VNode) =>
        //      hooks.addInsertHook { vnode =>

        dom.console.log(commonPrefix + ": ++CHILD SUB++ ")
        subscription = $node.subscribe(Listener(onNext = vnode => {
//          if (false && subscription == null) {
//            dom.console.log(commonPrefix + ": xx IGNORING xx NEW CHILD EVENT – " + vnode.text)
////            js.debugger()
//          } else {
            dom.console.log(commonPrefix + ": NEW CHILD EVENT – " + vnode.text)
//            if (vnode.text == "P") {
//              js.debugger()
//            }

            val newHooks = addChildHooks(vnode.data.hooks.getOrElse(HookLogger(
              prefix = commonPrefix + "-" + Random.nextInt(99).toString,
              callRemoveNode = true
            )))
            vnode.data.hooks = newHooks
//            if (currentNode == vnode) {
//              js.debugger()
//            }
            currentNode = patch(currentNode, vnode)
//          }
        }))
      }.addPostPatchHook { (oldNode: VNode, node: VNode) =>
        // We need this in case some other StreamModifier updates the vnode (we will get this event)
        dom.console.warn(commonPrefix + ": CHILD PREPATCH")
        dom.console.log("text:", oldNode.text, node.text)
        dom.console.log("styles:", oldNode.data.styles, node.data.styles)
//        js.debugger()

        currentNode = node
      }.addDestroyHook { vnode =>
          dom.console.log(commonPrefix + ": --CHILD UNSUB-- ")
          subscription.unsubscribe() // @TODO this needs to be tested! Especially in nested destroys
      }
    }

    val hooks = addChildHooks(HookLogger(prefix = commonPrefix, callRemoveNode = true))

    currentNode.data.hooks = hooks

    currentNode
  }
}
