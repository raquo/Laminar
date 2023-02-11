package com.raquo.laminar.nodes

import com.raquo.airstream.ownership.DynamicOwner
import com.raquo.ew._
import com.raquo.laminar.DomApi
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

trait ParentNode[+Ref <: dom.Element] extends ReactiveNode[Ref] {

  private[nodes] val dynamicOwner: DynamicOwner = new DynamicOwner(() => {
    val path = DomApi.debugPath(ref).mkString(" > ")
    throw new Exception(s"Attempting to use owner of unmounted element: $path")
  })

  // @TODO[Performance] We could get rid of this.
  //  - The only place where we really need this is ReplaceAll functionality of ChildrenCommand API
  //  - We can probably track specific children affected by that API instead
  //  - That would save us mutable Buffer searches and manipulations when inserting and removing nodes
  //  - I mean we could look at the real DOM nodes instead where needed. Would that be more efficient? We'd need to benchmark.
  private var _maybeChildren: js.UndefOr[JsArray[ChildNode.Base]] = js.undefined
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
    val nextParent = Some(parent)
    child.willSetParent(nextParent)

    // 1. Update DOM
    val appended = DomApi.appendChild(parent = parent, child = child)
    if (appended) {

      // 2A. Update child's current parent node
      child.maybeParent.foreach { childParent =>
        childParent._maybeChildren.foreach { children =>
          val ix = children.indexOf(child)
          children.splice(ix, deleteCount = 1)
        }
      }

      // 2B. Update this node
      if (parent._maybeChildren.isEmpty) {
        parent._maybeChildren = JsArray(child)
      } else {
        parent._maybeChildren.foreach(children => children.push(child))
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
          children.splice(indexOfChild, deleteCount = 1)

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
      parent._maybeChildren = JsArray[ChildNode.Base]()
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
          childParent._maybeChildren.foreach { children =>
            val ix = children.indexOf(child)
            children.splice(ix, deleteCount = 1)
          }
        }

        // 2B. Update this node
        children.splice(atIndex, deleteCount = 0, child)

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
    // 0. Check precondition required for consistency of our own Tree vs real DOM
    if (oldChild ne newChild) { // #TODO[API] Can we move this check outside of replaceChild? Or will something break? Sometimes this check is extraneous.
      parent._maybeChildren.foreach { children =>
        val indexOfOldChild = children.indexOf(oldChild)
        if (indexOfOldChild != -1) {
          val newChildNextParent = Some(parent)
          oldChild.willSetParent(None)
          newChild.willSetParent(newChildNextParent)

          // 1. Update DOM
          replaced = DomApi.replaceChild(
            parent = parent,
            newChild = newChild,
            oldChild = oldChild
          )

          if (replaced) {
            // 2A. Update new child's current parent node
            newChild.maybeParent.foreach { childParent =>
              childParent._maybeChildren.foreach { children =>
                val ix = children.indexOf(newChild)
                children.splice(ix, deleteCount = 1)
              }
            }

            // 2B. Update this node
            children.update(indexOfOldChild, newChild)

            // 3. Update children
            oldChild.setParent(None)
            newChild.setParent(newChildNextParent)
          }
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

    val newChildrenArr = newChildren.toJSArray.ew

    // 0. Prep this node
    if (parent._maybeChildren.isEmpty) {
      parent._maybeChildren = JsArray[ChildNode.Base]()
    }

    var replaced = false
    parent._maybeChildren.foreach { children =>
      if (
        newChildrenArr != children
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
        newChildrenArr.forEach { newChild =>
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
      children.forEach(child => removeChild(parent, child))
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
