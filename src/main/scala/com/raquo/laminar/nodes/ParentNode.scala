package com.raquo.laminar.nodes

import com.raquo.airstream.ownership.DynamicOwner
import com.raquo.laminar.DomApi
import com.raquo.laminar.inserters.InserterHooks
import org.scalajs.dom

import scala.scalajs.js

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
    child: ChildNode.Base,
    hooks: js.UndefOr[InserterHooks]
  ): Boolean = {
    val nextParent = Some(parent)
    child.willSetParent(nextParent)

    // 1. Update DOM
    hooks.foreach(_.onWillInsertNode(parent = parent, child = child))
    val appended = DomApi.appendChild(parent = parent.ref, child = child.ref)
    if (appended) {
      // 3. Update child
      child.setParent(nextParent)
    }
    appended
  }

  /** @return Whether child was successfully removed */
  def removeChild(
    parent: ParentNode.Base,
    child: ChildNode.Base
  ): Boolean = {
    var removed = false
    if (child.ref.parentNode == parent.ref) {
      child.willSetParent(None)
      removed = DomApi.removeChild(parent = parent.ref, child = child.ref)
      child.setParent(None)
    }
    removed
  }

  def insertChildBefore(
    parent: ParentNode.Base,
    newChild: ChildNode.Base,
    referenceChild: ChildNode.Base,
    hooks: js.UndefOr[InserterHooks]
  ): Boolean = {
    val nextParent = Some(parent)
    newChild.willSetParent(nextParent)
    hooks.foreach(_.onWillInsertNode(parent = parent, child = newChild))
    val inserted = DomApi.insertBefore(
      parent = parent.ref,
      newChild = newChild.ref,
      referenceChild = referenceChild.ref
    )
    newChild.setParent(nextParent)
    inserted
  }

  def insertChildAfter(
    parent: ParentNode.Base,
    newChild: ChildNode.Base,
    referenceChild: ChildNode.Base,
    hooks: js.UndefOr[InserterHooks]
  ): Boolean = {
    val nextParent = Some(parent)
    newChild.willSetParent(nextParent)
    hooks.foreach(_.onWillInsertNode(parent = parent, child = newChild))
    val inserted = DomApi.insertAfter(
      parent = parent.ref,
      newChild = newChild.ref,
      referenceChild = referenceChild.ref
    )
    newChild.setParent(nextParent)
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
    index: Int,
    hooks: js.UndefOr[InserterHooks]
  ): Boolean = {
    var inserted = false
    val nextParent = Some(parent)

    child.willSetParent(nextParent)
    hooks.foreach(_.onWillInsertNode(parent = parent, child = child))

    val children = parent.ref.childNodes
    inserted = if (index < children.length) {
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
      child.setParent(nextParent)
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
    newChild: ChildNode.Base,
    hooks: js.UndefOr[InserterHooks]
  ): Boolean = {
    var replaced = false
    if (oldChild ne newChild) {
      if (oldChild.maybeParent.contains(parent)) {
        val newChildNextParent = Some(parent)

        oldChild.willSetParent(None)
        newChild.willSetParent(newChildNextParent)
        hooks.foreach(_.onWillInsertNode(parent = parent, child = newChild))

        replaced = DomApi.replaceChild(
          parent = parent.ref,
          newChild = newChild.ref,
          oldChild = oldChild.ref
        )

        if (replaced) {
          oldChild.setParent(None)
          newChild.setParent(newChildNextParent)
        }
      }
    }
    replaced
  }
}
