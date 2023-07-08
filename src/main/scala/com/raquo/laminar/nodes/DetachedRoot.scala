package com.raquo.laminar.nodes

/** This class lets you manually manage the lifecycle of a single
  * Laminar element. This is useful when you need to pass a DOM
  * element to a third party JS library, and you want that element
  * to be managed by Laminar.
  */
class DetachedRoot[+El <: ReactiveElement.Base](
  val node: El,
  activateNow: Boolean
) {

  if (activateNow) {
    activate()
  }

  @inline def ref: node.ref.type = node.ref

  /** Start the element's subscriptions, as if it was mounted. */
  def activate(): Unit = {
    node.dynamicOwner.activate()
  }

  /** Stop the element's subscriptions, as if it was unmounted. */
  def deactivate(): Unit = {
    node.dynamicOwner.deactivate()
  }

  /** Returns true if the element's subscriptions are currently active. */
  def isActive: Boolean = {
    node.dynamicOwner.isActive
  }
}
