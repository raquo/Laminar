package com.raquo.laminar.receivers

import com.raquo.laminar.allTags.comment
import com.raquo.laminar.subscriptions.DynamicEventSubscription
import com.raquo.laminar.utils.GlobalCounter
import com.raquo.laminar.RNode
import com.raquo.xstream.XStream

import scala.scalajs.js

object ChildReceiver {

  def <--($node: XStream[RNode]): RNode = {
    // @TODO[Performance] Is there a better default for this? Some kind of generic empty node?
    val initialChildNode = comment()
    var childNode = initialChildNode
    var maybeSubscription: js.UndefOr[DynamicEventSubscription[RNode]] = js.undefined

    // About memory streams
    //
    // When a memory stream is subscribed to, it fires onNext synchronously, immediately.
    // This happens in `childNode.subscribe`, BEFORE we have a chance to return the newly
    // created subscription from that call and record it in `maybeSubscription`.
    //
    // So manually work around this in two ways:
    // - We update `childNode` in `onNext`
    // - We rewire the subscription AFTER we have a reference to it if childNode was updated
    // - We return the new, properly patched up `childNode` from `<--`


    def onNext(newChildNode: RNode, activeChildNode: RNode): RNode = {
      // We are updating the parent silently, in-place, without triggering any snabbdom events
      // because this does not represent a change in the parent node. This is a change in the
      // child node, and we deal with it ourselves by patch()-ing the child node directly in
      // `RNode.onNextEvent`

      if (maybeSubscription.isEmpty) {
        childNode = newChildNode // See notice about memory streams above
      } else {
        maybeSubscription.get.transfer(fromNode = activeChildNode, toNode = newChildNode)
      }

      newChildNode.activeParentNode = activeChildNode.activeParentNode
      newChildNode.activeParentNode.foreach { parentNode =>
        parentNode.maybeChildren.foreach { children =>
          // replace the old activeChildNode in parent's children with newChildNode
          val activeChildIndex = children.indexOf(activeChildNode)
          if (activeChildIndex != -1) {
            children.splice(activeChildIndex, 1, newChildNode)
          }
        }
      }

      newChildNode
    }

    childNode._debugNodeNumber = GlobalCounter.next().toString // because create hook is not called for comment nodes!

    maybeSubscription = childNode.subscribe[RNode]($node, onNext)
    if (childNode != initialChildNode) {
      maybeSubscription.get.transfer(initialChildNode, childNode) // See notice about memory streams above
    }

    childNode
  }
}
