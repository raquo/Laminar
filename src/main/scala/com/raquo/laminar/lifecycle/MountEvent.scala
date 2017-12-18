package com.raquo.laminar.lifecycle

// @TODO update the comment about cascading when end users will be allowed to discard nodes manually
/** Mount Events can inform you about certain stages of the Node's lifecycle,
  * letting you perform the required initialization / cleanup.
  *
  * They are also used internally for managing the lifecycle of stream subscriptions.
  *
  * A Node is considered mounted if it's present in the DOM, i.e. it has `dom.document`
  * among its ancestors. A node that is not mounted is considered unmounted.
  *
  * All MountEvent-s are cascading: if a certain event is fired on a node, it will also
  * fire on all descendants of that node.
  */
sealed trait MountEvent

/** `NodeDidMount` event fires when a previously unmounted (or newly created) node becomes
  * mounted.
  *
  * Internally, Laminar will activate the node's subscriptions when it's mounted.
  */
object NodeDidMount extends MountEvent

/** `NodeWillUnmount` event fires when a previously mounted node is about to be unmounted.
  * Note: when the even fires the node is still mounted.
  *
  * Internally, Laminar will deactivate the node's subscriptions when it's unmounted,
  * except for the internal subscription that listens to $mountEvent (so that we can
  * re-activate the other subscriptions when the node gets mounted again).
  *
  * Note: currently, every `NodeWillUnmount` event is followed by a `NodeWillBeDiscarded`
  * event which deactivates the subscription that listens to $mountEvent
  */
object NodeWillUnmount extends MountEvent

// @TODO Provide a way to avoid discarding nodes (and how do we manually discard them then? Discard when parent is discarded...? Think about it and update the docs before implementing)
// @TODO Think about the inheritance mechanics of discarding, and document all this
/** `NodeWillBeDiscarded` event fires immediately after `NodeWillUnmount` unless the end
  * user specified that the node should not be discarded when it's unmounted.
  *
  * A discarded node is defined as a node that is unmounted and will not be mounted again.
  * The latter is a promise by the end user, not enforced by Laminar.
  *
  * Internally, Laminar will deactivate the node's subscription that listens to $mountEvent
  * when the node discarded. Note that by this point the node is already unmounted, so all
  * other subscriptions have already been deactivated by Laminar in response to a
  * NodeWillUnmount event. This means that the node's subscriptions will not re-activate if
  * the node is mounted again (because the node will not be aware of this).
  *
  * Note: There currently is no way for the end user to specify that the node should not be
  * discarded, so the end user implicitly promises to not re-mount nodes which have
  * been unmounted.
  */
object NodeWillBeDiscarded extends MountEvent
