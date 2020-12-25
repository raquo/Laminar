package com.raquo.laminar

import com.raquo.domtypes.generic.keys.{HtmlAttr, Prop, Style, SvgAttr}
import com.raquo.laminar.nodes.{ChildNode, CommentNode, ParentNode, ReactiveElement, ReactiveHtmlElement, ReactiveSvgElement, TextNode}
import com.raquo.laminar.modifiers.EventPropBinder
import org.scalajs.dom
import org.scalajs.dom.DOMException

import scala.annotation.tailrec
import scala.scalajs.js
import scala.scalajs.js.{JavaScriptException, |}

object DomApi {

  /** Tree functions */

  def appendChild(
    parent: ParentNode.Base,
    child: ChildNode.Base
  ): Boolean = {
    try {
      parent.ref.appendChild(child.ref)
      true
    } catch {
      // @TODO[Integrity] Does this only catch DOM exceptions? (here and in other methods)
      case JavaScriptException(_: DOMException) => false
    }
  }

  def removeChild(
    parent: ParentNode.Base,
    child: ChildNode.Base
  ): Boolean = {
    try {
      parent.ref.removeChild(child.ref)
      true
    } catch {
      case JavaScriptException(_: DOMException) => false
    }
  }

  def insertBefore(
    parent: ParentNode.Base,
    newChild: ChildNode.Base,
    referenceChild: ChildNode.Base
  ): Boolean = {
    try {
      parent.ref.insertBefore(newChild = newChild.ref, refChild = referenceChild.ref)
      true
    } catch {
      case JavaScriptException(_: DOMException) => false
    }
  }

  def replaceChild(
    parent: ParentNode.Base,
    newChild: ChildNode.Base,
    oldChild: ChildNode.Base
  ): Boolean = {
    try {
      parent.ref.replaceChild(newChild = newChild.ref, oldChild = oldChild.ref)
      true
    } catch {
      case JavaScriptException(_: DOMException) => false
    }
  }


  /** Events */

  def addEventListener[Ev <: dom.Event](
    element: ReactiveElement.Base,
    eventPropSetter: EventPropBinder[Ev]
  ): Unit = {
    element.ref.addEventListener(
      `type` = eventPropSetter.key.domName,
      listener = eventPropSetter.domValue,
      useCapture = eventPropSetter.useCapture
    )
  }

  def removeEventListener[Ev <: dom.Event](
    element: ReactiveElement.Base,
    eventPropSetter: EventPropBinder[Ev]
  ): Unit = {
    element.ref.removeEventListener(
      `type` = eventPropSetter.key.domName,
      listener = eventPropSetter.domValue,
      useCapture = eventPropSetter.useCapture
    )
  }


  /** HTML Elements */

  def createHtmlElement[Ref <: dom.html.Element](element: ReactiveHtmlElement[Ref]): Ref = {
    dom.document.createElement(element.tag.name).asInstanceOf[Ref]
  }

  def getHtmlAttribute[V, DomV](element: ReactiveHtmlElement.Base, attr: HtmlAttr[V]): Option[V] = {
    val domValue = element.ref.getAttributeNS(namespaceURI = null, localName = attr.name)
    Option(domValue).map(attr.codec.decode)
  }

  def setHtmlAttribute[V](element: ReactiveHtmlElement.Base, attr: HtmlAttr[V], value: V): Unit = {
    val domValue = attr.codec.encode(value)
    if (domValue == null) { // End users should use `removeAttribute` instead. This is to support boolean attributes.
      removeHtmlAttribute(element, attr)
    } else {
      element.ref.setAttribute(attr.name, domValue.toString)
    }
  }

  def removeHtmlAttribute(element: ReactiveHtmlElement.Base, attr: HtmlAttr[_]): Unit = {
    element.ref.removeAttribute(attr.name)
  }

  /** #Note not sure if this is completely safe. Could this return null? */
  def getHtmlProperty[V, DomV](element: ReactiveHtmlElement.Base, prop: Prop[V, DomV]): V = {
    val domValue = element.ref.asInstanceOf[js.Dynamic].selectDynamic(prop.name).asInstanceOf[DomV]
    if (domValue != null) {
      prop.codec.decode(domValue)
    } else {
      null.asInstanceOf[V]
    }
  }

  def setHtmlProperty[V, DomV](element: ReactiveHtmlElement.Base, prop: Prop[V, DomV], value: V): Unit = {
    val newValue = prop.codec.encode(value).asInstanceOf[js.Any]
    element.ref.asInstanceOf[js.Dynamic].updateDynamic(prop.name)(newValue)
  }

  def setHtmlStyle[V](element: ReactiveHtmlElement.Base, style: Style[V], value: V): Unit = {
    setRefStyle(element.ref, style.cssName, cssValue(value))
  }

  def setHtmlStringStyle(element: ReactiveHtmlElement.Base, style: Style[_], value: String): Unit = {
    setRefStyle(element.ref, style.cssName, cssValue(value))
  }

  def setHtmlAnyStyle[V](element: ReactiveHtmlElement.Base, style: Style[V], value: V | String): Unit = {
    setRefStyle(element.ref, style.cssName, cssValue(value))
  }

  @inline private def cssValue(value: Any): String = {
    if (value == null) {
      null
    } else {
      // @TODO[Performance,Minor] not sure if converting to string is needed.
      //  - According to the API, yes, but in practice browsers are ok with raw values too it seems.
      value.toString
    }
    //value match {
    //  case str: String => str
    //  case int: Int => int
    //  case double: Double => double
    //  case null => null // @TODO[API] Setting a style to null unsets it. Maybe have a better API for this?
    //  case _ => value.toString
    //}
  }

  @inline private def setRefStyle(ref: dom.html.Element, styleCssName: String, styleValue: String): Unit = {
    // Note: we use setProperty / removeProperty instead of mutating ref.style directly to support custom CSS props / variables
    if (styleValue == null) {
      ref.style.removeProperty(styleCssName)
    } else {
      ref.style.setProperty(styleCssName, styleValue)
    }
  }


  /** SVG Elements */

  private val svgNamespaceUri = "http://www.w3.org/2000/svg"

  def createSvgElement[Ref <: dom.svg.Element](element: ReactiveSvgElement[Ref]): Ref = {
    dom.document
      .createElementNS(namespaceURI = svgNamespaceUri, qualifiedName = element.tag.name)
      .asInstanceOf[Ref]
  }

  def getSvgAttribute[V](element: ReactiveSvgElement.Base, attr: SvgAttr[V]): Option[V] = {
    val domValue = element.ref.getAttributeNS(namespaceURI = attr.namespace.orNull, localName = attr.name)
    Option(domValue).map(attr.codec.decode)
  }

  def setSvgAttribute[V](element: ReactiveSvgElement.Base, attr: SvgAttr[V], value: V): Unit = {
    val domValue = attr.codec.encode(value)
    if (domValue == null) { // End users should use `removeSvgAttribute` instead. This is to support boolean attributes.
      removeSvgAttribute(element, attr)
    } else {
      element.ref.setAttributeNS(namespaceURI = attr.namespace.orNull, qualifiedName = attr.name, value = domValue)
    }
  }

  def removeSvgAttribute(element: ReactiveSvgElement.Base, attr: SvgAttr[_]): Unit = {
    element.ref.removeAttributeNS(namespaceURI = attr.namespace.orNull, localName = localName(attr.name))
  }

  @inline private def localName(qualifiedName: String): String = {
    val nsPrefixLength = qualifiedName.indexOf(':')
    if (nsPrefixLength > -1) {
      qualifiedName.substring(nsPrefixLength + 1)
    } else qualifiedName
  }


  /** Comment Nodes */

  def createCommentNode(text: String): dom.Comment = dom.document.createComment(text)

  def setCommentNodeText(node: CommentNode, text: String): Unit = {
    node.ref.textContent = text
  }


  /** Text Nodes */

  def createTextNode(text: String): dom.Text = dom.document.createTextNode(text)

  def setTextNodeText(node: TextNode, text: String): Unit = {
    node.ref.textContent = text
  }


  /** Random utils */

  /** @return hierarchical path describing the position and identity of this node, starting with the root. */
  @tailrec def debugPath(element: dom.Node, initial: List[String] = Nil): List[String] = {
    element match {
      case null => initial
      case _ => debugPath(element.parentNode, debugNodeDescription(element) :: initial)
    }
  }

  /** @return e.g. a, div#mainSection, span.sideNote.sizeSmall */
  def debugNodeDescription(node: dom.Node): String = {
    node match {
      case el: dom.html.Element =>
        val id = el.id
        val suffixStr = if (id.nonEmpty) {
          "#" + id
        } else {
          val classes = el.className
          if (classes.nonEmpty) {
            "." + classes.replace(' ', '.')
          } else {
            ""
          }
        }
        el.tagName.toLowerCase + suffixStr

      case _ => node.nodeName
    }
  }
}
