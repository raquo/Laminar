package com.raquo.laminar.defs.complex

import com.raquo.laminar.DomApi
import com.raquo.laminar.codecs.StringAsIsCodec
import com.raquo.laminar.defs.complex.ComplexSvgKeys._
import com.raquo.laminar.keys.{CompositeKey, SvgAttr}
import com.raquo.laminar.nodes.ReactiveSvgElement

trait ComplexSvgKeys {

  /**
   * This attribute is a list of the classes of the element.
   * Classes allow CSS and Javascript to select and access specific elements
   * via the class selectors or functions like the DOM method
   * document.getElementsByClassName
   */
  val className: CompositeSvgAttr = stringCompositeSvgAttr("class", separator = " ")

  val cls: CompositeSvgAttr = className

  lazy val role: CompositeSvgAttr = stringCompositeSvgAttr("role", separator = " ")

  // --

  protected def stringCompositeSvgAttr(name: String, separator: String): CompositeSvgAttr = {
    val attr = new SvgAttr(name, StringAsIsCodec, namespacePrefix = None)
    new CompositeKey(
      name = attr.name,
      getRawDomValue = el => DomApi.getSvgAttribute(el, attr).getOrElse(""),
      setRawDomValue = (el, value) => DomApi.setSvgAttribute(el, attr, value),
      separator = separator
    )
  }
}

object ComplexSvgKeys {

  type CompositeSvgAttr = CompositeKey[SvgAttr[String], ReactiveSvgElement.Base]
}
