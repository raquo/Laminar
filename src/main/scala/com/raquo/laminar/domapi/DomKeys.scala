package com.raquo.laminar.domapi

import org.scalajs.dom

import scala.collection.immutable
import scala.scalajs.js
import scala.scalajs.js.|

trait DomKeys {

  /** Returns `js.undefined` aka `Unit` when attribute is not set on the element. */
  def getAttributeRaw(
    element: dom.Element,
    localName: String,
    namespaceUri: String // nullable
  ): String | Unit = {
    val domValue = if (namespaceUri == null) {
      element.getAttribute(
        name = localName
      )
    } else {
      element.getAttributeNS(
        namespaceURI = namespaceUri,
        localName = localName
      )
    }
    if (domValue != null) domValue else ()
  }

  def setAttributeRaw(
    element: dom.Element,
    localName: String,
    qualifiedName: String,
    namespaceUri: String, // nullable
    domValue: String // nullable
  ): Unit = {
    if (domValue == null) {
      if (namespaceUri == null) {
        element.removeAttribute(
          name = localName
        )
      } else {
        element.removeAttributeNS(
          namespaceURI = namespaceUri, // Tested that this works in Chrome & FF
          localName = localName
        )
      }
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

  /** Returns `js.undefined` aka `Unit` when the property is not applicable to the element.
    * If the element type supports this property, it should never be js.undefined,
    * (unless you've set that property on the wrong element).
    */
  def getHtmlPropertyRaw[DomV](
    element: dom.html.Element,
    name: String
  ): DomV | Unit = {
    element.asInstanceOf[js.Dynamic].selectDynamic(name).asInstanceOf[js.UndefOr[DomV]]
  }

  def setHtmlPropertyRaw[DomV](
    element: dom.html.Element,
    name: String,
    value: DomV
  ): Unit = {
    element.asInstanceOf[js.Dynamic].updateDynamic(name)(value.asInstanceOf[js.Any])
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

  def setHtmlStyleRaw(
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

  // #nc #TODO add computerd styles getter at least here
  /** Note: this only gets inline style values – those set via the `style` attribute, which includes
    * all style props set by Laminar. It does not account for CSS declarations in `<style>` tags.
    *
    * Returns empty string if the given style property is not defined in this element's inline styles.
    *
    * See https://developer.mozilla.org/en-US/docs/Web/API/CSSStyleDeclaration/getPropertyValue
    * Contrast with https://developer.mozilla.org/en-US/docs/Web/API/Window/getComputedStyle
    */
  def getInlineHtmlStyleRaw(
    element: dom.html.Element,
    styleCssName: String
  ): String = {
    element.style.getPropertyValue(styleCssName)
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

}
