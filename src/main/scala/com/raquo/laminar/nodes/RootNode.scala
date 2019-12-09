package com.raquo.laminar.nodes

import org.scalajs.dom

class RootNode(
  val container: dom.Element,
  val child: ChildNode[dom.Element]
) extends ParentNode[dom.Element] {

  /** When we create a Root, we don't want to create a new HTML Element, we want to
    * use a reference to an existing element, the container.
    */
  override val ref: dom.Element = container

  /** @return Whether child was successfully unmounted */
  def unmount(): Boolean = {
    this.removeChild(child)
  }

  appendChild(child)
}
