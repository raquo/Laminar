package com.raquo.laminar.api

import com.raquo.domtypes
import com.raquo.domtypes.generic.defs.attrs.{AriaAttrs, HtmlAttrs, SvgAttrs}
import com.raquo.domtypes.generic.defs.props.Props
import com.raquo.domtypes.generic.defs.reflectedAttrs.ReflectedHtmlAttrs
import com.raquo.domtypes.generic.defs.styles.{Styles, Styles2}
import com.raquo.domtypes.jsdom.defs.eventProps.{ClipboardEventProps, ErrorEventProps, FormEventProps, KeyboardEventProps, MediaEventProps, MiscellaneousEventProps, MouseEventProps, WindowOnlyEventProps}
import com.raquo.domtypes.jsdom.defs.tags.{DocumentTags, EmbedTags, FormTags, GroupingTags, MiscTags, SectionTags, SvgTags, TableTags, TextTags}
import com.raquo.laminar.Implicits
import com.raquo.laminar.builders.{ReactiveHtmlBuilders, ReactiveHtmlTag, ReactiveSvgBuilders, ReactiveSvgTag}
import com.raquo.laminar.defs.{ReactiveComplexHtmlKeys, ReactiveComplexSvgKeys}
import com.raquo.laminar.keys.{ReactiveEventProp, ReactiveHtmlAttr, ReactiveProp, ReactiveReflectedHtmlAttr, ReactiveStyle, ReactiveSvgAttr}
import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveComment, ReactiveElement, ReactiveHtmlElement, ReactiveRoot, ReactiveSvgElement, ReactiveText}
import com.raquo.laminar.receivers.{ChildReceiver, ChildrenReceiver, FocusReceiver}
import org.scalajs.dom

// @TODO[Performance] Check if order of traits matters for quicker access (given trait linearization). Not sure how it's encoded in JS.

private[laminar] object Laminar
  extends Airstream
  with ReactiveComplexHtmlKeys
  // Reflected Attrs
  with ReflectedHtmlAttrs[ReactiveReflectedHtmlAttr]
  // Attrs
  with HtmlAttrs[ReactiveHtmlAttr]
  // Event Props
  with ClipboardEventProps[ReactiveEventProp]
  with ErrorEventProps[ReactiveEventProp]
  with FormEventProps[ReactiveEventProp]
  with KeyboardEventProps[ReactiveEventProp]
  with MediaEventProps[ReactiveEventProp]
  with MiscellaneousEventProps[ReactiveEventProp]
  with MouseEventProps[ReactiveEventProp]
  with WindowOnlyEventProps[ReactiveEventProp]
  // Props
  with Props[ReactiveProp]
  // Styles
  with Styles[StyleSetter]
  with Styles2[StyleSetter]
  // Tags
  with DocumentTags[ReactiveHtmlTag]
  with EmbedTags[ReactiveHtmlTag]
  with FormTags[ReactiveHtmlTag]
  with GroupingTags[ReactiveHtmlTag]
  with MiscTags[ReactiveHtmlTag]
  with SectionTags[ReactiveHtmlTag]
  with TableTags[ReactiveHtmlTag]
  with TextTags[ReactiveHtmlTag]
  // Other things
  with ReactiveHtmlBuilders
  with Implicits {

  object aria
    extends AriaAttrs[ReactiveHtmlAttr]
    with ReactiveHtmlBuilders

  object svg
    extends SvgTags[ReactiveSvgTag]
    with ReactiveComplexSvgKeys
    with SvgAttrs[ReactiveSvgAttr]
    with ReactiveSvgBuilders


  // Base elements and nodes

  type HtmlElement = ReactiveHtmlElement[dom.html.Element]

  type SvgElement = ReactiveSvgElement[dom.svg.Element]

  type Element = ReactiveElement[dom.Element]

  type Node = ReactiveChildNode[dom.Node]

  type Text = ReactiveText

  type Comment = ReactiveComment

  type Root = ReactiveRoot


  // Modifiers

  type Mod[El] = domtypes.generic.Modifier[El]

  type Modifier[-El] = domtypes.generic.Modifier[El]


  // Keys

  type EventProp[Ev <: dom.Event] = ReactiveEventProp[Ev]

  type HtmlAttr[V] = ReactiveHtmlAttr[V]

  type Prop[V, DomV] = ReactiveProp[V, DomV]

  type Style[V] = ReactiveStyle[V]

  type SvgAttr[V] = ReactiveSvgAttr[V]


  // Specific HTML elements

  type Anchor = ReactiveHtmlElement[dom.html.Anchor]

  type Button = ReactiveHtmlElement[dom.html.Button]

  type Div = ReactiveHtmlElement[dom.html.Div]

  type IFrame = ReactiveHtmlElement[dom.html.IFrame]

  type Image = ReactiveHtmlElement[dom.html.Image]

  type Input = ReactiveHtmlElement[dom.html.Input]

  type Label = ReactiveHtmlElement[dom.html.Label]

  type Li = ReactiveHtmlElement[dom.html.LI]

  type Span = ReactiveHtmlElement[dom.html.Span]

  type TextArea = ReactiveHtmlElement[dom.html.TextArea]


  val focus: FocusReceiver.type = FocusReceiver

  val child: ChildReceiver.type = ChildReceiver

  val children: ChildrenReceiver.type = ChildrenReceiver

  @inline def render(
    container: dom.Element,
    rootNode: ReactiveChildNode[dom.Element]
  ): ReactiveRoot = {
    new ReactiveRoot(container, rootNode)
  }

  def inContext[El <: ReactiveElement[dom.Element]](makeModifier: El => Modifier[El]): Modifier[El] = {
    new Modifier[El] {
      override def apply(element: El): Unit = makeModifier(element).apply(element)
    }
  }

}
