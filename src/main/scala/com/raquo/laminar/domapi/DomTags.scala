package com.raquo.laminar.domapi

import com.raquo.ew._
import com.raquo.laminar.keys.SvgAttr
import com.raquo.laminar.nodes._
import com.raquo.laminar.tags._
import org.scalajs.dom

import scala.scalajs.js

trait DomTags {

  //
  // Elements
  //

  def createHtmlElement[Ref <: dom.html.Element](tag: HtmlTag[Ref]): Ref = {
    dom.document.createElement(tag.name).asInstanceOf[Ref]
  }

  def createSvgElement[Ref <: dom.svg.Element](tag: SvgTag[Ref]): Ref = {
    dom.document
      .createElementNS(namespaceURI = SvgAttr.svgNamespaceUri, qualifiedName = tag.name)
      .asInstanceOf[Ref]
  }

  //
  // Comment Nodes
  //

  def createCommentNode(text: String): dom.Comment = dom.document.createComment(text)

  def setCommentNodeText(node: CommentNode, text: String): Unit = {
    node.ref.textContent = text
  }

  //
  // Text Nodes
  //

  def createTextNode(text: String): dom.Text = dom.document.createTextNode(text)

  def setTextNodeText(node: TextNode, text: String): Unit = {
    node.ref.textContent = text
  }

  //
  // Custom Elements
  //

  def isCustomElement(element: dom.Element): Boolean = {
    element.tagName.contains('-')
  }

  //
  // Assertions
  //

  def assertSingleNode(
    nodes: js.Array[dom.Node],
    clue: String
  ): dom.Node = {
    if (nodes.length != 1) {
      throw new Exception(s"$clue: expected exactly 1 element, got ${nodes.length}")
    }
    nodes(0)
  }

  def assertHtmlElement(node: dom.Node, clue: String = "Error"): dom.html.Element = {
    node match {
      case element: dom.html.Element =>
        element
      case _ =>
        throw new Exception(s"$clue: expected HTML element, got non-element node: ${node.nodeName}")
    }
  }

  def assertSvgElement(node: dom.Node, clue: String = "Error"): dom.svg.Element = {
    node match {
      case element: dom.svg.Element =>
        element
      case _ =>
        throw new Exception(s"$clue: expected SVG element, got non-element node: ${node.nodeName}")
    }
  }

  def assertTagMatches[Ref <: dom.Element](
    tag: Tag[ReactiveElement[Ref]],
    node: dom.Node,
    clue: String = "Error"
  ): Ref = {
    node match {
      case element: dom.Element =>
        if (tag.name.ew.toLowerCase() != element.tagName.ew.toLowerCase()) {
          throw new Exception(s"$clue: expected tag name `${tag.name}`, got `${element.tagName}`")
        } else {
          node.asInstanceOf[Ref]
        }
      case _ =>
        throw new Exception(s"$clue: expected element, got non-element node: ${node.nodeName}")
    }
  }
}
