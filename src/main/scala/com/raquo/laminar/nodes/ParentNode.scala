package com.raquo.laminar.nodes

import com.raquo.airstream.ownership.DynamicOwner
import com.raquo.laminar.DomApi
import org.scalajs.dom

trait ParentNode[+Ref <: dom.Element] extends ReactiveNode[Ref] {

  private[nodes] val dynamicOwner: DynamicOwner = new DynamicOwner(() => {
    val path = DomApi.debugPath(ref).mkString(" > ")
    throw new Exception(s"Attempting to use owner of unmounted element: $path")
  })

}

object ParentNode {

  type Base = ParentNode[dom.Element]

  // @Note End users, you should achieve your DOM manipulation goals using the many <-- and --> methods that Laminar offers.
  //  Those arrow methods are in fact very flexible. Only use the methods below when really needed, and even then, very carefully.
  //  The methods below are safe to use IFF you're not doing crazy stuff.
  //  - For example, don't mess with nodes managed by any other Laminar code such as `child <-- ...` or `children <-- ...`.
  //  - Also, avoid inserting / removing / moving RELEVANT nodes while any other DOM update operation is in process
  //    - You don't want to affect the DOM subtree that is being modified with your own ad hoc modifications
  //    - It should be ok to update UNRELATED nodes though

  /** Note: can also be used to move children, even within the same parent
    *
    * @return Whether child was successfully appended
    */
  def appendChild(
    parent: ParentNode.Base,
    child: ChildNode.Base
  ): Boolean = {
    val appended = DomApi.appendChild(parent = parent.ref, child = child.ref)
    if (appended) {
      child.setParent(Some(parent))
    }
    appended
  }

  /** @return Whether child was successfully removed */
  def removeChild(
    parent: ParentNode.Base,
    child: ChildNode.Base
  ): Boolean = {
    val removed = DomApi.removeChild(parent = parent.ref, child = child.ref)
    if (removed) {
      child.setParent(None)
    }
    removed
  }

  def insertChildBefore(
    parent: ParentNode.Base,
    newChild: ChildNode.Base,
    referenceChild: ChildNode.Base
  ): Boolean = {
    val inserted = DomApi.insertBefore(
      parent = parent.ref,
      newChild = newChild.ref,
      referenceChild = referenceChild.ref
    )
    if (inserted) {
      newChild.setParent(Some(parent))
    }
    inserted
  }

  def insertChildAfter(
    parent: ParentNode.Base,
    newChild: ChildNode.Base,
    referenceChild: ChildNode.Base
  ): Boolean = {
    val inserted = DomApi.insertAfter(
      parent = parent.ref,
      newChild = newChild.ref,
      referenceChild = referenceChild.ref
    )
    if (inserted) {
      newChild.setParent(Some(parent))
    }
    inserted
  }

  /** Note: this method can also be used to move children,
    * even within the same parent
    *
    * @return Whether child was successfully inserted
    */
  def insertChildAtIndex(
    parent: ParentNode.Base,
    child: ChildNode.Base,
    index: Int
  ): Boolean = {
    val children = parent.ref.childNodes
    val inserted = if (index < children.length) {
      val referenceChild = children(index)
      DomApi.insertBefore(
        parent = parent.ref,
        newChild = child.ref,
        referenceChild = referenceChild
      )
    } else {
      DomApi.appendChild(parent = parent.ref, child = child.ref)
    }
    if (inserted) {
      child.setParent(Some(parent))
    }
    inserted
  }

  /** Note: Does nothing if `oldChild` was not found in parent's children, or if `oldChild==newChild`
    *
    * @return Whether child was replaced
    */
  def replaceChild(
    parent: ParentNode.Base,
    oldChild: ChildNode.Base,
    newChild: ChildNode.Base
  ): Boolean = {
    var replaced = false
    if (oldChild ne newChild) {
      if (oldChild.maybeParent.contains(parent)) {
        replaced = DomApi.replaceChild(
          parent = parent.ref,
          newChild = newChild.ref,
          oldChild = oldChild.ref
        )
        if (replaced) {
          oldChild.setParent(None)
          newChild.setParent(Some(parent))
        }
      }
    }
    replaced
  }
}
