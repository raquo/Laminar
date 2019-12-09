package com.raquo.laminar.nodes

import com.raquo.laminar.DomApi
import com.raquo.laminar.builders.HtmlTag
import com.raquo.laminar.keys.{ReactiveHtmlAttr, ReactiveProp, ReactiveStyle}
import com.raquo.laminar.receivers.{AttrReceiver, FocusReceiver, PropReceiver, StyleReceiver}
import org.scalajs.dom

class ReactiveHtmlElement[+Ref <: dom.html.Element](val tag: HtmlTag[Ref])
  extends ReactiveElement[Ref] {

  final override val ref: Ref = DomApi.createHtmlElement(this)

  final def <--[V](style: ReactiveStyle[V]): StyleReceiver[V] = new StyleReceiver(style, this)

  final def <--[V](attr: ReactiveHtmlAttr[V]): AttrReceiver[V] = new AttrReceiver(attr, this)

  final def <--[V, DomV](prop: ReactiveProp[V, DomV]): PropReceiver[V, DomV] = new PropReceiver(prop, this)

  final def <--[V](focus: FocusReceiver.type): FocusReceiver = new FocusReceiver(this)

  override def toString: String = {
    s"ReactiveHtmlElement(${ref.outerHTML})"
  }
}

object ReactiveHtmlElement {

  type Base = ReactiveHtmlElement[dom.html.Element]
}
