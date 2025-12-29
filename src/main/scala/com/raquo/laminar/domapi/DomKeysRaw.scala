package com.raquo.laminar.domapi

import com.raquo.laminar.domapi.DomKeysRaw._
import com.raquo.laminar.domext
import org.scalajs.dom

import scala.collection.immutable
import scala.scalajs.js
import scala.scalajs.js.|

trait DomKeysRaw {

  /** Returns `js.undefined` aka `Unit` when attribute is not set on the element. */
  def getAttribute(
    element: dom.Element,
    localName: String,
    namespaceUri: String | Null // nullable
  ): String | Unit = {
    val domValue = if (namespaceUri == null) {
      element.getAttribute(
        name = localName
      )
    } else {
      element.getAttributeNS(
        namespaceURI = namespaceUri.asInstanceOf[String], // #Safe because null check above
        localName = localName
      )
    }
    if (domValue != null) domValue else ()
  }

  def setAttribute(
    element: dom.Element,
    localName: String,
    qualifiedName: String,
    namespaceUri: String | Null, // nullable
    domValue: String | Null // nullable
  ): Unit = {
    if (domValue == null) {
      if (namespaceUri == null) {
        element.removeAttribute(
          name = localName
        )
      } else {
        element.removeAttributeNS(
          namespaceURI = namespaceUri.asInstanceOf[String], // #Safe because null check above.
          localName = localName
        )
      }
    } else {
      if (namespaceUri == null) {
        // See https://github.com/raquo/Laminar/issues/143
        element.setAttribute(
          name = qualifiedName,
          value = domValue.asInstanceOf[String], // #Safe because null check above
        )
      } else {
        element.setAttributeNS(
          namespaceURI = namespaceUri.asInstanceOf[String], // #Safe because null check above
          qualifiedName = qualifiedName,
          value = domValue.asInstanceOf[String], // #Safe because null check above
        )
      }
    }
  }

  /** Returns `js.undefined` aka `Unit` when the property is not applicable to the element.
    * If the element type supports this property, it should never be js.undefined,
    * (unless you've set that property on the wrong element).
    */
  def getHtmlProperty[DomV](
    element: dom.html.Element,
    name: String
  ): DomV | Unit = {
    element.asInstanceOf[js.Dynamic].selectDynamic(name).asInstanceOf[js.UndefOr[DomV]]
  }

  def setHtmlProperty[DomV](
    element: dom.html.Element,
    name: String,
    value: DomV
  ): Unit = {
    element.asInstanceOf[js.Dynamic].updateDynamic(name)(value.asInstanceOf[js.Any])
  }

  def removeHtmlProperty(
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

  def setStyle(
    element: dom.Element,
    styleCssName: String,
    prefixes: immutable.Seq[String],
    styleValue: String | Null
  ): Unit = {
    /** All dom.Element-s are actually stylable. See [[domext.StylableElement]] */
    val stylableElement = element.asInstanceOf[domext.StylableElement]
    // #Note: we use setProperty / removeProperty instead of mutating ref.style directly to support custom CSS props / variables
    if (styleValue == null) {
      prefixes.foreach(prefix => stylableElement.style.removeProperty(prefix + styleCssName))
      stylableElement.style.removeProperty(styleCssName)
    } else {
      val _styleValue: String = styleValue.asInstanceOf[String] // #Safe because null check above
      prefixes.foreach { prefix =>
        stylableElement.style.setProperty(prefix + styleCssName, _styleValue)
      }
      stylableElement.style.setProperty(styleCssName, _styleValue)
    }
  }

  /** Note: this only gets INLINE style values – those set via the `style` attribute, which includes
    * all style props set by Laminar. It does not account for CSS declarations in `<style>` tags.
    *
    * Returns empty string if the given style property is not defined in this element's inline styles.
    *
    * See https://developer.mozilla.org/en-US/docs/Web/API/CSSStyleDeclaration/getPropertyValue
    * Contrast with [[getComputedStyle]]
    */
  def getInlineStyle(
    element: DomStylableElement,
    styleCssName: String
  ): String = {
    element.style.getPropertyValue(styleCssName)
  }

  /** Returns COMPUTED value of the CSS property. Contrast with [[getInlineStyle]].
    *
    * Returns empty string if the given style property is not defined in this element's inline styles.
    *
    * See https://developer.mozilla.org/en-US/docs/Web/API/Window/getComputedStyle
    */
  def getComputedStyle(
    element: dom.Element,
    styleCssName: String
  ): String = {
    dom.window.getComputedStyle(element).getPropertyValue(styleCssName)
  }
}

object DomKeysRaw {

  // #TODO[scalajsdom] SVG and MathML elements should have .style property too (but not base Element IIUC)
  type DomStylableElement = dom.html.Element | dom.svg.Element

  implicit class DomStylableElementExt(val el: DomStylableElement) extends AnyVal {
    def style: dom.CSSStyleDeclaration = el.asInstanceOf[dom.html.Element].style
  }
}
