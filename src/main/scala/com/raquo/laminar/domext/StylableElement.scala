package com.raquo.laminar.domext

import org.scalajs.dom

import scala.scalajs.js

/** In practice, all possible subclasses of dom.Element:
  *  - dom.HTMLElement,
  *  - dom.SVGElement
  *  - dom.MathMLElement
  * support the same .style API.
  */
@js.native
trait StylableElement extends dom.Element {
  var style: dom.CSSStyleDeclaration = js.native
}
