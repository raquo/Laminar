package com.raquo.laminar.domapi

import org.scalajs.dom

import scala.annotation.tailrec
import scala.scalajs.js
import scala.scalajs.js.JavaScriptException

trait DomTreeRaw {

  def appendChild(
    parent: dom.Node,
    child: dom.Node
  ): Boolean = {
    try {
      parent.appendChild(child)
      true
    } catch {
      // @TODO[Integrity] Does this only catch DOM exceptions? (here and in other methods)
      case JavaScriptException(_: dom.DOMException) => false
    }
  }

  def removeChild(
    parent: dom.Node,
    child: dom.Node
  ): Boolean = {
    try {
      parent.removeChild(child)
      true
    } catch {
      case JavaScriptException(_: dom.DOMException) => false
    }
  }

  def insertBefore(
    parent: dom.Node,
    newChild: dom.Node,
    referenceChild: dom.Node
  ): Boolean = {
    try {
      parent.insertBefore(newChild = newChild, refChild = referenceChild)
      true
    } catch {
      case JavaScriptException(_: dom.DOMException) => false
    }
  }

  def insertAfter(
    parent: dom.Node,
    newChild: dom.Node,
    referenceChild: dom.Node
  ): Boolean = {
    try {
      // Note: parent.insertBefore correctly handles the case of `refChild == null`
      parent.insertBefore(newChild = newChild, refChild = referenceChild.nextSibling)
      true
    } catch {
      case JavaScriptException(_: dom.DOMException) => false
    }
  }

  def replaceChild(
    parent: dom.Node,
    newChild: dom.Node,
    oldChild: dom.Node
  ): Boolean = {
    try {
      parent.replaceChild(newChild = newChild, oldChild = oldChild)
      true
    } catch {
      case JavaScriptException(_: dom.DOMException) => false
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
