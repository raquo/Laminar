package com.raquo.laminar.nodes

import com.raquo.laminar.modifiers.Modifier
import org.scalajs.dom

import scala.annotation.tailrec

trait ChildNode[+Ref <: dom.Node]
  extends ReactiveNode[Ref]
  with Modifier[ReactiveElement[dom.Element]] {

  private var _maybeParent: Option[ParentNode.Base] = None

  def maybeParent: Option[ParentNode.Base] = _maybeParent

  /** Note: ReactiveElement overrides this with dynamicOwner logic. */
  private[nodes] def setParent(maybeNextParent: Option[ParentNode.Base]): Unit = {
    _maybeParent = maybeNextParent
  }

  override def apply(parentNode: ReactiveElement.Base): Unit = {
    ParentNode.appendChild(parent = parentNode, child = this)
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
