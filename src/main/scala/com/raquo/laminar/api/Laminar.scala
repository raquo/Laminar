package com.raquo.laminar.api

import com.raquo.domtypes
import com.raquo.domtypes.generic.defs.attrs.{AriaAttrs, HtmlAttrs, SvgAttrs}
import com.raquo.domtypes.generic.defs.props.Props
import com.raquo.domtypes.generic.defs.reflectedAttrs.ReflectedHtmlAttrs
import com.raquo.domtypes.generic.defs.styles.{Styles, Styles2}
import com.raquo.domtypes.jsdom.defs.eventProps._
import com.raquo.domtypes.jsdom.defs.tags._
import com.raquo.laminar.Implicits
import com.raquo.laminar.builders._
import com.raquo.laminar.collection.CollectionCommand
import com.raquo.laminar.defs._
import com.raquo.laminar.keys._
import com.raquo.laminar.nodes._
import com.raquo.laminar.receivers._
import com.raquo.laminar.setters.{ChildrenCommandSetter, ChildrenSetter}
import org.scalajs.dom

// @TODO[Performance] Check if order of traits matters for quicker access (given trait linearization). Not sure how it's encoded in JS.

private[laminar] object Laminar
  extends Airstream
  with ReactiveComplexHtmlKeys
  // Reflected Attrs
  with ReflectedHtmlAttrs[ReactiveReflectedProp]
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

  object documentEvents
    extends DomEventStreamPropBuilder(dom.document)
    with DocumentEventProps[DomEventStream]

  object windowEvents
    extends DomEventStreamPropBuilder(dom.window)
    with WindowEventProps[DomEventStream]

  /** An owner that never kills its possessions.
    *
    * Be careful to only use for subscriptions whose lifetime should match
    * the lifetime of `dom.window` (that is, of your whole application, basically).
    *
    * Legitimate use case: for app-wide observers of [[documentEvents]] and [[windowEvents]].
    */
  object unsafeWindowOwner extends Owner {}

  // Base elements and nodes

  type HtmlElement = ReactiveHtmlElement[dom.html.Element]

  type SvgElement = ReactiveSvgElement[dom.svg.Element]

  type Element = ReactiveElement[dom.Element]

  type Node = ReactiveChildNode[dom.Node]

  type Text = ReactiveText

  type Comment = ReactiveComment

  type Root = ReactiveRoot

  type Child = ChildrenSetter.Child

  type Children = ChildrenSetter.Children

  type ChildrenCommand = ChildrenCommandSetter.ChildrenCommand


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


  /** Note: this is not a [[ReactiveElement]] because [[dom.Comment]] is not a [[dom.Element]].
    * This is a bit annoying, I know.
    * Both [[ReactiveComment]] and [[ReactiveElement]] are `Child` aka `Node`.
    */
  val emptyNode: ReactiveComment = new ReactiveComment("")


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
