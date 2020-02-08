package com.raquo.laminar.nodes

import com.raquo.laminar.DomApi
import com.raquo.laminar.builders.HtmlTag
import org.scalajs.dom

class ReactiveHtmlElement[+Ref <: dom.html.Element](val tag: HtmlTag[Ref])
  extends ReactiveElement[Ref] {

  final override val ref: Ref = DomApi.createHtmlElement(this)

  override def toString: String = {
    // `ref` is not available inside ReactiveElement's constructor due to initialization order, so fall back to `tag`.
    s"ReactiveHtmlElement(${ if (ref != null) ref.outerHTML else s"tag=${tag.name}"})"
  }
}

object ReactiveHtmlElement {

  type Base = ReactiveHtmlElement[dom.html.Element]
}
