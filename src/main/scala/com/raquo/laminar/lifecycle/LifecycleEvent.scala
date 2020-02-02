package com.raquo.laminar.lifecycle

// @TODO update the comment about cascading when end users will be allowed to discard nodes manually
/** Mount Events can inform you about certain stages of the Node's lifecycle,
  * letting you perform the required initialization / cleanup.
  *
  * They are also used internally for managing the lifecycle of element subscriptions.
  *
  * A Node is considered mounted if it's present in the DOM, i.e. it has `dom.document`
  * among its ancestors. A node that is not mounted is considered unmounted.
  *
  * All MountEvent-s are cascading: if a certain event is fired on a node, it will also
  * fire on all descendants of that node.
  *
  * Important: All MountEvent-s are emitted AFTER the current Airstream transaction completes.
  * That means that any code that listens to those events including all element subscriptions
  * will be run AFTER whatever event caused the element to get mounted has finished propagating.
  * So don't expect to fire an event, mount an element in response to that event, and have one
  * of this new element's subscriptions also react to the same event. The subscription will not see
  * that event. See the ignored test in WeirdCasesSpec.
  */
sealed trait LifecycleEvent

/** `NodeDidMount` event fires when a previously unmounted (or newly created) node becomes
  * mounted.
  *
  * Internally, Laminar will activate the node's subscriptions when it's mounted.
  */
object NodeDidMount extends LifecycleEvent

/** `NodeWillUnmount` event fires when a previously mounted node is about to be unmounted.
  * Note: when the event fires, the node is still mounted.
  *
  * Internally, Laminar will deactivate the node's subscriptions when it's unmounted,
  * except for the internal pilot subscription that listens to mountEvents (so that we can
  * re-activate the other subscriptions when the node gets mounted again).
  */
object NodeWillUnmount extends LifecycleEvent

