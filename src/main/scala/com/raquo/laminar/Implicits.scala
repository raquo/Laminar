package com.raquo.laminar

import com.raquo.dombuilder.generic.KeyImplicits
import com.raquo.dombuilder.generic.builders.SetterBuilders
import com.raquo.dombuilder.generic.syntax.SyntaxImplicits
import com.raquo.dombuilder.jsdom.JsCallback
import com.raquo.domtypes.generic.keys.{Attr, EventProp, Style}
import com.raquo.laminar.nodes.{ReactiveNode, ReactiveText}
import com.raquo.laminar.receivers.{AttrReceiver, StyleReceiver}
import com.raquo.laminar.subscriptions.EventEmitter
import org.scalajs.dom

trait Implicits
  extends SyntaxImplicits[ReactiveNode, dom.Element, dom.Node, dom.Event, JsCallback]
  with KeyImplicits[ReactiveNode, dom.Element, dom.Node]
  with SetterBuilders[ReactiveNode, dom.Element, dom.Node]
  with DomApi
{

  @inline implicit def toAttrReceiver[V](attr: Attr[V]
  ): AttrReceiver[V] = {
    new AttrReceiver(attr)
  }

  @inline implicit def toStyleReceiver[V](
    style: Style[V]
  ): StyleReceiver[V] = {
    new StyleReceiver(style)
  }

  @inline implicit def toEventEmitter[Ev <: dom.Event](
    eventProp: EventProp[Ev]
  ): EventEmitter[Ev] = {
    new EventEmitter(eventProp)
  }

  @inline implicit def textToNode(text: String): ReactiveText = {
    new ReactiveText(text)
  }

  //  @inline implicit def optionToModifier[T](
  //    maybeModifier: Option[T]
  //  )(
  //    implicit toModifier: T => Modifier[RNode, RNodeData]
  //  ): Modifier[RNode, RNodeData] = {
  //    Conversions.optionToModifier(maybeModifier)
  //  }
}
