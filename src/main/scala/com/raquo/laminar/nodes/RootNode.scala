package com.raquo.laminar.nodes

import org.scalajs.dom

/**
  * We assume that RootNode's `container` element is always mounted.
  * RootNode does not receive outside notifications about the container becoming unmounted.
  */
class RootNode(
  val container: dom.Element,
  val child: ReactiveElement.Base
) extends ParentNode[dom.Element] {

  if (!ChildNode.isNodeMounted(container)) {
    throw new Exception("Unable to mount Laminar RootNode into an unmounted container.")
  }

  /** When we create a Root, we don't want to create a new HTML Element, we want to
    * use a reference to an existing element, the container.
    */
  override val ref: dom.Element = container

  /** @return Whether child was successfully unmounted */
  def unmount(): Boolean = {
    dynamicOwner.deactivate()
    this.removeChild(child)
  }

  dynamicOwner.activate()
  appendChild(child)
}
