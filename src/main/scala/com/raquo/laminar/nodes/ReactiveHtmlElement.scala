package com.raquo.laminar.nodes

import com.raquo.domtypes.generic.keys.{Attr, Prop, Style}
import com.raquo.laminar.DomApi
import com.raquo.laminar.builders.ReactiveHtmlTag
import com.raquo.laminar.receivers.{FocusReceiver, LockedAttrReceiver, LockedPropReceiver, LockedStyleReceiver, StyleReceiver}
import org.scalajs.dom

class ReactiveHtmlElement[+Ref <: dom.html.Element](val tag: ReactiveHtmlTag[Ref])
  extends ReactiveElement[Ref] {

  override val ref: Ref = DomApi.htmlElementApi.createHtmlElement(this)

  @inline def <--[V](style: Style[V]): LockedStyleReceiver[V] = new LockedStyleReceiver(style, this)

  @inline def <--[V](attr: Attr[V]): LockedAttrReceiver[V] = new LockedAttrReceiver(attr, this)

  @inline def <--[V, DomV](prop: Prop[V, DomV]): LockedPropReceiver[V, DomV] = new LockedPropReceiver(prop, this)

  @inline def <--[V] (focus: FocusReceiver.type): FocusReceiver = new FocusReceiver(this)
}
