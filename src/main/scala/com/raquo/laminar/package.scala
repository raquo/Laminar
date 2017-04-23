package com.raquo

import com.raquo.dombuilder.Root
import com.raquo.dombuilder.builders.{AttrBuilder, EventPropBuilder, PropBuilder, StyleBuilder}
import com.raquo.dombuilder.definitions.attrs.{Attrs, GlobalAttrs, InputAttrs}
import com.raquo.dombuilder.definitions.eventProps.{ClipboardEventProps, FormEventProps, KeyboardEventProps, MouseEventProps, SharedEventProps}
import com.raquo.dombuilder.definitions.props.{NodeProps, Props}
import com.raquo.dombuilder.definitions.styles.{Styles, Styles2}
import com.raquo.dombuilder.definitions.tags.{Tags, Tags2}
import com.raquo.dombuilder.jsdom.domapi.{JsCommentApi, JsElementApi, JsEventApi, JsNodeApi, JsTextApi, JsTreeApi}
import com.raquo.dombuilder.keys.{Attr, EventProp, Style}
import com.raquo.laminar.builders.{ReactiveCommentBuilder, ReactiveTagBuilder, ReactiveTextBuilder}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveNode, ReactiveText}
import com.raquo.laminar.receivers.{AttrReceiver, ChildReceiver, ChildrenReceiver, MaybeChildReceiver, StyleReceiver}
import com.raquo.laminar.subscriptions.EventEmitter
import org.scalajs.dom
import org.scalajs.dom.raw.Event

import scala.scalajs.js

package object laminar {

  type ChildNode = dombuilder.nodes.ChildNode[ReactiveNode, dom.Node, dom.Node]

  type ParentNode = dombuilder.nodes.ParentNode[ReactiveNode, dom.Element, dom.Node]

  val commentApi: JsCommentApi[ReactiveNode] = new JsCommentApi[ReactiveNode] {}

  val elementApi: JsElementApi[ReactiveNode] = new JsElementApi[ReactiveNode] {}

  val eventApi: JsEventApi[ReactiveNode] = new JsEventApi[ReactiveNode] {}

  val nodeApi: JsNodeApi[ReactiveNode] = new JsNodeApi[ReactiveNode] {}

  val textNodeApi: JsTextApi[ReactiveNode] = new JsTextApi[ReactiveNode] {}

  val treeApi: JsTreeApi[ReactiveNode] = new JsTreeApi[ReactiveNode] {}

  val commentBuilder: ReactiveCommentBuilder = new ReactiveCommentBuilder {}

  val tagBuilder: ReactiveTagBuilder = new ReactiveTagBuilder {}

  val textBuilder: ReactiveTextBuilder = new ReactiveTextBuilder {}

  object tags
    extends Tags[ReactiveElement, ReactiveNode, dom.Element, dom.Node]
    with ReactiveTagBuilder

  object tags2
    extends Tags2[ReactiveElement, ReactiveNode, dom.Element, dom.Node]
    with ReactiveTagBuilder

  object attrs
    extends Attrs[ReactiveNode, dom.Element, dom.Node]
    with InputAttrs[ReactiveNode, dom.Element, dom.Node]
    with GlobalAttrs[ReactiveNode, dom.Element, dom.Node]
    with AttrBuilder[ReactiveNode, dom.Element, dom.Node]

  object props
    extends Props[ReactiveNode]
    with PropBuilder[ReactiveNode]

  object nodeProps
    extends NodeProps[ReactiveNode]
    with PropBuilder[ReactiveNode]

  object events
    extends MouseEventProps[ReactiveNode, dom.MouseEvent, dom.Event, js.Function1]
    with FormEventProps[ReactiveNode, dom.Event, js.Function1]
    with KeyboardEventProps[ReactiveNode, dom.KeyboardEvent, dom.Event, js.Function1]
    with ClipboardEventProps[ReactiveNode, dom.Event, js.Function1]
    with EventPropBuilder[ReactiveNode, dom.Event, js.Function1] // @TODO add more `with`?
    with SharedEventProps[ReactiveNode, dom.ErrorEvent, dom.Event, js.Function1]
  {
    override val eventApi: JsEventApi[ReactiveNode] = laminar.eventApi
  }

  object styles
    extends Styles[ReactiveNode]
    with StyleBuilder[ReactiveNode]

  object styles2
    extends Styles2[ReactiveNode]
    with StyleBuilder[ReactiveNode]

  val child: ChildReceiver.type = ChildReceiver

  val maybeChild: MaybeChildReceiver.type = MaybeChildReceiver

  val children: ChildrenReceiver.type = ChildrenReceiver

  def render(container: dom.Element, rootNode: ReactiveElement): Unit = {
    Root.mount(container, rootNode, treeApi)
  }

  implicit def toAttrReceiver[V](
    attr: Attr[V, ReactiveNode, dom.Element, dom.Node]
  ): AttrReceiver[V] = {
    new AttrReceiver(attr)
  }

  implicit def toStyleReceiver[V](
    style: Style[V, ReactiveNode]
  ): StyleReceiver[V] = {
    new StyleReceiver(style)
  }

  implicit def toEventEmitter[Ev <: Event](
    eventProp: EventProp[Ev, ReactiveNode, dom.Event, js.Function1]
  ): EventEmitter[Ev] = {
    new EventEmitter(eventProp)
  }

  @inline implicit def textToNode(text: String): ReactiveText = {
    textBuilder.createNode(text)
  }

//  @inline implicit def optionToModifier[T](
//    maybeModifier: Option[T]
//  )(
//    implicit toModifier: T => Modifier[RNode, RNodeData]
//  ): Modifier[RNode, RNodeData] = {
//    Conversions.optionToModifier(maybeModifier)
//  }
}
