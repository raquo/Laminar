package com.raquo.laminar.domapi

import com.raquo.laminar.PlatformSpecific.StringOr
import com.raquo.laminar.domapi.DomKeys._
import com.raquo.laminar.keys.{DerivedStyleProp, HtmlProp, SimpleAttr, StyleProp}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}
import org.scalajs.dom
import org.scalajs.dom.CSSStyleDeclaration

import scala.collection.immutable
import scala.scalajs.js
import scala.scalajs.js.|

trait DomKeys {

  def setAttribute[V, El <: ReactiveElement.Base](
    element: El,
    attr: SimpleAttr[_, V, El],
    value: V | Null
  ): Unit = {
    val domValue = if (value == null) {
      null
    } else {
      attr.codec.encode(value.asInstanceOf[V]) // #Safe because null check above
    }
    setAttributeRaw(
      element = element.ref,
      localName = attr.localName,
      qualifiedName = attr.name,
      namespaceUri = attr.namespaceUri.orNull[String],
      domValue = domValue
    )
  }

  def getHtmlProperty[V, El <: ReactiveHtmlElement.Base](
    element: El,
    prop: HtmlProp[V]
  ): V | Unit = {
    getHtmlPropertyRaw(element.ref, prop.name).map(prop.codec.decode)
  }

  def setHtmlProperty[V, El <: ReactiveHtmlElement.Base](
    element: El,
    prop: HtmlProp[V],
    value: V | Null
  ): Unit = {
    val domValue = if (value == null) {
      null
    } else {
      prop.codec.encode(value.asInstanceOf[V]) // #Safe because null check above
    }
    setHtmlPropertyRaw(
      element = element.ref,
      name = prop.name,
      value = domValue
    )
  }

  /** If you don't have a String value, pass `prop(scalaValue).cssValue` to value`. */
  def setHtmlStyle[V](
    element: ReactiveHtmlElement.Base,
    prop: StyleProp[V],
    value: V | String | Null
  ): Unit = {
    DomApi.setHtmlStyleRaw(
      element = element.ref,
      styleCssName = prop.name,
      prefixes = prop.prefixes,
      styleValue = DomApi.cssValue(value)
    )
  }

  def setHtmlDerivedStyle[V](
    element: ReactiveHtmlElement.Base,
    prop: DerivedStyleProp[V],
    value: V | Null
  ): Unit = {
    val encodedValue = if (value == null) {
      null
    } else {
      prop.encode(value.asInstanceOf[V]) // #Safe because null check above
    }
    DomApi.setHtmlStyleRaw(
      element = element.ref,
      styleCssName = prop.name,
      prefixes = prop.key.prefixes,
      styleValue = encodedValue
    )
  }

  /** Returns `js.undefined` aka `Unit` when attribute is not set on the element. */
  def getAttributeRaw(
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

  def setAttributeRaw(
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
    styleValue: String | Null
  ): Unit = {
    // #Note: we use setProperty / removeProperty instead of mutating ref.style directly to support custom CSS props / variables
    if (styleValue == null) {
      prefixes.foreach(prefix => element.style.removeProperty(prefix + styleCssName))
      element.style.removeProperty(styleCssName)
    } else {
      val _styleValue: String = styleValue.asInstanceOf[String] // #Safe because null check above
      prefixes.foreach { prefix =>
        element.style.setProperty(prefix + styleCssName, _styleValue)
      }
      element.style.setProperty(styleCssName, _styleValue)
    }
  }

  /** Note: this only gets INLINE style values – those set via the `style` attribute, which includes
    * all style props set by Laminar. It does not account for CSS declarations in `<style>` tags.
    *
    * Returns empty string if the given style property is not defined in this element's inline styles.
    *
    * See https://developer.mozilla.org/en-US/docs/Web/API/CSSStyleDeclaration/getPropertyValue
    * Contrast with [[getComputedStyleRaw]]
    */
  def getInlineStyleRaw(
    element: DomStylableElement,
    styleCssName: String
  ): String = {
    element.style.getPropertyValue(styleCssName)
  }

  /** Returns COMPUTED value of the CSS property. Contrast with [[getInlineStyleRaw]].
    *
    * Returns empty string if the given style property is not defined in this element's inline styles.
    *
    * See https://developer.mozilla.org/en-US/docs/Web/API/Window/getComputedStyle
    */
  def getComputedStyleRaw(
    element: dom.Element,
    styleCssName: String
  ): String = {
    dom.window.getComputedStyle(element).getPropertyValue(styleCssName)
  }

  /** Stringifies the provided value for use in CSS.
    * #Note: this can potentially be applied twice (see usages), it's mostly a type helper.
    */
  def cssValue(value: Any): String | Null = {
    if (value == null) {
      null
    } else {
      // @TODO[Performance,Minor] not sure if converting to string is needed.
      //  - According to the API, yes, but in practice browsers are ok with raw values too it seems.
      //  - Given how rare Double / Int style props are, I think runtime type matching would be less efficient than string.toString
      value.toString
    }
  }

}

object DomKeys {

  // #TODO[scalajsdom] SVG and MathML elements should have .style property too (but not base Element IIUC)
  type DomStylableElement = dom.html.Element | dom.svg.Element

  implicit class DomStylableElementExt(val el: DomStylableElement) extends AnyVal {
    def style: CSSStyleDeclaration = el.asInstanceOf[dom.html.Element].style
  }
}
