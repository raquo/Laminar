package com.raquo.laminar.domapi.keyapi

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.keys.{DerivedStyleProp, StyleProp}
import com.raquo.laminar.nodes.ReactiveHtmlElement

object DerivedStylePropDomApi extends DomKeyApi[DerivedStyleProp, ReactiveHtmlElement.Base] {

  override def set[V](
    element: ReactiveHtmlElement.Base,
    key: DerivedStyleProp[V],
    value: V
  ): Unit = {
    DomApi.setHtmlStyleRaw(
      element = element.ref,
      styleCssName = key.name,
      prefixes = key.key.prefixes,
      styleValue = key.encode(value)
    )
  }

  def getInline(
    element: ReactiveHtmlElement.Base,
    key: StyleProp[_]
  ): String = {
    DomApi.getInlineHtmlStyleRaw(element.ref, key.name)
  }
}
