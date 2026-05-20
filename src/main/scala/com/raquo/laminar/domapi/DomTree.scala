package com.raquo.laminar.domapi

import com.raquo.airstream.core.AirstreamError
import com.raquo.laminar.inserters.InserterHooks
import com.raquo.laminar.nodes.{ChildNode, ParentNode}
import org.scalajs.dom

import scala.scalajs.js.|

trait DomTree {

  protected val raw: DomTreeRaw

  /** If true, Laminar will report exceptions thrown by DOM operations
    * to Airstream unhandled errors. Otherwise, they will be swallowed.
    *
    * These exceptions happen when attempting to perform invalid DOM
    * operations, such as trying to make an element its own child.
    *
    * Prior to v18.0.0, Laminar would always swallow these exceptions.
    *
    * Generally, this setting should not be disabled except temporarily
    * (e.g. to help with migration / debugging / testing).
    */
  var shouldReportDomErrors: Boolean = true

  private def maybeReportDomError(e: dom.DOMException): Unit = {
    if (shouldReportDomErrors) {
      AirstreamError.sendUnhandledError(new DomError(e))
    }
  }

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
    hooks: InserterHooks | Unit
  ): Boolean = {
    val nextParent = Some(parent)
    child.willSetParent(nextParent)

    // 1. Update DOM
    hooks.foreach(_.onWillInsertNode(parent = parent, child = child))
    val maybeDomError = raw.appendChild(parent = parent.ref, child = child.ref)
    maybeDomError.foreach(maybeReportDomError)
    val appended = maybeDomError.isEmpty
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
      val maybeDomError = raw.removeChild(parent = parent.ref, child = child.ref)
      maybeDomError.foreach(maybeReportDomError)
      removed = maybeDomError.isEmpty
      if (removed) {
        child.setParent(None)
      }
    }
    removed
  }

  def insertChildBefore(
    parent: ParentNode.Base,
    newChild: ChildNode.Base,
    referenceChild: ChildNode.Base,
    hooks: InserterHooks | Unit
  ): Boolean = {
    val nextParent = Some(parent)
    newChild.willSetParent(nextParent)
    hooks.foreach(_.onWillInsertNode(parent = parent, child = newChild))
    val maybeDomError = raw.insertBefore(
      parent = parent.ref,
      newChild = newChild.ref,
      referenceChild = referenceChild.ref
    )
    maybeDomError.foreach(maybeReportDomError)
    val inserted = maybeDomError.isEmpty
    if (inserted) {
      newChild.setParent(nextParent)
    }
    inserted
  }

  def insertChildAfter(
    parent: ParentNode.Base,
    newChild: ChildNode.Base,
    referenceChild: ChildNode.Base,
    hooks: InserterHooks | Unit
  ): Boolean = {
    val nextParent = Some(parent)
    newChild.willSetParent(nextParent)
    hooks.foreach(_.onWillInsertNode(parent = parent, child = newChild))
    val maybeDomError = raw.insertAfter(
      parent = parent.ref,
      newChild = newChild.ref,
      referenceChild = referenceChild.ref
    )
    maybeDomError.foreach(maybeReportDomError)
    val inserted = maybeDomError.isEmpty
    if (inserted) {
      newChild.setParent(nextParent)
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
    index: Int,
    hooks: InserterHooks | Unit
  ): Boolean = {
    var inserted = false
    val nextParent = Some(parent)

    child.willSetParent(nextParent)
    hooks.foreach(_.onWillInsertNode(parent = parent, child = child))

    val children = parent.ref.childNodes
    val maybeDomError = if (index < children.length) {
      val referenceChild = children(index)
      raw.insertBefore(
        parent = parent.ref,
        newChild = child.ref,
        referenceChild = referenceChild
      )
    } else {
      raw.appendChild(parent = parent.ref, child = child.ref)
    }
    maybeDomError.foreach(maybeReportDomError)
    inserted = maybeDomError.isEmpty

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
    hooks: InserterHooks | Unit
  ): Boolean = {
    var replaced = false
    if (oldChild ne newChild) {
      if (oldChild.maybeParent.contains(parent)) {
        val newChildNextParent = Some(parent)

        oldChild.willSetParent(None)
        newChild.willSetParent(newChildNextParent)
        hooks.foreach(_.onWillInsertNode(parent = parent, child = newChild))

        val maybeDomError = raw.replaceChild(
          parent = parent.ref,
          newChild = newChild.ref,
          oldChild = oldChild.ref
        )
        maybeDomError.foreach(maybeReportDomError)
        replaced = maybeDomError.isEmpty

        if (replaced) {
          oldChild.setParent(None)
          newChild.setParent(newChildNextParent)
        }
      }
    }
    replaced
  }
}
