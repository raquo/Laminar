package com.raquo.laminar.nodes

import com.raquo.airstream.ownership.DynamicOwner
import com.raquo.laminar.DomApi
import org.scalajs.dom

import scala.collection.mutable

trait ParentNode[+Ref <: dom.Element] extends ReactiveNode[Ref] {

  private[nodes] val dynamicOwner: DynamicOwner = new DynamicOwner

  // @TODO[Performance] We should get rid of this.
  //  - The only place where we need this is ReplaceAll functionality of ChildrenCommand API
  //  - We can probably track specific children affected by that API instead
  //  - That would save us mutable Buffer searches and manipulations when inserting and removing nodes
  private var _maybeChildren: Option[mutable.Buffer[ChildNode.Base]] = None

  @deprecated("ParentNode.maybeChildren will be removed in a future version of Laminar.", "0.8")
  @inline def maybeChildren: Option[List[ChildNode.Base]] = _maybeChildren.map(_.toList)
}

object ParentNode {

  type Base = ParentNode[dom.Element]

  // @Note End users, you should achieve your DOM manipulation goals using the many of <-- and --> methods that Laminar offers.
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
    val nextParent = Some(parent)
    child.willSetParent(nextParent)

    // 1. Update DOM
    val appended = DomApi.appendChild(parent = parent, child = child)
    if (appended) {

      // 2A. Update child's current parent node
      child.maybeParent.foreach { childParent =>
        childParent._maybeChildren.foreach(childParentChildren => childParentChildren -= child)
      }

      // 2B. Update this node
      if (parent._maybeChildren.isEmpty) {
        parent._maybeChildren = Some(mutable.Buffer(child))
      } else {
        parent._maybeChildren.foreach(children => children += child)
      }

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
    parent._maybeChildren.foreach { children =>

      // 0. Check precondition required for consistency of our own Tree vs real DOM
      val indexOfChild = children.indexOf(child)
      if (indexOfChild != -1) {
        child.willSetParent(None)

        // 1. Update DOM
        removed = DomApi.removeChild(parent = parent, child = child)
        if (removed) {

          // 2. Update this node
          children.remove(indexOfChild, count = 1)

          // 3. Update child
          child.setParent(None)
        }
      }
    }
    removed
  }

  /** Note: can also be used to move children, even within the same parent
    *
    * @return Whether child was successfully inserted
    */
  def insertChild(
    parent: ParentNode.Base,
    child: ChildNode.Base,
    atIndex: Int
  ): Boolean = {
    var inserted = false

    // 0. Prep this node
    if (parent._maybeChildren.isEmpty) {
      parent._maybeChildren = Some(mutable.Buffer())
    }

    parent._maybeChildren.foreach { children =>
      val nextParent = Some(parent)
      child.willSetParent(nextParent)

      // 1. Update DOM
      if (atIndex < children.length) {
        val nextChild = children.apply(atIndex)
        inserted = DomApi.insertBefore(
          parent = parent,
          newChild = child,
          referenceChild = nextChild
        )
      } else if (atIndex == children.length) {
        inserted = DomApi.appendChild(parent = parent, child = child)
      }

      if (inserted) {
        // 2A. Update child's current parent node
        child.maybeParent.foreach { childParent =>
          childParent._maybeChildren.foreach(childParentChildren => childParentChildren -= child)
        }

        // 2B. Update this node
        children.insert(atIndex, child)

        // 3. Update child
        child.setParent(nextParent)
      }
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
    parent._maybeChildren.foreach { children =>

      // 0. Check precondition required for consistency of our own Tree vs real DOM
      if (oldChild != newChild) {
        val indexOfChild = children.indexOf(oldChild)
        if (indexOfChild != -1) {
          val newChildNextParent = Some(parent)
          oldChild.willSetParent(None)
          newChild.willSetParent(newChildNextParent)

          // 1. Update DOM
          replaced = DomApi.replaceChild(
            parent = parent,
            newChild = newChild,
            oldChild = oldChild
          )

          // 2. Update this node
          children.update(indexOfChild, newChild)

          // 3. Update children
          oldChild.setParent(None)
          newChild.setParent(newChildNextParent)
        }
      }
    }
    replaced
  }

  /** Note: Does nothing if `fromIndex` or `toIndex` are out of bounds or if `fromIndex>toIndex`
    *
    * @return Whether children were replaced
    */
  def replaceChildren(
    parent: ParentNode.Base,
    fromIndex: Int,
    toIndex: Int,
    newChildren: Iterable[ChildNode.Base]
  ): Boolean = {
    // A note on efficiency of this method:
    //
    // Scala DOM Builder is not a virtual DOM, it has no concept of "child reconciliation" because
    // there are no virtual children to compare. If you're building a virtual DOM library on top
    // of Scala DOM builder, it's up to you to design a reconciliation algorithm that would call
    // more specific methods like insertChild / replaceChild / removeChild.
    //
    // Note that this method does not create any new DOM nodes. All the nodes that should
    // exist are already provided to it. All it does it detach from the DOM the old children
    // (within the given index bounds), and attach new children where the old children used to be.
    //
    // It is likely that a more refined algorithm could reduce the amount of DOM operations needed
    // for certain popular use cases, e.g. for reordering of the same items. And we should look
    // into that eventually, but not before it is proven with benchmarks that
    //
    // That said, we can probably improve this method's performance. Even though it doesn't create
    // any HTML elements unnecessarily (or at all) because they are provided to it

    // @TODO[Performance] introduce a reorderChildren() method to support efficient sorting?
    // @TODO[Integrity] This does not properly report failures like other methods do

    // 0. Prep this node
    if (parent._maybeChildren.isEmpty) {
      parent._maybeChildren = Some(mutable.Buffer())
    }

    var replaced = false
    parent._maybeChildren.foreach { children =>
      if (
        newChildren != children
          && fromIndex >= 0 && fromIndex < children.length
          && toIndex >= 0 && toIndex < children.length
          && fromIndex <= toIndex
      ) {
        replaced = true

        // A. Remove existing children
        var numRemovedNodes = 0
        val numNodesToRemove = toIndex - fromIndex + 1
        while (numRemovedNodes < numNodesToRemove) {
          removeChild(parent, children(fromIndex))
          numRemovedNodes += 1
        }

        // B. Insert new children
        var insertedCount = 0
        newChildren.foreach { newChild =>
          insertChild(
            parent,
            newChild,
            atIndex = fromIndex + insertedCount
          )
          insertedCount += 1
        }
      }
    }
    replaced
  }

  def replaceAllChildren(
    parent: ParentNode.Base,
    newChildren: Iterable[ChildNode.Base]
  ): Unit = {
    // @TODO[Performance] This could be optimized
    // @TODO[Integrity] This does not properly report failures like other methods do

    // A. Remove existing children
    parent._maybeChildren.foreach { children =>
      children.foreach(child => removeChild(parent, child))
    }

    // B. Add new children
    newChildren.foreach(child => appendChild(parent, child))
  }

  def indexOfChild(
    parent: ParentNode.Base,
    child: ChildNode.Base
  ): Int = {
    parent._maybeChildren.map(children => children.indexOf(child)).getOrElse(-1)
  }
}
