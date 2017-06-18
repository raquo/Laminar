package com.raquo

import com.raquo.dombuilder.generic.builders.{BoundedBuilder, SpecializedBuilder}
import com.raquo.dombuilder.generic.defs.attrs.{Attrs, GlobalAttrs, InputAttrs}
import com.raquo.dombuilder.generic.defs.eventProps.{ClipboardEventProps, ErrorEventProps, FormEventProps, KeyboardEventProps, MouseEventProps}
import com.raquo.dombuilder.generic.defs.props.{NodeProps, Props}
import com.raquo.dombuilder.generic.defs.tags._
import com.raquo.dombuilder.generic.keys.{Attr, Prop, Style}
import com.raquo.dombuilder.jsdom
import com.raquo.dombuilder.jsdom.nodes.ChildNode
import com.raquo.laminar.builders.{ReactiveTag, ReactiveTagBuilder}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveNode, ReactiveRoot, ReactiveText}
import com.raquo.laminar.receivers.{AttrReceiver, ChildReceiver, ChildrenReceiver, MaybeChildReceiver, StyleReceiver}
import com.raquo.laminar.subscriptions.EventEmitter
import org.scalajs.dom
import org.scalajs.dom.raw.Event

package object laminar {

  object tags
    extends DocumentTags[ReactiveTag, dom.Element, dom.html.Html, dom.html.Head, dom.html.Base, dom.html.Link, dom.html.Meta, dom.html.Script, dom.html.Element]
    with GroupingTags[ReactiveTag, dom.Element, dom.html.Paragraph, dom.html.HR, dom.html.Pre, dom.html.Quote, dom.html.OList, dom.html.UList, dom.html.LI, dom.html.DList, dom.html.DT, dom.html.DD, dom.html.Element, dom.html.Div]
    with TextTags[ReactiveTag, dom.Element, dom.html.Anchor, dom.html.Element, dom.html.Span, dom.html.BR, dom.html.Mod]
    with FormTags[ReactiveTag, dom.Element, dom.html.Form, dom.html.FieldSet, dom.html.Legend, dom.html.Label, dom.html.Input, dom.html.Button, dom.html.Select, dom.html.DataList, dom.html.OptGroup, dom.html.Option, dom.html.TextArea]
    with SectionTags[ReactiveTag, dom.Element, dom.html.Body, dom.html.Element, dom.html.Heading]
    with EmbedTags[ReactiveTag, dom.Element, dom.html.Image, dom.html.IFrame, dom.html.Embed, dom.html.Object, dom.html.Param, dom.html.Video, dom.html.Audio, dom.html.Source, dom.html.Track, dom.html.Canvas, dom.html.Map, dom.html.Area]
    with TableTags[ReactiveTag, dom.Element, dom.html.Table, dom.html.TableCaption, dom.html.TableCol, dom.html.TableSection, dom.html.TableRow, dom.html.TableCell, dom.html.TableHeaderCell]
    with ReactiveTagBuilder

  object tags2
    extends MiscTags[ReactiveTag, dom.Element, dom.html.Title, dom.html.Style, dom.html.Element, dom.html.Quote, dom.html.Progress, dom.html.Menu]
    with ReactiveTagBuilder

  object attrs
    extends Attrs[Attr]
    with InputAttrs[Attr]
    with GlobalAttrs[Attr]
    with SpecializedBuilder[Attr]
  {
    override def build[V](key: String): Attr[V] = {
      new Attr[V](key)
    }
  }

  object props
    extends Props[Prop]
    with NodeProps[Prop]
    with SpecializedBuilder[Prop]
  {
    override def build[V](key: String): Prop[V] = {
      new Prop(key)
    }
  }

  object events
    extends MouseEventProps[jsdom.keys.EventProp, dom.Event, dom.MouseEvent]
      with FormEventProps[jsdom.keys.EventProp, dom.Event]
      with KeyboardEventProps[jsdom.keys.EventProp, dom.Event, dom.KeyboardEvent]
      with ClipboardEventProps[jsdom.keys.EventProp, dom.Event]
      with ErrorEventProps[jsdom.keys.EventProp, dom.Event, dom.ErrorEvent]
      with BoundedBuilder[jsdom.keys.EventProp, dom.Event]
  {
    override def build[Ev <: dom.Event](key: String): jsdom.keys.EventProp[Ev] = {
      new jsdom.keys.EventProp[Ev](name = key)
    }
  }

  object styles
    extends jsdom.defs.styles.Styles
    with jsdom.builders.StyleBuilder

  object styles2
    extends jsdom.defs.styles.Styles2
    with jsdom.builders.StyleBuilder

  val child: ChildReceiver.type = ChildReceiver

  val maybeChild: MaybeChildReceiver.type = MaybeChildReceiver

  val children: ChildrenReceiver.type = ChildrenReceiver

  def render(
    container: dom.Element,
    rootNode: ReactiveNode with ChildNode[ReactiveNode, dom.Element]
  ): ReactiveRoot = {
    new ReactiveRoot(container, rootNode)
  }

  implicit def toAttrReceiver[V](
    attr: Attr[V]
  ): AttrReceiver[V] = {
    new AttrReceiver(attr)
  }

  implicit def toStyleReceiver[V](
    style: Style[V]
  ): StyleReceiver[V] = {
    new StyleReceiver(style)
  }

  implicit def toEventEmitter[Ev <: Event](
    eventProp: jsdom.keys.EventProp[Ev]
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
