package com.raquo.laminar.api

import com.raquo.airstream.web.DomEventStream
import com.raquo.laminar.defs.attrs.{AriaAttrs, HtmlAttrs, SvgAttrs}
import com.raquo.laminar.defs.complex.{ComplexHtmlKeys, ComplexSvgKeys}
import com.raquo.laminar.defs.eventProps.{DocumentEventProps, GlobalEventProps, WindowEventProps}
import com.raquo.laminar.defs.props.HtmlProps
import com.raquo.laminar.defs.styles.StyleProps
import com.raquo.laminar.defs.tags.{HtmlTags, SvgTags}
import com.raquo.laminar.inputs.InputController
import com.raquo.laminar.keys._
import com.raquo.laminar.modifiers.{EventListener, KeyUpdater}
import com.raquo.laminar.nodes.{DetachedRoot, ReactiveElement, ReactiveHtmlElement, ReactiveSvgElement}
import com.raquo.laminar.receivers._
import com.raquo.laminar.tags.{HtmlTag, SvgTag}
import com.raquo.laminar.{DomApi, nodes}
import org.scalajs.dom

// @TODO[Performance] Check if order of traits matters for quicker access (given trait linearization). Not sure how it's encoded in JS.

trait Laminar
  extends HtmlTags
    with HtmlAttrs
    with HtmlProps
    with GlobalEventProps
    with StyleProps
    with ComplexHtmlKeys
    with MountHooks // onMountFocus, onMountSet, onMountBind, OnMountCallback, OnUnmountCallback, etc.
    with AirstreamAliases
    with LaminarAliases
    with Implicits {

  /** Contains Helpers like `style.px(12)` // returns "12px" */
  object style
    extends StyleUnitsApi

  /** Contains ARIA attrs for accessibility */
  object aria
    extends AriaAttrs

  // @TODO[API] Add GlobalEventProps to SVG as well?
  //  Regular event props work fine, but they might be hard to import if you also import svg._
  /** Contains SVG tags and attrs */
  object svg
    extends SvgTags
      with SvgAttrs
      with ComplexSvgKeys


  // -- Document & window events --

  protected val documentEventProps = new GlobalEventProps with DocumentEventProps

  protected val windowEventProps = new GlobalEventProps with WindowEventProps

  /** Typical usage: documentEvents(_.onDomContentLoaded) */
  def documentEvents[Ev <: dom.Event, V](events: documentEventProps.type => EventProcessor[Ev, V]): EventStream[V] = {
    val p = events(documentEventProps)
    DomEventStream[Ev](dom.document, EventProcessor.eventProp(p).name, EventProcessor.shouldUseCapture(p))
      .collectOpt(EventProcessor.processor(p))
  }

  /** Typical usage: windowEvents(_.onOffline) */
  def windowEvents[Ev <: dom.Event, V](events: windowEventProps.type => EventProcessor[Ev, V]): EventStream[V] = {
    val p = events(windowEventProps)
    DomEventStream[Ev](dom.window, EventProcessor.eventProp(p).name, EventProcessor.shouldUseCapture(p))
      .collectOpt(EventProcessor.processor(p))
  }


  /** An owner that never kills its possessions.
    *
    * Be careful to only use for subscriptions whose lifetime should match
    * the lifetime of `dom.window` (that is, of your whole application, basically).
    *
    * Legitimate use case: for app-wide observers of [[documentEvents]] and [[windowEvents]].
    */
  object unsafeWindowOwner extends Owner {}


  // -- Rendering --

  /** Render a Laminar element into a container DOM node, right now.
    * You must make sure that the container node already exists
    * in the DOM, otherwise this method will throw.
    */
  @inline def render(
    container: dom.Element,
    rootNode: nodes.ReactiveElement.Base
  ): RootNode = {
    new RootNode(container, rootNode)
  }

  /** Wait for `DOMContentLoaded` event to fire, then render a Laminar
    * element into a container DOM node. This is probably what you want
    * to initialize your Laminar application on page load.
    *
    * See https://developer.mozilla.org/en-US/docs/Web/API/Window/DOMContentLoaded_event
    */
  @inline def renderOnDomContentLoaded(
    container: => dom.Element,
    rootNode: => nodes.ReactiveElement.Base
  ): Unit = {
    documentEvents(_.onDomContentLoaded).foreach { _ =>
      new RootNode(container, rootNode)
    }(unsafeWindowOwner)
  }

  /** Wrap a Laminar element in [[DetachedRoot]], which allows you to
    * manually activate and deactivate Laminar subscriptions on this
    * element. Pass `activateNow = true` to activate the subscriptions
    * upon calling this method, or call `.activate()` manually.
    *
    * Unlike other `render*` methods, this one does NOT attach the element
    * to any container / parent DOM node. Instead, you can obtain the JS DOM
    * node as `.ref`, and pass it to a third party JS library that requires
    * you to provide a DOM node (which it will attach to the DOM on its own).
    */
  def renderDetached[El <: ReactiveElement.Base](
    rootNode: => El,
    activateNow: Boolean
  ): DetachedRoot[El] = {
    new DetachedRoot(rootNode, activateNow)
  }

  /**
    * Get a Seq of modifiers. You could just use Seq(...), but this helper
    * has better type inference in some cases.
    */
  def modSeq[El <: Element](mods: Modifier[El]*): Seq[Modifier[El]] = mods

  /**
    * Get a Seq of nodes. You could just use Seq(...), but this helper
    * has better type inference in some cases.
    */
  def nodeSeq[El <: Node](nodes: Node*): Seq[Node] = nodes

  /** A universal Modifier that does nothing */
  val emptyMod: Modifier.Base = Modifier.empty

  /** Note: this is not a [[nodes.ReactiveElement]] because [[dom.Comment]] is not a [[dom.Element]].
    * This is a bit annoying, I know, but we kinda have to follow the native JS DOM API on this.
    * Both [[CommentNode]] and [[nodes.ReactiveElement]] are [[Node]].
    */
  @inline def emptyNode: CommentNode = commentNode("")

  def commentNode(text: String = ""): CommentNode = new CommentNode(text)

  /** Non-breaking space character
    *
    * Note: In Laminar you don't need to use html entities, you can just
    * insert the actual character you want, e.g. "»" instead of "&raquo;"
    */
  val nbsp: String = "\u00a0"

  /** Wrap an HTML JS DOM element created by an external library into a reactive Laminar element. */
  def foreignHtmlElement[Ref <: dom.html.Element](tag: HtmlTag[Ref], element: Ref): ReactiveHtmlElement[Ref] = {
    DomApi.assertTagMatches(tag, element, "Unable to init foreign html element")
    new ReactiveHtmlElement[Ref](tag, element)
  }

  /** Wrap an HTML JS DOM element created by an external library into a reactive Laminar element. */
  def foreignHtmlElement(element: dom.html.Element): ReactiveHtmlElement.Base = {
    val tag = new HtmlTag(ReactiveElement.normalizeTagName(element)) // #Note: this tag instance is fake
    new ReactiveHtmlElement(tag, element)
  }

  /** Wrap an SVG JS DOM element created by an external library into a reactive Laminar element. */
  def foreignSvgElement[Ref <: dom.svg.Element](tag: SvgTag[Ref], element: Ref): ReactiveSvgElement[Ref] = {
    DomApi.assertTagMatches(tag, element, "Unable to init foreign svg element")
    new ReactiveSvgElement[Ref](tag, element)
  }

  /** Wrap an SVG JS DOM element created by an external library into a reactive Laminar element. */
  def foreignSvgElement(element: dom.svg.Element): ReactiveSvgElement[dom.svg.Element] = {
    val tag = new SvgTag(ReactiveElement.normalizeTagName(element)) // #Note: this tag instance is fake
    foreignSvgElement(tag, element)
  }


  val child: ChildReceiver.type = ChildReceiver

  val text: ChildTextReceiver.type = ChildTextReceiver

  val children: ChildrenReceiver.type = ChildrenReceiver

  /** Focus or blur an element: `focus <-- EventStream[Boolean]` (true = focus, false = blur) */
  val focus: FocusReceiver.type = FocusReceiver


  /** Use this when you need a reference to current element. See docs. */
  def inContext[El <: Element](makeModifier: El => Modifier[El]): Modifier[El] = {
    Modifier[El] { element => makeModifier(element).apply(element) }
  }


  /** Modifier that applies one or more modifiers if `condition` is true */
  def when[El <: Element](condition: Boolean)(mods: Modifier[El]*): Modifier[El] = {
    if (condition) {
      mods // implicitly converted to a single modifier
    } else {
      emptyMod
    }
  }

  /** Modifier that applies one or more modifiers if `condition` is true */
  @inline def whenNot[El <: Element](condition: Boolean)(mods: Modifier[El]*): Modifier[El] = when(!condition)(mods)


  /** Creates controlled input block.
    * See [[https://laminar.dev/documentation#controlled-inputs Controlled Inputs docs]]
    */
  def controlled[Ref <: dom.html.Element, Ev <: dom.Event, V](
    listener: EventListener[Ev, _],
    updater: KeyUpdater[ReactiveHtmlElement[Ref], HtmlProp[V, _], V]
  ): Binder[ReactiveHtmlElement[Ref]] = {
    InputController.controlled(listener, updater)
  }

  /** Creates controlled input block.
    * See [[https://laminar.dev/documentation#controlled-inputs Controlled Inputs docs]]
    */
  def controlled[Ref <: dom.html.Element, Ev <: dom.Event, V](
    updater: KeyUpdater[ReactiveHtmlElement[Ref], HtmlProp[V, _], V],
    listener: EventListener[Ev, _]
  ): Binder[ReactiveHtmlElement[Ref]] = {
    InputController.controlled(listener, updater)
  }
}
