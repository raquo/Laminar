package com.raquo.laminar.implicits

import com.raquo.airstream.core.Observable
import com.raquo.dombuilder.generic.KeyImplicits
import com.raquo.dombuilder.generic.builders.SetterBuilders
import com.raquo.dombuilder.generic.syntax.SyntaxImplicits
import com.raquo.dombuilder.jsdom.JsCallback
import com.raquo.domtypes.generic.keys.{Attr, EventProp, Prop, Style, SvgAttr}
import com.raquo.laminar.DomApi
import com.raquo.laminar.emitter.EventPropOps
import com.raquo.laminar.nodes.{ReactiveNode, ReactiveText}
import com.raquo.laminar.receivers.{AttrReceiver, PropReceiver, StyleReceiver, SvgAttrReceiver}
import org.scalajs.dom

import scala.scalajs.js.|

trait Implicits
  extends SyntaxImplicits[ReactiveNode, dom.html.Element, dom.svg.Element, dom.Node, dom.Event, JsCallback]
  with LowPriorityImplicits
  with KeyImplicits[ReactiveNode, dom.html.Element, dom.svg.Element, dom.Node]
  with SetterBuilders[ReactiveNode, dom.html.Element, dom.svg.Element, dom.Node]
  with DomApi
{

  @inline implicit def toAttrReceiver[V](attr: Attr[V]): AttrReceiver[V] = {
    new AttrReceiver(attr)
  }

  @inline implicit def toPropReceiver[V, DomV](prop: Prop[V, DomV]): PropReceiver[V, DomV] = {
    new PropReceiver(prop)
  }

  @inline implicit def toStyleReceiver[V](style: Style[V]): StyleReceiver[V] = {
    new StyleReceiver(style)
  }

  @inline implicit def eventPropToEventPropOps[Ev <: dom.Event](eventProp: EventProp[Ev]): EventPropOps[Ev] = {
    new EventPropOps(eventProp)
  }

  @inline implicit def toSvgAttrReceiver[V](attr: SvgAttr[V]): SvgAttrReceiver[V] = {
    new SvgAttrReceiver(attr)
  }

  @inline implicit def textToNode(text: String): ReactiveText = {
    new ReactiveText(text)
  }

  // @TODO[IDE] This implicit conversion is actually never used by the compiler. However, this makes the Scala plugin for IntelliJ 2017.3 happy.
  @inline implicit def intellijStringObservableAsStringOrStringObservable(stringStream: Observable[String]): Observable[String | String] = {
    stringStream.asInstanceOf[Observable[String | String]]
  }
}
