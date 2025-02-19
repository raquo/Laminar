package com.raquo.laminar

import com.raquo.ew._
import com.raquo.laminar.api.L.svg
import com.raquo.laminar.keys.{AriaAttr, EventProcessor, HtmlAttr, HtmlProp, StyleProp, SvgAttr}
import com.raquo.laminar.modifiers.EventListener
import com.raquo.laminar.nodes.{CommentNode, ReactiveElement, ReactiveHtmlElement, ReactiveSvgElement, TextNode}
import com.raquo.laminar.tags.{HtmlTag, SvgTag, Tag}
import org.scalajs.dom

import scala.annotation.tailrec
import scala.collection.immutable
import scala.scalajs.js
import scala.scalajs.js.{|, JavaScriptException}

/** Low level DOM APIs used by Laminar.
  *
  * End users: Do not call any mutator methods here on Laminar-managed elements,
  * these methods do not make the necessary updates to Laminar internal state.
  * Instead, use regular Laminar API, or, if you must, the methods from
  * [[com.raquo.laminar.nodes.ParentNode]]
  */
object DomApi {

  /* Tree update functions */

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

  //
  // Events
  //

  def addEventListener[Ev <: dom.Event](
    element: dom.Element,
    listener: EventListener[Ev, _]
  ): Unit = {
    // println(s"> Adding listener on ${DomApi.debugNodeDescription(element.ref)} for `${eventPropSetter.key.name}` with useCapture=${eventPropSetter.useCapture}")
    element.addEventListener(
      `type` = EventProcessor.eventProp(listener.eventProcessor).name,
      listener = listener.domCallback,
      options = listener.options
    )
  }

  def removeEventListener[Ev <: dom.Event](
    element: dom.Element,
    listener: EventListener[Ev, _]
  ): Unit = {
    element.removeEventListener(
      `type` = EventProcessor.eventProp(listener.eventProcessor).name,
      listener = listener.domCallback,
      options = listener.options
    )
  }

  //
  // HTML Elements
  //

  def createHtmlElement[Ref <: dom.html.Element](tag: HtmlTag[Ref]): Ref = {
    dom.document.createElement(tag.name).asInstanceOf[Ref]
  }

  //
  // HTML Attributes
  //

  def getHtmlAttribute[V](
    element: ReactiveHtmlElement.Base,
    attr: HtmlAttr[V]
  ): js.UndefOr[V] = {
    getHtmlAttributeRaw(element.ref, attr.name).map(attr.codec.decode)
  }

  // #nc changed api
  def getHtmlAttributeRaw(
    element: dom.html.Element,
    name: String
  ): String | Unit = {
    val domValue = element.getAttributeNS(namespaceURI = null, localName = name)
    if (domValue != null) {
      domValue
    } else {
      ()
    }
  }

  def setHtmlAttribute[V](
    element: ReactiveHtmlElement.Base,
    attr: HtmlAttr[V],
    value: V
  ): Unit = {
    val domValue = attr.codec.encode(value)
    setHtmlAttributeRaw(element.ref, attr.name, domValue)
  }

  /** @param domValue nullable */
  private[laminar] def setHtmlAttributeRaw(
    element: dom.html.Element,
    name: String,
    domValue: String
  ): Unit = {
    if (domValue == null) { // End users should use `removeHtmlAttribute` instead. This is to support boolean attributes.
      element.removeAttribute(name)
    } else {
      element.setAttribute(name, domValue.asInstanceOf[String])
    }
  }

  def removeHtmlAttribute(
    element: ReactiveHtmlElement.Base,
    attr: HtmlAttr[_]
  ): Unit = {
    setHtmlAttributeRaw(element.ref, attr.name, domValue = null)
  }

  //
  // HTML Properties
  //

  /** Returns `js.undefined` when the property is missing on the element.
    * If the element type supports this property, it should never be js.undefined.
    */
  def getHtmlProperty[V, DomV](
    element: ReactiveHtmlElement.Base,
    prop: HtmlProp[V, DomV]
  ): js.UndefOr[V] = {
    val domValue = getHtmlPropertyRaw[DomV](element.ref, prop.name)
    domValue.map(prop.codec.decode)
  }

  def getHtmlPropertyRaw[DomV](
    element: dom.html.Element,
    name: String
  ): js.UndefOr[DomV] = {
    element.asInstanceOf[js.Dynamic].selectDynamic(name).asInstanceOf[js.UndefOr[DomV]]
  }

  def setHtmlProperty[V, DomV](
    element: ReactiveHtmlElement.Base,
    prop: HtmlProp[V, DomV],
    value: V
  ): Unit = {
    val domValue = prop.codec.encode(value)
    setHtmlPropertyRaw(element.ref, prop.name, domValue)
  }

  def removeHtmlProperty(
    element: ReactiveHtmlElement.Base,
    prop: HtmlProp[_, _]
  ): Unit = {
    removeHtmlPropertyRaw(element.ref, prop.name, prop.reflectedAttrName)
  }

  def removeHtmlPropertyRaw(
    element: dom.html.Element,
    propName: String,
    reflectedAttrName: Option[String]
  ): Unit = {
    // #TODO[Integrity] Not sure if setting the property to `null` works universally.
    //  - We may need to implement custom adjustments later.
    reflectedAttrName.fold(
      ifEmpty = element.asInstanceOf[js.Dynamic].updateDynamic(propName)(null)
    ) { attrName =>
      element.removeAttribute(attrName)
    }
  }

  // #nc change api
  private[laminar] def setHtmlPropertyRaw[DomV](
    element: dom.html.Element,
    name: String,
    value: DomV
  ): Unit = {
    element.asInstanceOf[js.Dynamic].updateDynamic(name)(value.asInstanceOf[js.Any])
  }

  //
  // CSS Style Properties
  //

  // #nc changed api
  /** Note: this only gets inline style values – those set via the `style` attribute, which includes
    * all style props set by Laminar. It does not account for CSS declarations in `<style>` tags.
    *
    * Returns empty string if the given style property is not defined in this element's inline styles.
    *
    * See https://developer.mozilla.org/en-US/docs/Web/API/CSSStyleDeclaration/getPropertyValue
    * Contrast with https://developer.mozilla.org/en-US/docs/Web/API/Window/getComputedStyle
    */
  def getHtmlStyleRaw(
    element: dom.html.Element,
    styleCssName: String
  ): String = {
    element.style.getPropertyValue(styleCssName)
  }

  def setHtmlStyle[V](
    element: ReactiveHtmlElement.Base,
    styleProp: StyleProp[V],
    value: V
  ): Unit = {
    setHtmlStyleRaw(element.ref, styleProp.name, styleProp.prefixes, cssValue(value))
  }

  def setHtmlStringStyle(
    element: ReactiveHtmlElement.Base,
    styleProp: StyleProp[_],
    value: String
  ): Unit = {
    setHtmlStyleRaw(element.ref, styleProp.name, styleProp.prefixes, cssValue(value))
  }

  @deprecated("setHtmlAnyStyle was renamed to setHtmlStyle", "18.0.0-M1")
  def setHtmlAnyStyle[V](
    element: ReactiveHtmlElement.Base,
    style: StyleProp[V],
    value: V
  ): Unit = {
    setHtmlStyleRaw(element.ref, style.name, style.prefixes, cssValue(value))
  }

  def removeHtmlStyle[V](
    element: ReactiveHtmlElement.Base,
    style: StyleProp[V]
  ): Unit = {
    setHtmlStyleRaw(element.ref, style.name, style.prefixes, styleValue = null)
  }

  @inline private[laminar] def cssValue(value: Any): String = {
    if (value == null) {
      null
    } else {
      // @TODO[Performance,Minor] not sure if converting to string is needed.
      //  - According to the API, yes, but in practice browsers are ok with raw values too it seems.
      value.toString
    }
    // value match {
    //   case str: String => str
    //   case int: Int => int
    //   case double: Double => double
    //   case null => null // @TODO[API] Setting a style to null unsets it. Maybe have a better API for this?
    //   case _ => value.toString
    // }
  }

  @inline private[laminar] def setHtmlStyleRaw(
    element: dom.html.Element,
    styleCssName: String,
    prefixes: immutable.Seq[String],
    styleValue: String
  ): Unit = {
    // #Note: we use setProperty / removeProperty instead of mutating ref.style directly to support custom CSS props / variables
    if (styleValue == null) {
      prefixes.foreach(prefix => element.style.removeProperty(prefix + styleCssName))
      element.style.removeProperty(styleCssName)
    } else {
      prefixes.foreach { prefix =>
        element.style.setProperty(prefix + styleCssName, styleValue)
      }
      element.style.setProperty(styleCssName, styleValue)
    }
  }

  //
  // SVG Elements
  //

  def createSvgElement[Ref <: dom.svg.Element](tag: SvgTag[Ref]): Ref = {
    dom.document
      .createElementNS(namespaceURI = SvgAttr.svgNamespaceUri, qualifiedName = tag.name)
      .asInstanceOf[Ref]
  }

  //
  // SVG Attributes
  //

  def getSvgAttribute[V](
    element: ReactiveSvgElement.Base,
    attr: SvgAttr[V]
  ): js.UndefOr[V] = {
    getSvgAttributeRaw(
      element = element.ref,
      localName = attr.localName,
      namespaceUri = attr.namespaceUri.orNull
    ).map(attr.codec.decode)
  }

  // #nc this is a change in api

  /** @param namespaceUri nullable */
  def getSvgAttributeRaw(
    element: dom.svg.Element,
    localName: String,
    namespaceUri: String
  ): js.UndefOr[String] = {
    val domValue = element.getAttributeNS(
      namespaceURI = namespaceUri,
      localName = localName
    )
    if (domValue != null) {
      domValue
    } else {
      js.undefined
    }
  }

  def setSvgAttribute[V](
    element: ReactiveSvgElement.Base,
    attr: SvgAttr[V],
    value: V
  ): Unit = {
    val domValue = attr.codec.encode(value)
    setSvgAttributeRaw(
      element = element.ref,
      localName = attr.localName,
      qualifiedName = attr.name,
      namespaceUri = attr.namespaceUri.orNull,
      domValue = domValue
    )
  }

  // #nc
  /** @param namespaceUri nullable */
  private[laminar] def setSvgAttributeRaw(
    element: dom.svg.Element,
    localName: String,
    qualifiedName: String,
    namespaceUri: String,
    domValue: String
  ): Unit = {
    if (domValue == null) {
      element.removeAttributeNS(
        namespaceURI = namespaceUri, // Tested that this works in Chrome & FF
        localName = localName
      )
    } else {
      if (namespaceUri == null) {
        // See https://github.com/raquo/Laminar/issues/143
        element.setAttribute(
          name = qualifiedName,
          value = domValue
        )
      } else {
        element.setAttributeNS(
          namespaceURI = namespaceUri,
          qualifiedName = qualifiedName,
          value = domValue
        )
      }
    }
  }

  def removeSvgAttribute(
    element: ReactiveSvgElement.Base,
    attr: SvgAttr[_]
  ): Unit = {
    setSvgAttributeRaw(
      element.ref,
      localName = attr.localName,
      qualifiedName = attr.name,
      namespaceUri = attr.namespaceUri.orNull,
      domValue = null
    )
  }

  /** Aria attributes */

  def getAriaAttribute[V](
    element: ReactiveElement.Base,
    attr: AriaAttr[V]
  ): js.UndefOr[V] = {
    getAriaAttributeRaw(element.ref, attr.name).map(attr.codec.decode)
  }

  def getAriaAttributeRaw(
    element: dom.Element,
    name: String
  ): String | Unit = {
    val domValue = element.getAttributeNS(namespaceURI = null, localName = name)
    if (domValue != null) {
      domValue
    } else {
      ()
    }
  }

  def setAriaAttribute[V](
    element: ReactiveElement.Base,
    attr: AriaAttr[V],
    value: V
  ): Unit = {
    val domValue = attr.codec.encode(value)
    setAriaAttributeRaw(element.ref, attr.name, domValue)
  }

  private[laminar] def setAriaAttributeRaw(
    element: dom.Element,
    name: String,
    domValue: String
  ): Unit = {
    if (domValue == null) {
      // End users should use `removeAriaAttribute` instead. This is to support boolean attributes.
      element.removeAttribute(name)
    } else {
      element.setAttribute(name, domValue)
    }
  }

  def removeAriaAttribute(
    element: ReactiveElement.Base,
    attr: AriaAttr[_]
  ): Unit = {
    element.ref.removeAttribute(attr.name)
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
  // Input related stuff
  //

  def getChecked(element: dom.Element): js.UndefOr[Boolean] = {
    element match {
      case input: dom.html.Input if input.`type` == "checkbox" || input.`type` == "radio" =>
        input.checked
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
        input.value
      case textarea: dom.html.TextArea =>
        textarea.value
      case select: dom.html.Select =>
        select.value
      case button: dom.html.Button =>
        button.value
      case option: dom.html.Option =>
        option.value
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

  //
  // DOM Parser
  //

  private val htmlParserContainer: dom.HTMLTemplateElement = {
    dom.document.createElement("template").asInstanceOf[dom.HTMLTemplateElement]
  }

  private val svgParserContainer: dom.svg.Element = {
    createSvgElement(svg.svg)
  }

  /** #WARNING: Only use on trusted HTML strings.
    *  HTML strings can contain embedded Javascript code,
    *  which this function will execute blindly!
    *
    * Note: this expects the html string to contain one HTML element,
    * and will throw otherwise (e.g. if given a text node, an SVG, or multiple elements)
    */
  def unsafeParseHtmlString(
    dangerousHtmlString: String
  ): dom.html.Element = {
    val nodes = unsafeParseHtmlStringIntoNodeArray(dangerousHtmlString)
    val clue = "Error parsing HTML string"
    val node = assertSingleNode(nodes, clue)
    assertHtmlElement(node, clue)
  }

  /** #WARNING: Only use on trusted HTML strings.
    *  HTML strings can contain embedded Javascript code,
    *  which this function will execute blindly!
    *
    * Note: this expects the html string to contain one element matching the
    * tag name, and will throw otherwise (e.g. if given a text node, an
    * element with a different tag name, or multiple elements)
    */
  def unsafeParseHtmlString[Ref <: dom.html.Element](
    tag: HtmlTag[Ref],
    dangerousHtmlString: String
  ): Ref = {
    val nodes = unsafeParseHtmlStringIntoNodeArray(dangerousHtmlString)
    val clue = "Error parsing HTML string"
    val node = assertSingleNode(nodes, clue)
    assertTagMatches(tag, node, clue)
  }

  /** #WARNING: Only use on trusted SVG strings.
    *  SVG strings can contain embedded Javascript code,
    *  which this function will execute blindly!
    *
    * Note: this expects the svg string to contain one HTML element,
    * and will throw otherwise (e.g. if given a text node, HTML, or
    * multiple elements)
    */
  def unsafeParseSvgString(
    dangerousSvgString: String
  ): dom.svg.Element = {
    val nodes = unsafeParseSvgStringIntoNodeArray(dangerousSvgString)
    val clue = "Error parsing SVG string"
    val node = assertSingleNode(nodes, clue)
    assertSvgElement(node, clue)
  }

  /** #WARNING: Only use on trusted SVG strings.
    *  SVG strings can contain embedded Javascript code,
    *  which this function will execute blindly!
    *
    * Note: this expects the svg string to contain one element matching the
    * tag name, and will throw otherwise (e.g. if given a text node, an
    * element with a different tag name, or multiple elements)
    */
  def unsafeParseSvgString[Ref <: dom.svg.Element](
    tag: SvgTag[Ref],
    dangerousSvgString: String
  ): Ref = {
    val nodes = unsafeParseSvgStringIntoNodeArray(dangerousSvgString)
    val clue = "Error parsing SVG string"
    val node = assertSingleNode(nodes, clue)
    assertTagMatches(tag, node, clue)
  }

  /** #WARNING: Only use on trusted HTML strings.
    *  HTML strings can contain embedded Javascript code,
    *  which this function will execute blindly!
    *
    * Note: This method does not work in Internet Explorer.
    * See https://stackoverflow.com/q/10585029 for the issues with various approaches.
    * Use this if you need IE support: https://gist.github.com/Munawwar/6e6362dbdf77c7865a99
    */
  def unsafeParseHtmlStringIntoNodeArray(
    dangerousHtmlString: String
  ): js.Array[dom.Node] = {
    htmlParserContainer.innerHTML = dangerousHtmlString
    // #TODO add to `ew`: js.Array.from(nodeList)
    val arr = js.Array.from(htmlParserContainer.content.childNodes.asInstanceOf[js.Iterable[dom.Node]])
    htmlParserContainer.innerHTML = ""
    arr
  }

  /** #WARNING: Only use on trusted SVG strings.
    *  SVG strings can contain embedded Javascript code,
    *  which this function will execute blindly!
    */
  def unsafeParseSvgStringIntoNodeArray(
    dangerousSvgString: String
  ): js.Array[dom.Node] = {
    svgParserContainer.innerHTML = dangerousSvgString
    // #TODO add to `ew`: js.Array.from(nodeList)
    val arr = js.Array.from(svgParserContainer.childNodes.asInstanceOf[js.Iterable[dom.Node]])
    svgParserContainer.innerHTML = ""
    arr
  }

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

  //
  // Random utils
  //

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
