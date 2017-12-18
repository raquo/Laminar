package com.raquo.laminar

import com.raquo.dombuilder.generic.KeyImplicits
import com.raquo.dombuilder.generic.builders.SetterBuilders
import com.raquo.dombuilder.generic.syntax.SyntaxImplicits
import com.raquo.dombuilder.jsdom.JsCallback
import com.raquo.domtypes.generic.keys.{Attr, EventProp, Prop, Style}
import com.raquo.laminar.emitter.EventPropOps
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveNode, ReactiveText}
import com.raquo.laminar.receivers.{AttrReceiver, PropReceiver, StyleReceiver}
import com.raquo.laminar.syntax.ReactiveHtmlElementSyntax
import com.raquo.xstream.XStream
import org.scalajs.dom

import scala.scalajs.js.|

trait Implicits
  extends SyntaxImplicits[ReactiveNode, dom.Element, dom.Node, dom.Event, JsCallback]
  with KeyImplicits[ReactiveNode, dom.Element, dom.Node]
  with SetterBuilders[ReactiveNode, dom.Element, dom.Node]
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

  @inline implicit def toEventPropOps[Ev <: dom.Event](eventProp: EventProp[Ev]): EventPropOps[Ev] = {
    new EventPropOps(eventProp)
  }

  @inline implicit def textToNode(text: String): ReactiveText = {
    new ReactiveText(text)
  }

  @inline implicit def reactiveHtmlElementToSyntax(element: ReactiveElement[dom.html.Element]): ReactiveHtmlElementSyntax = {
    new ReactiveHtmlElementSyntax(element)
  }

  // @TODO[IDE] This implicit conversion is actually never used by the compiler. However, this makes the Scala plugin for IntelliJ 2017.2.5 happy.
  @inline implicit def intellijStringStreamAsStringOrStringStream(stringStream: XStream[String]): XStream[String | String] = {
    stringStream.asInstanceOf[XStream[String | String]]
  }

  //  @inline implicit def optionToModifier[T](
  //    maybeModifier: Option[T]
  //  )(
  //    implicit toModifier: T => Modifier[RNode, RNodeData]
  //  ): Modifier[RNode, RNodeData] = {
  //    Conversions.optionToModifier(maybeModifier)
  //  }
}
