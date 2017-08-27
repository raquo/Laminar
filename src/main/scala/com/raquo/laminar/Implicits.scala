package com.raquo.laminar

import com.raquo.dombuilder.jsdom.syntax

import com.raquo.domtypes.generic.keys.{Attr, EventProp, Style}

import com.raquo.laminar.nodes.ReactiveText
import com.raquo.laminar.receivers.{AttrReceiver, StyleReceiver}
import com.raquo.laminar.subscriptions.EventEmitter

import org.scalajs.dom

trait Implicits extends syntax.Implicits {

  implicit def toAttrReceiver[V](attr: Attr[V]
  ): AttrReceiver[V] = {
    new AttrReceiver(attr)
  }

  implicit def toStyleReceiver[V](
    style: Style[V]
  ): StyleReceiver[V] = {
    new StyleReceiver(style)
  }

  implicit def toEventEmitter[Ev <: dom.Event](
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

/** You can import this to load implicits if you don't want to `import com.raquo.laminar._` */
object Implicits extends Implicits
