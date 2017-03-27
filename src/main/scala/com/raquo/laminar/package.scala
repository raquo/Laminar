package com.raquo

import com.raquo.laminar.receivers.{AttrReceiver, ChildReceiver, StyleReceiver}
import com.raquo.laminar.subscriptions.EventEmitter
import com.raquo.snabbdom._
import com.raquo.snabbdom.collections.attrs.{Attrs, GlobalAttrs, InputAttrs}
import com.raquo.snabbdom.collections.eventProps.{ClipboardEventProps, KeyboardEventProps, MouseEventProps}
import com.raquo.snabbdom.collections.props.Props
import com.raquo.snabbdom.collections.styles.Styles
import com.raquo.snabbdom.collections.tags.{Tags, Tags2}
import com.raquo.snabbdom.hooks.ModuleHooks
import com.raquo.snabbdom.nodes.{ChildNode, Conversions, IterableNode}
import com.raquo.snabbdom.setters.{Attr, EventProp, Style}
import org.scalajs.dom
import org.scalajs.dom.raw.Event

import scala.scalajs.js
import scala.scalajs.js.|

package object laminar {

  private val modules: js.Array[NativeModule | ModuleHooks[RNode, RNodeData]] = js.Array(
    AttrsModule,
    PropsModule,
    EventsModule,
    StyleModule
  )

  val patch: Snabbdom.PatchFn[RNode, RNodeData] = Snabbdom.init(modules)

  object tags
    extends Tags[RNode, RNodeData]
    with ReactiveBuilders

  object allTags
    extends Tags[RNode, RNodeData]
    with Tags2[RNode, RNodeData]
    with ReactiveBuilders

  object attrs
    extends Attrs[RNode, RNodeData]
    with InputAttrs[RNode, RNodeData]
    with GlobalAttrs[RNode, RNodeData]
    with ReactiveBuilders

  object props extends Props[RNode, RNodeData] with ReactiveBuilders // @TODO add more `with`?

  object events
    extends MouseEventProps[RNode, RNodeData]
    with KeyboardEventProps[RNode, RNodeData]
    with ClipboardEventProps[RNode, RNodeData]
    with ReactiveBuilders

  object styles extends Styles[RNode, RNodeData] with ReactiveBuilders

  def render(entryPoint: dom.Element, rootNode: RNode): Unit = {
    patch(entryPoint, rootNode)
  }


  val child = ChildReceiver

  implicit def toAttrReceiver[V](
    attr: Attr[V, RNode, RNodeData]
  ): AttrReceiver[V] = {
    new AttrReceiver(attr)
  }

  implicit def toStyleReceiver[V](
    style: Style[V, RNode, RNodeData]
  ): StyleReceiver[V] = {
    new StyleReceiver(style)
  }

  implicit def toEventEmitter[Ev <: Event](
    eventProp: EventProp[EventCallback[Ev], RNode, RNodeData]
  ): EventEmitter[Ev] = {
    new EventEmitter(eventProp)
  }



  implicit val builders = new ReactiveBuilders {}

  @inline implicit def textToChildNode(
    text: String
  ): ChildNode[RNode, RNodeData] = {
    Conversions.textToChildNode(text)
  }

  @inline implicit def nodeToChildNode(
    node: RNode
  ): ChildNode[RNode, RNodeData] = {
    Conversions.nodeToChildNode(node)
  }

  @inline implicit def toIterableNode(
    modifiers: Iterable[Modifier[RNode, RNodeData]]
  ): IterableNode[RNode, RNodeData] = {
    Conversions.toIterableNode(modifiers)
  }

  @inline implicit def optionToModifier[T](
    maybeModifier: Option[T]
  )(
    implicit toModifier: T => Modifier[RNode, RNodeData]
  ): Modifier[RNode, RNodeData] = {
    Conversions.optionToModifier(maybeModifier)
  }
}
