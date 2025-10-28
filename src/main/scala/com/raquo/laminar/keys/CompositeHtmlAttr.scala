package com.raquo.laminar.keys

import com.raquo.laminar.nodes.ReactiveHtmlElement

class CompositeHtmlAttr(
  override val name: String,
  override val separator: String
) extends CompositeAttr[CompositeHtmlAttr, ReactiveHtmlElement.Base] {

  override val localName: String = name

  override val namespaceUri: Option[String] = None
}
