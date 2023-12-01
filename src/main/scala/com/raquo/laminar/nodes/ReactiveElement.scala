package com.raquo.laminar.nodes

import com.raquo.airstream.core.{EventStream, Observable, Observer, Sink, Transaction}
import com.raquo.airstream.eventbus.{EventBus, WriteBus}
import com.raquo.airstream.ownership.{DynamicOwner, DynamicSubscription, Subscription, TransferableSubscription}
import com.raquo.ew.JsArray
import com.raquo.laminar.keys.{CompositeKey, EventProcessor, Key}
import com.raquo.laminar.lifecycle.MountContext
import com.raquo.laminar.modifiers.{EventListener, Modifier}
import com.raquo.laminar.tags.Tag
import org.scalajs.dom

import scala.scalajs.js

trait ReactiveElement[+Ref <: dom.Element]
  extends ChildNode[Ref]
  with ParentNode[Ref] {

  // @Warning[Fragile] deactivate should not need an isActive guard.
  //  If Laminar starts to cause exceptions here, we need to find the root cause.
  /** This subscription activates and deactivates this element's subscriptions when mounting and unmounting this element.
    * In terms of memory management it's ok because a detached node will not have any active subscriptions here.
    */
  private val pilotSubscription: TransferableSubscription = new TransferableSubscription(
    activate = dynamicOwner.activate,
    deactivate = dynamicOwner.deactivate
  )

  // #TODO[API] Do we actually need this? What for?
  private[this] var maybeEventListeners: js.UndefOr[JsArray[EventListener.Base]] = js.undefined

  private[laminar] def foreachEventListener(f: EventListener.Base => Unit): Unit = {
    maybeEventListeners.foreach(_.forEach(f))
  }

  /** Note: Only call this after checking that this element doesn't already have this listener */
  private[laminar] def addEventListener(
    listener: EventListener.Base,
    unsafePrepend: Boolean
  ): Unit = {
    if (maybeEventListeners.isEmpty) {
      maybeEventListeners = JsArray[EventListener.Base](listener)
    } else if (unsafePrepend) {
      maybeEventListeners.get.unshift(listener)
    } else {
      maybeEventListeners.get.push(listener)
    }
  }

  /** Note: Only call this after checking that this element currently has this listener */
  private[laminar] def removeEventListener(index: Int): Unit = {
    maybeEventListeners.foreach { eventListeners =>
      eventListeners.splice(index, deleteCount = 1)
    }
  }

  /** @return -1 if not found */
  private[laminar] def indexOfEventListener(listener: EventListener.Base): Int = {
    maybeEventListeners.fold(ifEmpty = -1) { eventListeners =>
      var found: Boolean = false
      var ix = 0
      while (!found && ix < eventListeners.length) {
        if (eventListeners(ix) == listener) {
          found = true
        } else {
          ix += 1
        }
      }
      if (found) ix else -1
    }
  }

  /** This structure keeps track of reasons for including a given item for a given composite key.
    * For example, this remembers which modifiers have previously added which className.
    *
    * We need this to avoid interference in cases when two modifiers want to add the same
    * class name (e.g.) and one of those subsequently does not want to add that class name
    * anymore. Without this structure, the element would end up without that class name even
    * though one modifier still wants it added.
    *
    * Structure:
    *
    * Map(
    *   cls: List(
    *     "always" -> null,
    *     "present" -> null,
    *     "classes" -> null,
    *     "class1" -> modThatWantsClass1Set,
    *     "class1" -> anotherModThatWantsClass1Set,
    *     "class2" -> modThatWantsClass2Set
    *   ),
    *   rel: List( ... ),
    *   role: List( ... )
    * )
    *
    * Note that `mod` key can be null if the mod is not reactive, e.g. in the simple case of `cls` := "always"
    * This is to avoid keeping the mod in memory after it has served its purpose.
    *
    * Note that this structure can have redundant items (e.g. class names) in it, they are filtered out when writing to the DOM
    */
  private[this] var _compositeValues: Map[CompositeKey[_, this.type], List[(String, Modifier.Any)]] =
    Map.empty

  private[laminar] def compositeValueItems(
    prop: CompositeKey[_, this.type],
    reason: Modifier.Any
  ): List[String] = {
    _compositeValues
      .getOrElse(prop, Nil)
      .collect { case (item, r) if r == reason => item }
  }

  private[laminar] def updateCompositeValue(
    key: CompositeKey[_, this.type],
    reason: Modifier.Any,
    addItems: List[String],
    removeItems: List[String]
  ): Unit = {
    val keyItemsWithReason = _compositeValues.getOrElse(key, Nil)
    val itemHasAnotherReason = (item: String) => {
      // #Note null reason is shared among all static modifiers to avoid keeping a reference to them
      keyItemsWithReason.exists(t => t._1 == item && (t._2 != reason || reason == null))
    }

    val itemsToAdd = addItems.distinct
    val itemsToRemove = removeItems.filterNot(itemHasAnotherReason)
    val newItems = _compositeValues
      .getOrElse(key, Nil)
      .filterNot(t => itemsToRemove.contains(t._1)) ++ itemsToAdd.map((_, reason))

    val domValues = key.codec.decode(key.getRawDomValue(this))

    val nextDomValues = domValues.filterNot(itemsToRemove.contains) ++ itemsToAdd.filterNot(itemHasAnotherReason)

    // 1. Update Laminar's internal structure
    _compositeValues = _compositeValues.updated(key, newItems)

    // 2. Write desired state to the DOM
    // #Note this logic is compatible with third parties setting classes on Laminar elements
    //  using raw JS methods as long as they don't remove classes managed by Laminar or add
    //  classes that were also added by Laminar.
    key.setRawDomValue(this, key.codec.encode(nextDomValues))
  }

  val tag: Tag[ReactiveElement[Ref]]

  def eventListeners: List[EventListener.Base] = {
    maybeEventListeners.map(_.asScalaJs.toList).getOrElse(Nil)
  }

  /** Create and get a stream of events on this node */
  def events[Ev <: dom.Event, Out](
    prop: EventProcessor[Ev, Out]
  ): EventStream[Out] = {
    val eventBus = new EventBus[Out]
    val listener = new EventListener[Ev, Out](
      prop,
      eventBus.writer.onNext
    )
    listener(this)
    eventBus.events
  }

  // @TODO[Performance] Review scala.js generated code for single-element use case.
  //  - I would like to also have a `def amend(mod: Modifier[this.type])` method
  //  - but then SyntaxSpec / "amend on inlined element" test will fail. Check in Dotty later.
  //  - not a big deal but lame
  def amend(mods: Modifier[this.type]*): this.type = {
    Transaction.onStart.shared {
      mods.foreach(mod => mod(this))
      this
    }
  }

  def amendThis(makeMod: this.type => Modifier[this.type]): this.type = {
    val mod = makeMod(this)
    mod(this)
    this
  }

  private[laminar] def onBoundKeyUpdater(key: Key): Unit

  override private[nodes] def willSetParent(maybeNextParent: Option[ParentNode.Base]): Unit = {
    //println(s"> willSetParent of ${this.ref.tagName} to ${maybeNextParent.map(_.ref.tagName)}")

    // @Note this should cover ALL cases not covered by setParent
    if (isUnmounting(maybePrevParent = maybeParent, maybeNextParent = maybeNextParent)) {
      setPilotSubscriptionOwner(maybeNextParent)
    }
  }

  /** Don't call setParent directly – willSetParent will not be called. Use methods like `appendChild` defined on `ParentNode` instead. */
  override private[nodes] def setParent(maybeNextParent: Option[ParentNode.Base]): Unit = {
    //println(s"> setParent of ${this.ref.tagName} to ${maybeNextParent.map(_.ref.tagName)}")

    val maybePrevParent = maybeParent
    super.setParent(maybeNextParent)

    // @Note this should cover ALL cases not covered by willSetParent
    if (!isUnmounting(maybePrevParent = maybePrevParent, maybeNextParent = maybeNextParent)) {
      setPilotSubscriptionOwner(maybeNextParent)
    }
  }

  private[this] def isUnmounting(
    maybePrevParent: Option[ParentNode.Base],
    maybeNextParent: Option[ParentNode.Base]
  ): Boolean = {
    val isPrevParentActive = maybePrevParent.exists(_.dynamicOwner.isActive)
    val isNextParentActive = maybeNextParent.exists(_.dynamicOwner.isActive)
    isPrevParentActive && !isNextParentActive
  }

  private[this] def setPilotSubscriptionOwner(maybeNextParent: Option[ParentNode.Base]): Unit = {
    unsafeSetPilotSubscriptionOwner(maybeNextParent.map(_.dynamicOwner))
  }

  protected[this] def unsafeSetPilotSubscriptionOwner(maybeNextOwner: Option[DynamicOwner]): Unit = {
    // @Warning[Fragile] I had issues with clearOwner requiring a hasOwner check but that should not be necessary anymore.
    //  - If exceptions crop up caused by this, need to find the root cause before rushing to patch this here.
    maybeNextOwner.fold(pilotSubscription.clearOwner()) { nextOwner =>
      pilotSubscription.setOwner(nextOwner)
    }
  }
}

object ReactiveElement {

  type Base = ReactiveElement[dom.Element]

  @inline def bindFn[V](
    element: ReactiveElement.Base,
    observable: Observable[V]
  )(
    onNext: V => Unit
  ): DynamicSubscription = {
    DynamicSubscription.subscribeFn(element.dynamicOwner, observable, onNext)
  }

  @inline def bindObserver[V](
    element: ReactiveElement.Base,
    observable: Observable[V]
  )(
    observer: Observer[V]
  ): DynamicSubscription = {
    DynamicSubscription.subscribeObserver(element.dynamicOwner, observable, observer)
  }

  @inline def bindSink[V](
    element: ReactiveElement.Base,
    observable: Observable[V]
  )(
    sink: Sink[V]
  ): DynamicSubscription = {
    DynamicSubscription.subscribeSink(element.dynamicOwner, observable, sink)
  }

  @inline def bindBus[V](
    element: ReactiveElement.Base,
    eventStream: EventStream[V]
  )(
    writeBus: WriteBus[V]
  ): DynamicSubscription = {
    DynamicSubscription.subscribeBus(element.dynamicOwner, eventStream, writeBus)
  }

  /** #Note: Unsafe because you must make sure that the Subscription created by
    *  the `subscribe` callback is not killed externally. Otherwise, when the
    *  DynamicSubscription decides to kill it, the already-killed Subscription
    *  will throw an exception.
    */
  @inline def bindSubscriptionUnsafe[El <: ReactiveElement.Base](
    element: El
  )(
    subscribe: MountContext[El] => Subscription
  ): DynamicSubscription = {
    DynamicSubscription.unsafe(element.dynamicOwner, { owner =>
      subscribe(new MountContext[El](element, owner))
    })
  }

  @inline def bindCallback[El <: ReactiveElement.Base](
    element: El
  )(
    activate: MountContext[El] => Unit
  ): DynamicSubscription = {
    DynamicSubscription.subscribeCallback(element.dynamicOwner, owner => {
      activate(new MountContext[El](element, owner))
    })
  }

  /** Bind subscription such that it will appear first in the list of dynamicOwner's subscriptions.
    * Be careful, use wisely. If you're using this for events, make sure that the listeners in the
    * DOM (and also the `maybeEventListeners` array) are in the same order.
    */
  private[laminar] def unsafeBindPrependSubscription[El <: ReactiveElement.Base](
    element: El
  )(
    subscribe: MountContext[El] => Subscription
  ): DynamicSubscription = {
    DynamicSubscription.unsafe(element.dynamicOwner, { owner =>
      subscribe(new MountContext[El](element, owner))
    }, prepend = true)
  }

  // @TODO Maybe later. Wanted to make a seqToBinder.
  //@inline def bindCombinedSubscription[El <: ReactiveElement.Base](
  //  element: El
  //)(
  //  subscriptions: DynamicOwner => collection.Seq[DynamicSubscription]
  //): DynamicSubscription = {
  //  DynamicSubscription.combine(element.dynamicOwner, subscriptions)
  //}

  def isActive(element: ReactiveElement.Base): Boolean = {
    element.dynamicOwner.isActive
  }

  /** You should not need this, we're just exposing this for testing. */
  def numDynamicSubscriptions(element: ReactiveElement.Base): Int = {
    element.dynamicOwner.numSubscriptions
  }

  /** Transform tag name obtained from the DOM to match the tag name
    * that we would have defined for it in HtmlTags / SvgTags.
    *
    * Basically this just means lower-casing HTML tag names, e.g.
    * transforming "DIV" returned from the DOM to "div", taking care
    * to avoid mangling custom element tag names.
    */
  def normalizeTagName(element: dom.Element): String = {
    val rawTagName = element.tagName
    val isCustomElement = rawTagName.contains("-")
    if (isCustomElement) {
      rawTagName // Don't touch tag names of custom elements
    } else {
      rawTagName.toLowerCase
    }
  }
}
