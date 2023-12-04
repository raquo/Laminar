package com.raquo.laminar.nodes

import com.raquo.laminar.modifiers.Modifier
import org.scalajs.dom

import scala.annotation.tailrec
import scala.scalajs.js

trait ChildNode[+Ref <: dom.Node]
  extends ReactiveNode[Ref]
  with Modifier[ReactiveElement[dom.Element]] {

  private var _maybeParent: Option[ParentNode.Base] = None

  def maybeParent: Option[ParentNode.Base] = _maybeParent

  /** Note: Make sure to call [[willSetParent]] before calling this method manually */
  private[nodes] def setParent(maybeNextParent: Option[ParentNode.Base]): Unit = {
    _maybeParent = maybeNextParent
  }

  /** This is called as a notification, BEFORE changes to the real DOM or to the Scala DOM tree are applied.
    * - Corollary: When this is called, this node's maybeParent reference has not been updated yet.
    *
    * Default implementation is a noop. You can override this to implement DOM lifecycle hooks similar to
    * React's `componentWillUnmount`.
    *
    * Note: This method is NOT automatically called inside [[setParent]] because [[setParent]] is called
    *       AFTER the real DOM was modified. Therefore, IF you call [[setParent]] directly, you need to
    *       also call [[willSetParent]] before that, if you plan to implement that method. However, if you
    *       only call [[setParent]] indirectly, via the methods defined in [[ParentNode]], those methods
    *       take care of calling [[willSetParent]] for you.
    *
    * @param maybeNextParent  `None` means this node is about to be detached form its parent
    */
  @inline private[nodes] def willSetParent(maybeNextParent: Option[ParentNode.Base]): Unit = ()

  override def apply(parentNode: ReactiveElement.Base): Unit = {
    ParentNode.appendChild(parent = parentNode, child = this, hooks = js.undefined)
  }

}

object ChildNode {

  type Base = ChildNode[dom.Node]

  /** Note: This walks up Laminar's element tree, not the real DOM tree.
    * See [[com.raquo.laminar.DomApi.isDescendantOf]] if you want to check the real DOM tree.
    */
  @tailrec final def isDescendantOf(
    child: ChildNode.Base,
    parent: ParentNode.Base
  ): Boolean = {
    child.maybeParent match {
      case Some(`parent`) => true
      case Some(intermediateParent: ChildNode.Base) => isDescendantOf(intermediateParent, parent)
      case _ => false
    }
  }
}
