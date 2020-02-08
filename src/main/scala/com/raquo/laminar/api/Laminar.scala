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
import com.raquo.laminar.defs._
import com.raquo.laminar.keys._
import com.raquo.laminar.lifecycle.MountContext
import com.raquo.laminar.nodes
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
  with DocumentTags[HtmlTag]
  with EmbedTags[HtmlTag]
  with FormTags[HtmlTag]
  with GroupingTags[HtmlTag]
  with MiscTags[HtmlTag]
  with SectionTags[HtmlTag]
  with TableTags[HtmlTag]
  with TextTags[HtmlTag]
  // Other things
  with HtmlBuilders
  with Implicits {

  object aria
    extends AriaAttrs[ReactiveHtmlAttr]
    with HtmlBuilders

  object svg
    extends SvgTags[SvgTag]
    with ReactiveComplexSvgKeys
    with SvgAttrs[ReactiveSvgAttr]
    with SvgBuilders

  object documentEvents
    extends DomEventStreamPropBuilder(dom.document)
    with DocumentEventProps[EventStream]

  object windowEvents
    extends DomEventStreamPropBuilder(dom.window)
    with WindowEventProps[EventStream]

  /** An owner that never kills its possessions.
    *
    * Be careful to only use for subscriptions whose lifetime should match
    * the lifetime of `dom.window` (that is, of your whole application, basically).
    *
    * Legitimate use case: for app-wide observers of [[documentEvents]] and [[windowEvents]].
    */
  object unsafeWindowOwner extends Owner {}

  // Base elements and nodes

  type HtmlElement = nodes.ReactiveHtmlElement.Base

  type SvgElement = nodes.ReactiveSvgElement.Base

  type Element = nodes.ReactiveElement.Base

  type Node = nodes.ChildNode.Base

  type TextNode = nodes.TextNode

  type CommentNode = nodes.CommentNode

  type RootNode = nodes.RootNode

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

  type Anchor = nodes.ReactiveHtmlElement[dom.html.Anchor]

  type Button = nodes.ReactiveHtmlElement[dom.html.Button]

  type Div = nodes.ReactiveHtmlElement[dom.html.Div]

  type IFrame = nodes.ReactiveHtmlElement[dom.html.IFrame]

  type Image = nodes.ReactiveHtmlElement[dom.html.Image]

  type Input = nodes.ReactiveHtmlElement[dom.html.Input]

  type Label = nodes.ReactiveHtmlElement[dom.html.Label]

  type Li = nodes.ReactiveHtmlElement[dom.html.LI]

  type Span = nodes.ReactiveHtmlElement[dom.html.Span]

  type TextArea = nodes.ReactiveHtmlElement[dom.html.TextArea]


  /** Note: this is not a [[nodes.ReactiveElement]] because [[dom.Comment]] is not a [[dom.Element]].
    * This is a bit annoying, I know, but we kinda have to follow the native JS DOM API on this.
    * Both [[CommentNode]] and [[nodes.ReactiveElement]] are [[Node]] aka [[Child]].
    */
  @inline def emptyNode: CommentNode = commentNode("")

  def commentNode(text: String = ""): CommentNode = new CommentNode(text)


  val focus: FocusReceiver.type = FocusReceiver

  val child: ChildReceiver.type = ChildReceiver

  val children: ChildrenReceiver.type = ChildrenReceiver


  @inline def render(
    container: dom.Element,
    rootNode: nodes.ReactiveElement.Base
  ): RootNode = {
    new RootNode(container, rootNode)
  }

  def onMount[El <: nodes.ReactiveElement[Ref], Ref <: dom.Element](fn: MountContext[El, Ref] => Unit): Modifier[El] = {
    new Modifier[El] {
      override def apply(element: El): Unit = {
        element.onMount(fn)
      }
    }
  }

  def onUnmount[El <: nodes.ReactiveElement.Base](fn: El => Unit): Modifier[El] = {
    new Modifier[El] {
      override def apply(element: El): Unit = {
        element.onUnmount(fn)
      }
    }
  }

  def onLifecycle[El <: nodes.ReactiveElement[Ref], Ref <: dom.Element](
    mount: MountContext[El, Ref] => Unit,
    unmount: El => Unit
  ): Modifier[El] = {
    new Modifier[El] {
      override def apply(element: El): Unit = {
        element.onLifecycle(mount, unmount)
      }
    }
  }

  def inContext[El <: nodes.ReactiveElement.Base](makeModifier: El => Modifier[El]): Modifier[El] = {
    new Modifier[El] {
      override def apply(element: El): Unit = makeModifier(element).apply(element)
    }
  }

}
