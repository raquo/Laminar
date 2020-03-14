package com.raquo.laminar.api

import com.raquo.domtypes
import com.raquo.domtypes.generic.defs.attrs.{AriaAttrs, HtmlAttrs, SvgAttrs}
import com.raquo.domtypes.generic.defs.props.Props
import com.raquo.domtypes.generic.defs.reflectedAttrs.ReflectedHtmlAttrs
import com.raquo.domtypes.generic.defs.styles.{Styles, Styles2}
import com.raquo.domtypes.jsdom.defs.eventProps._
import com.raquo.domtypes.jsdom.defs.tags._
import com.raquo.laminar.builders._
import com.raquo.laminar.defs._
import com.raquo.laminar.keys._
import com.raquo.laminar.lifecycle.InsertContext
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.receivers._
import com.raquo.laminar.{Implicits, lifecycle, modifiers, nodes}
import org.scalajs.dom

// @TODO[Performance] Check if order of traits matters for quicker access (given trait linearization). Not sure how it's encoded in JS.

private[laminar] object Laminar
  extends Airstream
  with ReactiveComplexHtmlKeys
  // Reflected Attrs
  with ReflectedHtmlAttrs[ReactiveProp]
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

  type Child = modifiers.ChildrenInserter.Child

  type Children = modifiers.ChildrenInserter.Children

  type ChildrenCommand = modifiers.ChildrenCommandInserter.ChildrenCommand


  // Modifiers

  type Mod[-El] = domtypes.generic.Modifier[El]

  type Modifier[-El] = domtypes.generic.Modifier[El]

  type Setter[-El <: Element] = modifiers.Setter[El]

  val Setter: modifiers.Setter.type = modifiers.Setter

  type Binder[-El <: Element] = modifiers.Binder[El]

  val Binder: modifiers.Binder.type = modifiers.Binder

  type Inserter[-El <: Element] = modifiers.Inserter[El]


  // Lifecycle

  type MountContext[+El <: Element] = lifecycle.MountContext[El]

  type InsertContext[+El <: Element] = lifecycle.InsertContext[El]


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

  type Select = nodes.ReactiveHtmlElement[dom.html.Select]

  type Span = nodes.ReactiveHtmlElement[dom.html.Span]

  type TextArea = nodes.ReactiveHtmlElement[dom.html.TextArea]


  @inline def render(
    container: dom.Element,
    rootNode: nodes.ReactiveElement.Base
  ): RootNode = {
    new RootNode(container, rootNode)
  }


  /** Note: this is not a [[nodes.ReactiveElement]] because [[dom.Comment]] is not a [[dom.Element]].
    * This is a bit annoying, I know, but we kinda have to follow the native JS DOM API on this.
    * Both [[CommentNode]] and [[nodes.ReactiveElement]] are [[Node]] aka [[Child]].
    */
  @inline def emptyNode: CommentNode = commentNode("")

  def commentNode(text: String = ""): CommentNode = new CommentNode(text)


  val child: ChildReceiver.type = ChildReceiver

  val children: ChildrenReceiver.type = ChildrenReceiver


  /** Focus or blur an element: `focus <-- EventStream[Boolean]` (true = focus, false = blur) */
  val focus: FocusReceiver.type = FocusReceiver

  /** Focus this element on mount */
  val onMountFocus: Modifier[HtmlElement] = onMountCallback(_.thisNode.ref.focus())

  /** Set a property / attribute / style on mount.
    * Similarly to other onMount methods, you only need this when:
    * a) you need to access MountContext
    * b) you truly need this to only happen on mount
    *
    * Example usage: `onMountSet(ctx => someAttr := someValue(ctx))`. See docs for details.
    */
  def onMountSet[El <: Element](fn: MountContext[El] => Setter[El]): Modifier[El] = {
    onMountCallback(c => fn(c)(c.thisNode))
  }

  /** Bind a subscription on mount
    *
    * Example usage: `onMountBind(ctx => someAttr <-- someObservable(ctx))`. See docs for details.
    */
  @inline def onMountBind[El <: Element](fn: MountContext[El] => Binder[El]): Modifier[El] = {
    var maybeSubscription: Option[DynamicSubscription] = None
    onMountUnmountCallback(
      mount = { c =>
        val binder = fn(c)
        maybeSubscription = Some(binder.bind(c.thisNode))
      },
      unmount = { _ =>
        maybeSubscription.foreach(_.kill())
        maybeSubscription = None
      }
    )
  }

  /** Insert child node(s) on mount.
    *
    * Note: insert position is reserved as soon as this modifier is applied to the element.
    * Basically it will insert elements in the same position, where you'd expect, on every mount.
    *
    * Example usage: `onMountInsert(ctx => child <-- someObservable(ctx))`. See docs for details.
    */
  def onMountInsert[El <: Element](fn: MountContext[El] => Inserter[El]): Modifier[El] = {
    new Modifier[El] {
      override def apply(element: El): Unit = {
        var maybeSubscription: Option[DynamicSubscription] = None
        val lockedContext = InsertContext.reserveSpotContext[El](element)
        element.amend(
          onMountUnmountCallback[El](
            mount = { c =>
              val inserter = fn(c).withContext(lockedContext)
              maybeSubscription = Some(inserter.bind(c.thisNode))
            },
            unmount = { _ =>
              maybeSubscription.foreach(_.kill())
              maybeSubscription = None
            }
          )
        )
      }
    }
  }

  /** Execute a callback on mount. Good for integrating third party libraries.
    *
    * The callback runs on every mount, not just the first one.
    * - Therefore, don't bind any subscriptions inside that you won't manually unbind on unmount.
    *   - If you fail to unbind manually, you will have N copies of them after mounting this element N times.
    *   - Use onMountBind or onMountInsert for that.
    *
    * When the callback is called, the element is already mounted.
    *
    * If you apply this modifier to an element that is already mounted, the callback
    * will not fire until and unless it is unmounted and mounted again.
    */
  def onMountCallback[El <: Element](fn: MountContext[El] => Unit): Modifier[El] = {
    new Modifier[El] {
      override def apply(element: El): Unit = {
        var ignoreNextActivation = ReactiveElement.isActive(element)
        ReactiveElement.bindCallback[El](element) { c =>
          if (ignoreNextActivation) {
            ignoreNextActivation = false
          } else {
            fn(c)
          }
        }
      }
    }
  }

  /** Execute a callback on unmount. Good for integrating third party libraries.
    *
    * When the callback is called, the element is still mounted.
    *
    * If you apply this modifier to an element that is already unmounted, the callback
    * will not fire until and unless it is mounted and then unmounted again.
    */
  def onUnmountCallback[El <: Element](fn: El => Unit): Modifier[El] = {
    new Modifier[El] {
      override def apply(element: El): Unit = {
        ReactiveElement.bindSubscription(element) { c =>
          new Subscription(c.owner, cleanup = () => fn(element))
        }
      }
    }
  }

  /** Combines onMountCallback and onUnmountCallback for easier integration.
    *
    * Note that the same caveats apply as for those individual methods.
    */
  def onMountUnmountCallback[El <: Element](mount: MountContext[El] => Unit, unmount: El => Unit): Modifier[El] = {
    new Modifier[El] {
      override def apply(element: El): Unit = {
        var ignoreNextActivation = ReactiveElement.isActive(element)
        ReactiveElement.bindSubscription[El](element) { c =>
          if (ignoreNextActivation) {
            ignoreNextActivation = false
          } else {
            mount(c)
          }
          new Subscription(c.owner, cleanup = () => unmount(element))
        }
      }
    }
  }

  // @TODO[Naming] Find a better name for this. We now have actual MountContext classes that this has nothing to do with.
  /** Use this when you need a reference to current element. See docs. */
  def inContext[El <: Element](makeModifier: El => Modifier[El]): Modifier[El] = {
    new Modifier[El] {
      override def apply(element: El): Unit = makeModifier(element).apply(element)
    }
  }

  @inline def forthis[El <: Element](makeModifier: El => Modifier[El]): Modifier[El] = {
    inContext(makeModifier)
  }

}
