package com.raquo.laminar.nodes

import com.raquo.laminar.DomApi
import org.scalajs.dom

import scala.scalajs.js

/** RootNode will mount itself (and the child) if the container node
  * is attached to the DOM at RootNode initialization time.
  *
  * Note: RootNode does not receive any outside notifications about
  * the container being attached or detached from the DOM.
  *
  * If you are trying to create a Laminar RootNode inside a
  * React.js component, make sure to call:
  * - mount() when componentDidMount is due, and
  * - unmount() when componentWillUnmount is due.
  *
  * Other libraries' integration follows the same principle.
  */
class RootNode(
  val container: dom.Element,
  val child: ReactiveElement.Base
) extends ParentNode[dom.Element] {

  if (container == null) {
    throw new Exception("Unable to mount Laminar RootNode into a null container. See https://laminar.dev/documentation#waiting-for-the-dom-to-load")
  }

  if (!DomApi.isDescendantOf(container, dom.document)) {
    throw new Exception("Unable to mount Laminar RootNode into an unmounted container. See https://laminar.dev/documentation#rendering")
  }

  /** When we create a Root, we don't want to create a new HTML Element, we want to
    * use a reference to an existing element, the container.
    */
  final override val ref: dom.Element = container

  mount()

  /** @return Whether child was successfully mounted */
  def mount(): Boolean = {
    dynamicOwner.activate()
    ParentNode.appendChild(parent = this, child, hooks = js.undefined)
  }

  /** @return Whether child was successfully unmounted */
  def unmount(): Boolean = {
    dynamicOwner.deactivate()
    ParentNode.removeChild(parent = this, child = child)
  }

}
