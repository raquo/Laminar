package com.raquo.laminar.domapi.keyapi

import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.nodes.ReactiveHtmlElement

object StylePropDomApi extends DomKeyApi[StyleProp, ReactiveHtmlElement.Base] {

  override def set[V](
    element: ReactiveHtmlElement.Base,
    key: StyleProp[V],
    value: V
  ): Unit = {
    DomApi.setHtmlStyleRaw(
      element = element.ref,
      styleCssName = key.name,
      prefixes = key.prefixes,
      styleValue = DomApi.cssValue(value) // key.encode(value) // #nc
    )
  }

  // #nc TODO add getInline & getComputed?

  // override def get[V](
  //   element: ReactiveHtmlElement.Base,
  //   key: StyleProp[V]
  // ): V | Unit = {
  //   DomApi.getHtmlStyleRaw(element.ref, key.name)
  //   ??? // #nc we can't encode styles – may need special treatment
  // }
}
