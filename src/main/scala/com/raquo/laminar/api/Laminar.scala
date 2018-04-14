package com.raquo.laminar.api

import com.raquo.dombuilder.generic.builders.SetterBuilders
import com.raquo.domtypes.generic
import com.raquo.domtypes.generic.builders.canonical.{CanonicalAttrBuilder, CanonicalEventPropBuilder, CanonicalPropBuilder, CanonicalReflectedAttrBuilder, CanonicalSvgAttrBuilder}
import com.raquo.domtypes.generic.defs.attrs.{AriaAttrs, Attrs, SvgAttrs}
import com.raquo.domtypes.generic.defs.props.Props
import com.raquo.domtypes.generic.defs.reflectedAttrs.ReflectedAttrs
import com.raquo.domtypes.generic.defs.styles.{Styles, Styles2}
import com.raquo.domtypes.generic.keys.{Attr, EventProp, Prop, SvgAttr}
import com.raquo.domtypes.jsdom.defs.eventProps.{ClipboardEventProps, ErrorEventProps, FormEventProps, KeyboardEventProps, MediaEventProps, MiscellaneousEventProps, MouseEventProps, WindowOnlyEventProps}
import com.raquo.domtypes.jsdom.defs.tags.{DocumentTags, EmbedTags, FormTags, GroupingTags, MiscTags, SectionTags, SvgTags, TableTags, TextTags}
import com.raquo.laminar.DomApi
import com.raquo.laminar.builders.{ReactiveHtmlTag, ReactiveHtmlTagBuilder, ReactiveSvgTag, ReactiveSvgTagBuilder}
import com.raquo.laminar.implicits.Implicits
import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveElement, ReactiveHtmlElement, ReactiveNode, ReactiveRoot, ReactiveSvgElement}
import com.raquo.laminar.receivers.{ChildReceiver, ChildrenReceiver, FocusReceiver}
import org.scalajs.dom


private[laminar] object Laminar
  // Attrs
  extends Attrs[Attr]
  with AriaAttrs[Attr]
  // Event Props
  with ClipboardEventProps[EventProp]
  with ErrorEventProps[EventProp]
  with FormEventProps[EventProp]
  with KeyboardEventProps[EventProp]
  with MediaEventProps[EventProp]
  with MiscellaneousEventProps[EventProp]
  with MouseEventProps[EventProp]
  with WindowOnlyEventProps[EventProp]
  // Props
  with Props[Prop]
  // Reflected Attrs
  with ReflectedAttrs[ReflectedAttr]
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
  // Builders
  with CanonicalAttrBuilder
  with CanonicalReflectedAttrBuilder
  with CanonicalEventPropBuilder[dom.Event]
  with CanonicalPropBuilder
  with ReactiveHtmlTagBuilder
  with SetterBuilders[ReactiveNode, dom.html.Element, dom.svg.Element, dom.Node]
  // Other things
  with DomApi
  with Implicits {

  object svg
    extends SvgTags[ReactiveSvgTag]
    with SvgAttrs[SvgAttr]
    with CanonicalSvgAttrBuilder
    with ReactiveSvgTagBuilder


  type HtmlElement = ReactiveHtmlElement[dom.html.Element]

  type SvgElement = ReactiveSvgElement[dom.svg.Element]

  type Element = ReactiveElement[dom.Element]


  type Modifier[El <: Element] = generic.Modifier[El]


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
