package com.raquo.laminar

import com.raquo.ew._
import com.raquo.laminar.api.Laminar.{div, svg}
import com.raquo.laminar.keys.{EventProcessor, HtmlAttr, Prop, StyleProp, SvgAttr}
import com.raquo.laminar.modifiers.EventListener
import com.raquo.laminar.nodes.{ChildNode, CommentNode, ParentNode, ReactiveElement, ReactiveHtmlElement, ReactiveSvgElement, TextNode}
import com.raquo.laminar.tags.{HtmlTag, SvgTag, Tag}
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
    listener: EventListener[Ev, _]
  ): Unit = {
    //println(s"> Adding listener on ${DomApi.debugNodeDescription(element.ref)} for `${eventPropSetter.key.name}` with useCapture=${eventPropSetter.useCapture}")
    element.ref.addEventListener(
      `type` = EventProcessor.eventProp(listener.eventProcessor).name,
      listener = listener.domCallback,
      useCapture = EventProcessor.shouldUseCapture(listener.eventProcessor)
    )
  }

  def removeEventListener[Ev <: dom.Event](
    element: ReactiveElement.Base,
    listener: EventListener[Ev, _]
  ): Unit = {
    element.ref.removeEventListener(
      `type` = EventProcessor.eventProp(listener.eventProcessor).name,
      listener = listener.domCallback,
      useCapture = EventProcessor.shouldUseCapture(listener.eventProcessor)
    )
  }


  /** HTML Elements */

  def createHtmlElement[Ref <: dom.html.Element](tag: HtmlTag[Ref]): Ref = {
    dom.document.createElement(tag.name).asInstanceOf[Ref]
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

  def setHtmlStyle[V](element: ReactiveHtmlElement.Base, style: StyleProp[V], value: V): Unit = {
    setRefStyle(element.ref, style.name, style.prefixes, cssValue(value))
  }

  def setHtmlStringStyle(element: ReactiveHtmlElement.Base, style: StyleProp[_], value: String): Unit = {
    setRefStyle(element.ref, style.name, style.prefixes, cssValue(value))
  }

  def setHtmlAnyStyle[V](element: ReactiveHtmlElement.Base, style: StyleProp[V], value: V | String): Unit = {
    setRefStyle(element.ref, style.name, style.prefixes, cssValue(value))
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

  @inline private def setRefStyle(
    ref: dom.html.Element,
    styleCssName: String,
    prefixes: Seq[String],
    styleValue: String
  ): Unit = {
    // #Note: we use setProperty / removeProperty instead of mutating ref.style directly to support custom CSS props / variables
    if (styleValue == null) {
      prefixes.foreach(prefix => ref.style.removeProperty(prefix + styleCssName))
      ref.style.removeProperty(styleCssName)
    } else {
      prefixes.foreach { prefix =>
        ref.style.setProperty(prefix + styleCssName, styleValue)
      }
      ref.style.setProperty(styleCssName, styleValue)
    }
  }


  /** SVG Elements */

  private val svgNamespaceUri = "http://www.w3.org/2000/svg"

  def createSvgElement[Ref <: dom.svg.Element](tag: SvgTag[Ref]): Ref = {
    dom.document
      .createElementNS(namespaceURI = svgNamespaceUri, qualifiedName = tag.name)
      .asInstanceOf[Ref]
  }

  def getSvgAttribute[V](element: ReactiveSvgElement.Base, attr: SvgAttr[V]): Option[V] = {
    // @TODO[Integrity] We're passing fully qualified name instead of local name. Seems to work though?
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
    val nsPrefixLength = qualifiedName.ew.indexOf(":")
    if (nsPrefixLength > -1) {
      qualifiedName.ew.substr(nsPrefixLength + 1).str
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


  /** Custom Elements */

  def isCustomElement(element: dom.Element): Boolean = {
    element.tagName.contains('-')
  }


  /** Input related stuff */

  def getChecked(element: dom.Element): js.UndefOr[Boolean] = {
    element match {
      case input: dom.html.Input if input.`type` == "checkbox" || input.`type` == "radio" =>
        js.defined(input.checked)
      case el if isCustomElement(el) =>
        el.asInstanceOf[js.Dynamic]
          .selectDynamic("checked")
          .asInstanceOf[js.UndefOr[Any]]
          .collect { case b: Boolean => b }
      case _ =>
        js.undefined
    }
  }

  /** @return whether the operation succeeded */
  def setChecked(element: dom.Element, checked: Boolean): Boolean = {
    element match {
      case input: dom.html.Input if input.`type` == "checkbox" || input.`type` == "radio" =>
        input.checked = checked
        true
      case el if isCustomElement(el) =>
        el.asInstanceOf[js.Dynamic].updateDynamic("checked")(checked)
        true
      case _ =>
        false
    }
  }

  def getValue(element: dom.Element): js.UndefOr[String] = {
    element match {
      case input: dom.html.Input =>
        // @TODO[API,Warn] is there a legitimate use case for this on checkbox / radio inputs?
        js.defined(input.value)
      case textarea: dom.html.TextArea =>
        js.defined(textarea.value)
      case select: dom.html.Select =>
        js.defined(select.value)
      case button: dom.html.Button =>
        js.defined(button.value)
      case option: dom.html.Option =>
        js.defined(option.value)
      case el if isCustomElement(el) =>
        el.asInstanceOf[js.Dynamic]
          .selectDynamic("value")
          .asInstanceOf[js.UndefOr[Any]]
          .collect { case s: String => s }
      case _ =>
        js.undefined
    }
  }

  /** @return whether the operation succeeded */
  def setValue(element: dom.Element, value: String): Boolean = {
    element match {
      case input: dom.html.Input =>
        // @TODO[API,Warn] is there a legitimate use case for this on checkbox / radio inputs?
        input.value = value
        true
      case textarea: dom.html.TextArea =>
        textarea.value = value
        true
      case select: dom.html.Select =>
        select.value = value
        true
      case button: dom.html.Button =>
        button.value = value
        true
      case option: dom.html.Option =>
        option.value = value
        true
      case el if isCustomElement(el) =>
        el.asInstanceOf[js.Dynamic]
          .updateDynamic("value")(value)
        true
      case _ =>
        false
    }
  }

  /** @see https://developer.mozilla.org/en-US/docs/Web/API/File_API/Using_files_from_web_applications */
  def getFiles(element: dom.Element): js.UndefOr[List[dom.File]] = {
    element match {
      case input: dom.html.Input if input.`type` == "file" =>
        var result: List[dom.File] = Nil
        var ix = input.files.length - 1
        while (ix >= 0) {
          result = input.files(ix) :: result
          ix -= 1
        }
        result
      case _ => js.undefined
    }
  }


  /** DOM Parser */

  /** #WARNING: HTML can contain Javascript code, which this function will execute blindly! Only use on trusted HTML strings. */
  def unsafeParseHtmlString(dangerousHtmlString: String): dom.html.Element = {
    unsafeParseElementString(htmlParserContainer, tag = js.undefined, dangerousHtmlString, "Error parsing HTML string")
  }

  /** #WARNING: HTML can contain Javascript code, which this function will execute blindly! Only use on trusted HTML strings.
    *
    *  @param tag   the HTML tag you expect from parsing. Will throw exception if does not match the result.
    */
  def unsafeParseHtmlString[Ref <: dom.html.Element](tag: HtmlTag[Ref], dangerousHtmlString: String): Ref = {
    unsafeParseElementString(htmlParserContainer, tag, dangerousHtmlString, "Error parsing HTML string")
  }

  /** #WARNING: SVG can contain Javascript code, which this function will execute blindly! Only use on trusted SVG strings. */
  def unsafeParseSvgString(dangerousSvgString: String): dom.svg.Element = {
    unsafeParseElementString(svgParserContainer, tag = js.undefined, dangerousSvgString, "Error parsing SVG string")
  }

  /** #WARNING: SVG can contain Javascript code, which this function will be execute blindly! Only use on trusted SVG strings.
    *
    * @param tag   the SVG tag you expect from parsing. Will throw exception if does not match the result.
    */
  def unsafeParseSvgString[Ref <: dom.svg.Element](tag: SvgTag[Ref], dangerousSvgString: String): Ref = {
    unsafeParseElementString(svgParserContainer, tag, dangerousSvgString, "Error parsing SVG string")
  }

  private val htmlParserContainer: dom.html.Element = createHtmlElement(div)

  private val svgParserContainer: dom.svg.Element = createSvgElement(svg.svg)

  private def unsafeParseElementString[Ref <: dom.Element](
    parserContainer: dom.Element,
    tag: js.UndefOr[Tag[ReactiveElement[Ref]]],
    dangerousHtmlOrSvgString: String,
    clue: String
  ): Ref = {
    parserContainer.innerHTML = dangerousHtmlOrSvgString
    val numChildren = parserContainer.children.length
    if (numChildren != 1) {
      throw new Exception(s"$clue: expected exactly 1 child in parserContainer, got $numChildren")
    }
    val element = parserContainer.children(0)
    tag.foreach(DomApi.assertTagMatches(_, element, clue))
    parserContainer.innerHTML = ""
    element.asInstanceOf[Ref]
  }

  private[laminar] def assertTagMatches[Ref <: dom.Element](
    tag: Tag[ReactiveElement[Ref]],
    element: dom.Element,
    clue: String
  ): Unit = {
    if (tag.name.ew.toLowerCase() != element.tagName.ew.toLowerCase()) {
      throw new Exception(s"$clue: expected tag name `${tag.name}`, got `${element.tagName}`")
    }
  }


  /** Random utils */

  private val classNamesSeparatorRegex = new js.RegExp(" ", flags = "g")

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
            "." + classes.ew.replace(classNamesSeparatorRegex, ".").str
          } else {
            ""
          }
        }
        el.tagName.ew.toLowerCase().str + suffixStr

      case _ => node.nodeName
    }
  }

  def debugNodeOuterHtml(node: dom.Node): String = {
    node match {
      case el: dom.Element => el.outerHTML
      case el: dom.Text => s"Text(${el.textContent})"
      case el: dom.Comment => s"Comment(${el.textContent})"
      case null => "<null>"
      case other => s"OtherNode(${other.toString})"
    }
  }

  def debugNodeInnerHtml(node: dom.Node): String = {
    node match {
      case el: dom.Element => el.innerHTML
      case el: dom.Text => s"Text(${el.textContent})"
      case el: dom.Comment => s"Comment(${el.textContent})"
      case null => "<null>"
      case other => s"OtherNode(${other.toString})"
    }
  }

}
