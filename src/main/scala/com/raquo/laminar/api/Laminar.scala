package com.raquo.laminar.api

import com.raquo.airstream.web.DomEventStream
import com.raquo.laminar
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.defs.attrs.{AriaAttrs, HtmlAttrs, SvgAttrs}
import com.raquo.laminar.defs.complex.{ComplexHtmlKeys, ComplexSvgKeys}
import com.raquo.laminar.defs.eventProps.{DocumentEventProps, GlobalEventProps, WindowEventProps}
import com.raquo.laminar.defs.props.HtmlProps
import com.raquo.laminar.defs.styles.{StyleProps, units}
import com.raquo.laminar.defs.tags.{HtmlTags, SvgTags}
import com.raquo.laminar.keys._
import com.raquo.laminar.lifecycle.InsertContext
import com.raquo.laminar.modifiers.{EventListener, KeyUpdater}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement, ReactiveSvgElement}
import com.raquo.laminar.receivers._
import com.raquo.laminar.tags.{HtmlTag, SvgTag}
import com.raquo.laminar.{DomApi, Implicits, keys, lifecycle, modifiers, nodes}
import org.scalajs.dom

import scala.collection.immutable

// @TODO[Performance] Check if order of traits matters for quicker access (given trait linearization). Not sure how it's encoded in JS.

trait Laminar
  extends Airstream
    with HtmlTags
    with HtmlAttrs
    with HtmlProps
    with GlobalEventProps
    with StyleProps
    with ComplexHtmlKeys
    with Implicits {

  /** This marker trait is used for implicit conversions. For all intents and purposes it's just a function. */
  trait StyleEncoder[A] extends Function1[A, String]

  object style
    extends DerivedStyleBuilder[String, StyleEncoder]
      with units.Color[String, StyleEncoder]
      with units.Length[StyleEncoder, Int]
      with units.Time[StyleEncoder]
      with units.Url[StyleEncoder] {

    override protected def styleSetter(value: String): String = value

    override protected def derivedStyle[A](encode: A => String): StyleEncoder[A] = new StyleEncoder[A] {
      override def apply(v: A): String = encode(v)
    }
  }

  object aria
    extends AriaAttrs

  // @TODO[API] Add GlobalEventProps to SVG as well? Regular event props work fine, but they might be hard to import if you also import svg._
  object svg
    extends SvgTags
      with SvgAttrs
      with ComplexSvgKeys {

    @deprecated("customEventProp was removed from the svg scope, use eventProp in the parent scope", "0.15.0-RC1")
    @inline def customEventProp[Ev <: dom.Event](key: String): EventProp[Ev] = eventProp(key)

    @deprecated("customSvgAttr was renamed to svgAttr", "0.15.0-RC1")
    @inline def customSvgAttr[V](key: String, codec: Codec[V, String], namespace: Option[String] = None): SvgAttr[V] = svgAttr(key, codec, namespace)

    @deprecated("customSvgTag was renamed to svgTag", "0.15.0-RC1")
    @inline def customSvgTag[Ref <: dom.svg.Element](tagName: String): SvgTag[Ref] = svgTag(tagName)
  }

  // -- Document & window events --

  protected val documentEventProps = new GlobalEventProps with DocumentEventProps

  protected val windowEventProps = new GlobalEventProps with WindowEventProps

  /** Typical usage: documentEvents(_.onDomContentLoaded) */
  def documentEvents[Ev <: dom.Event, V](events: documentEventProps.type => EventProcessor[Ev, V]): EventStream[V] = {
    val p = events(documentEventProps)
    DomEventStream[Ev](dom.document, EventProcessor.eventProp(p).name,  EventProcessor.shouldUseCapture(p))
      .collectOpt(EventProcessor.processor(p))
  }

  /** Typical usage: windowEvents(_.onOffline) */
  def windowEvents[Ev <: dom.Event, V](events: windowEventProps.type => EventProcessor[Ev, V]): EventStream[V] = {
    val p = events(windowEventProps)
    DomEventStream[Ev](dom.window, EventProcessor.eventProp(p).name, EventProcessor.shouldUseCapture(p))
      .collectOpt(EventProcessor.processor(p))
  }

  @deprecated("customHtmlAttr was renamed to htmlAttr", "0.15.0-RC1")
  @inline def customHtmlAttr[V](key: String, codec: Codec[V, String]): keys.HtmlAttr[V] = htmlAttr(key, codec)

  @deprecated("customProp was renamed to htmlProp", "0.15.0-RC1")
  @inline def customProp[V, DomV](key: String, codec: Codec[V, DomV]): keys.HtmlProp[V, DomV] = htmlProp(key, codec)

  @deprecated("customEventProp was renamed to eventProp", "0.15.0-RC1")
  @inline def customEventProp[Ev <: dom.Event](key: String): keys.EventProp[Ev] = eventProp(key)

  @deprecated("customStyle was renamed to styleProp", "0.15.0-RC1")
  @inline def customStyle[V](key: String): keys.StyleProp[V] = styleProp(key)

  @deprecated("customHtmlTag was renamed to htmlTag", "0.15.0-RC1")
  @inline def customHtmlTag[Ref <: dom.html.Element](tagName: String): HtmlTag[Ref] = htmlTag(tagName)

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

  @deprecated("`Child` type alias is deprecated. Use ChildNode.Base", "15.0.0-M6")
  type Child = nodes.ChildNode.Base

  @deprecated("`Children`type alias is deprecated. Use immutable.Seq[ChildNode.Base]", "15.0.0-M6")
  type Children = immutable.Seq[nodes.ChildNode.Base]

  @deprecated("`ChildrenCommand` type alias is deprecated. Use CollectionCommand.Base", "15.0.0-M5")
  type ChildrenCommand = CollectionCommand[nodes.ChildNode.Base]

  type CollectionCommand[+Item] = laminar.CollectionCommand[Item]

  lazy val CollectionCommand: laminar.CollectionCommand.type = laminar.CollectionCommand

  // Modifiers

  type Mod[-El <: ReactiveElement.Base] = modifiers.Modifier[El]

  @inline def Mod: modifiers.Modifier.type = modifiers.Modifier

  type HtmlMod = Mod[HtmlElement]

  type SvgMod = Mod[SvgElement]


  type Modifier[-El <: ReactiveElement.Base] = modifiers.Modifier[El]

  val Modifier: modifiers.Modifier.type = modifiers.Modifier


  type Setter[-El <: Element] = modifiers.Setter[El]

  val Setter: modifiers.Setter.type = modifiers.Setter


  type Binder[-El <: Element] = modifiers.Binder[El]

  val Binder: modifiers.Binder.type = modifiers.Binder


  type Inserter[-El <: Element] = modifiers.Inserter[El]

  val Inserter: modifiers.Inserter.type = modifiers.Inserter


  // Events

  type EventProcessor[Ev <: dom.Event, V] = keys.EventProcessor[Ev, V]


  // Lifecycle

  type MountContext[+El <: Element] = lifecycle.MountContext[El]

  type InsertContext[+El <: Element] = lifecycle.InsertContext[El]


  // Keys

  type EventProp[Ev <: dom.Event] = keys.EventProp[Ev]

  type HtmlAttr[V] = keys.HtmlAttr[V]

  @deprecated("Use `HtmlProp` instead of `Prop`", "0.15.0-RC1")
  type Prop[V] = keys.HtmlProp[V, _]

  type HtmlProp[V] = keys.HtmlProp[V, _]

  @deprecated("Use `StyleProp` instead of `Style`", "0.15.0-RC1")
  type Style[V] = keys.StyleProp[V]

  type StyleProp[V] = keys.StyleProp[V]

  type SvgAttr[V] = keys.SvgAttr[V]

  type CompositeHtmlAttr = ComplexHtmlKeys.CompositeHtmlAttr

  type CompositeSvgAttr = ComplexSvgKeys.CompositeSvgAttr


  // Specific HTML elements

  type Anchor = nodes.ReactiveHtmlElement[dom.html.Anchor]

  type Button = nodes.ReactiveHtmlElement[dom.html.Button]

  type Div = nodes.ReactiveHtmlElement[dom.html.Div]

  type IFrame = nodes.ReactiveHtmlElement[dom.html.IFrame]

  type Image = nodes.ReactiveHtmlElement[dom.html.Image]

  type Input = nodes.ReactiveHtmlElement[dom.html.Input]

  type FormElement = nodes.ReactiveHtmlElement[dom.html.Form]

  type Label = nodes.ReactiveHtmlElement[dom.html.Label]

  @deprecated("Use LI instead of Li type", "0.15.0-RC1")
  type Li = nodes.ReactiveHtmlElement[dom.html.LI]

  type LI = nodes.ReactiveHtmlElement[dom.html.LI]

  type Select = nodes.ReactiveHtmlElement[dom.html.Select]

  type Span = nodes.ReactiveHtmlElement[dom.html.Span]

  type TextArea = nodes.ReactiveHtmlElement[dom.html.TextArea]


  @inline def render(
    container: dom.Element,
    rootNode: nodes.ReactiveElement.Base
  ): RootNode = {
    new RootNode(container, rootNode)
  }

  @inline def renderOnDomContentLoaded(
    container: => dom.Element,
    rootNode: => nodes.ReactiveElement.Base
  ): Unit = {
    documentEvents(_.onDomContentLoaded).foreach { _ =>
      new RootNode(container, rootNode)
    }(unsafeWindowOwner)
  }

  /** A universal Modifier that does nothing */
  val emptyMod: Modifier[ReactiveElement.Base] = Modifier.empty

  /** Note: this is not a [[nodes.ReactiveElement]] because [[dom.Comment]] is not a [[dom.Element]].
    * This is a bit annoying, I know, but we kinda have to follow the native JS DOM API on this.
    * Both [[CommentNode]] and [[nodes.ReactiveElement]] are [[Node]].
    */
  @inline def emptyNode: CommentNode = commentNode("")

  def commentNode(text: String = ""): CommentNode = new CommentNode(text)

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

  /** Non-breaking space character
    *
    * Note: In Laminar you don't need to use html entities, you can just
    * insert the actual character you want, e.g. "»" instead of "&raquo;"
    */
  val nbsp: String = "\u00a0"


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
    Modifier[El] { element =>
      var maybeSubscription: Option[DynamicSubscription] = None
      // We start the context in loose mode for performance, because it's cheaper to go from there
      // to strict mode, than the other way. The inserters are able to handle any initial mode.
      val lockedInsertContext = InsertContext.reserveSpotContext[El](element, strictMode = false)
      element.amend(
        onMountUnmountCallback[El](
          mount = { mountContext =>
            val inserter = fn(mountContext).withContext(lockedInsertContext)
            maybeSubscription = Some(inserter.bind(mountContext.thisNode))
          },
          unmount = { _ =>
            maybeSubscription.foreach(_.kill())
            maybeSubscription = None
          }
        )
      )
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
    Modifier[El] { element =>
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

  /** Execute a callback on unmount. Good for integrating third party libraries.
    *
    * When the callback is called, the element is still mounted.
    *
    * If you apply this modifier to an element that is already unmounted, the callback
    * will not fire until and unless it is mounted and then unmounted again.
    */
  def onUnmountCallback[El <: Element](fn: El => Unit): Modifier[El] = {
    Modifier[El] { element =>
      ReactiveElement.bindSubscriptionUnsafe(element) { c =>
        new Subscription(c.owner, cleanup = () => fn(element))
      }
    }
  }

  /** Combines onMountCallback and onUnmountCallback for easier integration.
    * - Note that the same caveats apply as for those individual methods.
    * - See also: [[onMountUnmountCallbackWithState]]
    */
  def onMountUnmountCallback[El <: Element](
    mount: MountContext[El] => Unit,
    unmount: El => Unit
  ): Modifier[El] = {
    onMountUnmountCallbackWithState[El, Unit](mount, (el, _) => unmount(el))
  }

  /** Combines onMountCallback and onUnmountCallback for easier integration.
    * - Note that the same caveats apply as for those individual methods.
    * - The mount callback returns state which will be provided to the unmount callback.
    * - The unmount callback receives an Option of the state because it's possible that
    * onMountUnmountCallbackWithState was called *after* the element was already mounted,
    * in which case the mount callback defined here wouldn't have run.
    */
  def onMountUnmountCallbackWithState[El <: Element, A](
    mount: MountContext[El] => A,
    unmount: (El, Option[A]) => Unit
  ): Modifier[El] = {
    Modifier[El] { element =>
      var ignoreNextActivation = ReactiveElement.isActive(element)
      var state: Option[A] = None
      ReactiveElement.bindSubscriptionUnsafe[El](element) { c =>
        if (ignoreNextActivation) {
          ignoreNextActivation = false
        } else {
          state = Some(mount(c))
        }
        new Subscription(
          c.owner,
          cleanup = () => {
            unmount(element, state)
            state = None
          }
        )
      }
    }
  }

  // @TODO[Naming] Find a better name for this. We now have actual MountContext classes that this has nothing to do with.
  /** Use this when you need a reference to current element. See docs. */
  def inContext[El <: Element](makeModifier: El => Modifier[El]): Modifier[El] = {
    Modifier[El] { element => makeModifier(element).apply(element) }
  }

  /** Use this when you need to apply stream operators on this element's events, e.g.:
    *
    *     div(composeEvents(onScroll)(_.throttle(100)) --> observer)
    *
    *     a(composeEvents(onClick.preventDefault)(_.delay(100)) --> observer)
    */
  @deprecated("Instead of composeEvents(a)(b), use a.compose(b)", "0.15.0-RC1")
  def composeEvents[Ev <: dom.Event, In, Out](
    event: EventProcessor[Ev, In]
  )(
    composer: EventStream[In] => Observable[Out]
  ): LockedEventKey[Ev, In, Out] = {
    new LockedEventKey(event, composer)
  }

  def controlled[El <: HtmlElement, Ev <: dom.Event, V](
    updater: KeyUpdater[El, HtmlProp[V], V],
    listener: EventListener[Ev, _]
  ): Binder[El] = {
    Binder[El] { element =>
      // @TODO[Elegance] Clean up the whole ValueController structure later
      // @TODO[Integrity] Not sure if there's a good way to avoid asInstanceOf here
      if (updater.key == value) {
        element.setValueController(updater.asInstanceOf[KeyUpdater[HtmlElement, HtmlProp[String], String]], listener)
      } else if (updater.key == checked) {
        element.setCheckedController(updater.asInstanceOf[KeyUpdater[HtmlElement, HtmlProp[Boolean], Boolean]], listener)
      } else {
        throw new Exception(s"Can not add a controller for property `${updater.key}` – only `value` and `checked` can be controlled this way. See docs on controlled inputs for details.")
      }
    }
  }

  /** Just like the other `controlled` method, but with the two arguments swapped places. Works the same. */
  @inline def controlled[El <: HtmlElement, Ev <: dom.Event, V](
    listener: EventListener[Ev, _],
    updater: KeyUpdater[El, HtmlProp[V], V]
  ): Binder[El] = {
    controlled(updater, listener)
  }
}
