package com.raquo.laminar.domapi

import org.scalajs.dom

import scala.annotation.tailrec
import scala.scalajs.js
import scala.scalajs.js.JavaScriptException

trait DomTreeRaw {

  def appendChild(
    parent: dom.Node,
    child: dom.Node
  ): Option[dom.DOMException] = {
    try {
      parent.appendChild(child)
      None
    } catch {
      // @TODO[Integrity] Does this only catch DOM exceptions? (here and in other methods)
      case JavaScriptException(e: dom.DOMException) => Some(e)
    }
  }

  def removeChild(
    parent: dom.Node,
    child: dom.Node
  ): Option[dom.DOMException] = {
    try {
      parent.removeChild(child)
      None
    } catch {
      case JavaScriptException(e: dom.DOMException) => Some(e)
    }
  }

  def insertBefore(
    parent: dom.Node,
    newChild: dom.Node,
    referenceChild: dom.Node
  ): Option[dom.DOMException] = {
    try {
      parent.insertBefore(newChild = newChild, refChild = referenceChild)
      None
    } catch {
      case JavaScriptException(e: dom.DOMException) => Some(e)
    }
  }

  def insertAfter(
    parent: dom.Node,
    newChild: dom.Node,
    referenceChild: dom.Node
  ): Option[dom.DOMException] = {
    try {
      // Note: parent.insertBefore correctly handles the case of `refChild == null`
      parent.insertBefore(newChild = newChild, refChild = referenceChild.nextSibling)
      None
    } catch {
      case JavaScriptException(e: dom.DOMException) => Some(e)
    }
  }

  def replaceChild(
    parent: dom.Node,
    newChild: dom.Node,
    oldChild: dom.Node
  ): Option[dom.DOMException] = {
    try {
      parent.replaceChild(newChild = newChild, oldChild = oldChild)
      None
    } catch {
      case JavaScriptException(e: dom.DOMException) => Some(e)
    }
  }

  //
  // Tree query functions
  //

  def indexOfChild(
    parent: dom.Node,
    child: dom.Node
  ): Int = {
    parent.childNodes.indexOf(child)
  }

  /** Note: This walks up the real DOM element tree, not the Laminar DOM tree.
    * See ChildNode.isDescendantOf if you want to walk up Laminar's tree instead.
    */
  @tailrec final def isDescendantOf(node: dom.Node, ancestor: dom.Node): Boolean = {
    // @TODO[Performance] Maybe use https://developer.mozilla.org/en-US/docs/Web/API/Node/contains instead (but IE only supports it for Elements)
    // For children of shadow roots, parentNode is null, but the host property contains a reference to the shadow root
    val effectiveParentNode = if (node.parentNode != null) {
      node.parentNode
    } else {
      val maybeShadowHost = node.asInstanceOf[js.Dynamic].selectDynamic("host").asInstanceOf[js.UndefOr[dom.Node]]
      maybeShadowHost.orNull
    }
    effectiveParentNode match {
      case null => false
      case `ancestor` => true
      case intermediateParent => isDescendantOf(intermediateParent, ancestor)
    }
  }
}
