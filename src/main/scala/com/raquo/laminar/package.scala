package com.raquo

import com.raquo.snabbdom.EventCallback
import com.raquo.snabbdom._
import com.raquo.snabbdom.collections.attrs.{Attrs, GlobalAttrs, InputAttrs}
import com.raquo.snabbdom.collections.eventProps.{ClipboardEventProps, KeyboardEventProps, MouseEventProps}
import com.raquo.snabbdom.collections.props.Props
import com.raquo.snabbdom.collections.styles.Styles
import com.raquo.snabbdom.collections.tags.{Tags, Tags2}
import com.raquo.snabbdom.nodes.{ChildNode, Conversions, IterableNode, NodeData}
import com.raquo.snabbdom.setters.{Attr, EventProp, Style}
import org.scalajs.dom
import org.scalajs.dom.raw.Event

import scala.scalajs.js

package object laminar {

  private val modules = js.Array(AttrsModule, PropsModule, EventsModule, StyleModule)

  val patch: Snabbdom.PatchFn[RNode] = Snabbdom.init(modules)

  object tags extends Tags[RNode] with ReactiveBuilders

  object allTags extends Tags[RNode] with Tags2[RNode] with ReactiveBuilders

  object attrs extends Attrs[RNode] with InputAttrs[RNode] with GlobalAttrs[RNode] with ReactiveBuilders

  object props extends Props[RNode] with ReactiveBuilders // @TODO add more `with`?

  object events extends MouseEventProps[RNode] with KeyboardEventProps[RNode] with ClipboardEventProps[RNode] with ReactiveBuilders

  object styles extends Styles[RNode] with ReactiveBuilders

  def render(entryPoint: dom.Element, rootNode: RNode): Unit = {
    patch(entryPoint, rootNode)
  }


  // @TODO[API,Elegance] - ADD TYPE PARAM TO NODE INSTEAD !!!
  implicit def toRichData(data: NodeData[RNode]): RichRNodeData = {
    data.asInstanceOf[RichRNodeData]
  }


  val child = ChildReceiver

  implicit def toAttrReceiver[V](attr: Attr[V, RNode]): AttrReceiver[V] = {
    new AttrReceiver(attr)
  }

  implicit def toStyleReceiver[V](style: Style[V, RNode]): StyleReceiver[V] = {
    new StyleReceiver(style)
  }

  implicit def toEventEmitter[Ev <: Event](eventProp: EventProp[EventCallback[Ev], RNode]): EventEmitter[Ev] = {
    new EventEmitter(eventProp)
  }



  implicit val builders = new ReactiveBuilders {}

  @inline implicit def textToChildNode(text: String): ChildNode[RNode] = {
    Conversions.textToChildNode(text)
  }

  @inline implicit def nodeToChildNode(node: RNode): ChildNode[RNode] = {
    Conversions.nodeToChildNode(node)
  }

  @inline implicit def toIterableNode(modifiers: Iterable[Modifier[RNode]]): IterableNode[RNode] = {
    Conversions.toIterableNode(modifiers)
  }

  @inline implicit def optionToModifier[T](maybeModifier: Option[T])(implicit toModifier: T => Modifier[RNode]): Modifier[RNode] = {
    Conversions.optionToModifier(maybeModifier)
  }

}
