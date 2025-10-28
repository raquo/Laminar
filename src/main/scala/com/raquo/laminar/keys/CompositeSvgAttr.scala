package com.raquo.laminar.keys

import com.raquo.laminar.nodes.ReactiveSvgElement

class CompositeSvgAttr(
  override val localName: String,
  val namespacePrefix: Option[String],
  override val separator: String
) extends CompositeAttr[CompositeSvgAttr, ReactiveSvgElement.Base] {

  /** Qualified name, including namespace */
  override val name: String = namespacePrefix.map(_ + ":" + localName).getOrElse(localName)

  val namespaceUri: Option[String] = namespacePrefix.map(SvgAttr.namespaceUri)
}
