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
      .createElementNS(namespaceURI = svgNamespaceUri, qualifiedName = tag.name)
      .asInstanceOf[Ref]
  }

  def createMathMlElement(tag: MathMlTag): dom.MathMLElement = {
    dom.document
      .createElementNS(namespaceURI = mathmlNamespaceUri, qualifiedName = tag.name)
      .asInstanceOf[dom.MathMLElement]
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
    val elementTypeMatches = (node, tag) match {
      case (_: dom.html.Element, _: HtmlTag[_]) => true
      case (_: dom.svg.Element, _: SvgTag[_]) => true
      case _ => false
    }
    lazy val expectedElementTypeDesc = {
      tag match {
        case t: HtmlTag[_] => s"HTML <${t.name}>"
        case t: SvgTag[_] => s"SVG <${t.name}>"
        case t => s"Unknown element <${t.name}>"
      }
    }
    lazy val actualNodeTypeDesc = {
      node match {
        case n: dom.html.Element => s"HTML <${n.tagName}>"
        case n: dom.svg.Element => s"SVG <${n.tagName}>"
        case n: dom.Element => s"Unknown <${n.tagName}>"
        case n: dom.Comment => s"CommentNode <${n.text}>"
        case n: dom.Text => s"TextNode <${n.textContent}>"
        case n => s"Unknown node `${n.toString.take(20)}`"
      }
    }
    node match {
      case element: dom.Element if elementTypeMatches =>
        val tagNameMatches = tag.name.ew.toLowerCase() != element.tagName.ew.toLowerCase()
        if (tagNameMatches) {
          throw new Exception(s"$clue: expected $expectedElementTypeDesc, got different tag name: `$actualNodeTypeDesc`")
        } else {
          node.asInstanceOf[Ref]
        }
      case _ =>
        throw new Exception(s"$clue: expected $expectedElementTypeDesc, got different node type: `$actualNodeTypeDesc`")
    }
  }

  // Namespaces

  final def namespaceUri(namespace: String): String = {
    namespace match {
      case "svg" => svgNamespaceUri
      case "xlink" => xlinkNamespaceUri
      case "xml" => xmlNamespaceUri
      case "xmlns" => xmlnsNamespaceUri
      case "mathml" => mathmlNamespaceUri
      case _ => throw new Exception(s"Unknown namespace: ${namespace}")
    }
  }

  final val svgNamespaceUri: String = "http://www.w3.org/2000/svg"

  final val xlinkNamespaceUri: String = "http://www.w3.org/1999/xlink"

  final val xmlNamespaceUri: String = "http://www.w3.org/XML/1998/namespace"

  final val xmlnsNamespaceUri: String = "http://www.w3.org/2000/xmlns/"

  final val mathmlNamespaceUri: String = "http://www.w3.org/1998/Math/MathML"
}
