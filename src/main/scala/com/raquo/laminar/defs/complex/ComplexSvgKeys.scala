package com.raquo.laminar.defs.complex

import com.raquo.laminar.keys
import com.raquo.laminar.keys.CompositeSvgAttr

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

  protected def stringCompositeSvgAttr(
    name: String,
    separator: String,
    namespacePrefix: Option[String] = None
  ): CompositeSvgAttr = {
    new CompositeSvgAttr(name, namespacePrefix, separator)
  }
}

object ComplexSvgKeys {

  @deprecated("CompositeSvgAttr was moved to import com.raquo.laminar.keys, and is now a concrete class", "18.0.0-M1")
  type CompositeSvgAttr = keys.CompositeSvgAttr
}
