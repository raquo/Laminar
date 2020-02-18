package com.raquo.laminar.nodes

import com.raquo.airstream.core.{Observable, Observer}
import com.raquo.airstream.eventbus.{EventBus, WriteBus}
import com.raquo.airstream.eventstream.EventStream
import com.raquo.airstream.ownership.{DynamicSubscription, Owner, Subscription, TransferableSubscription}
import com.raquo.domtypes
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.DomApi
import com.raquo.laminar.emitter.EventPropTransformation
import com.raquo.laminar.lifecycle.{InsertContext, MountContext}
import com.raquo.laminar.modifiers.{Binder, EventPropBinder, Inserter, Setter}
import org.scalajs.dom

import scala.collection.mutable

trait ReactiveElement[+Ref <: dom.Element]
  extends ChildNode[Ref]
  with ParentNode[Ref]
  with domtypes.generic.nodes.Element {

  // @TODO[Naming] We reuse EventPropSetter to represent an active event listener. Makes for a bit confusing naming.
  private[ReactiveElement] var _maybeEventListeners: Option[mutable.Buffer[EventPropBinder[_]]] = None

  @deprecated("ReactiveElement.maybeEventListeners will be removed in a future version of Laminar.", "0.8")
  @inline def maybeEventListeners: Option[List[EventPropBinder[_]]] = _maybeEventListeners.map(_.toList)

  // @Warning[Fragile] deactivate should not need an isActive guard.
  //  If Laminar starts to cause exceptions here, we need to find the root cause.
  /** This subscription activates and deactivates this element's subscriptions when mounting and unmounting this element.
    * In terms of memory management it's ok because a detached node will not have any active subscriptions here.
    */
  private val pilotSubscription: TransferableSubscription = new TransferableSubscription(
    activate = dynamicOwner.activate,
    deactivate = dynamicOwner.deactivate
  )

  /** Create and get a stream of events on this node */
  def events[Ev <: dom.Event](
    eventProp: EventProp[Ev],
    useCapture: Boolean = false,
    stopPropagation: Boolean = false, // @TODO[API] This is inconsistent with EventPropTransformation API. Fix or ok?
    preventDefault: Boolean = false // @TODO[API] This is inconsistent with EventPropTransformation API. Fix or ok?
  ): EventStream[Ev] = {
    val eventBus = new EventBus[Ev]
    val setter = EventPropBinder[Ev, Ev, this.type](
      eventBus.writer,
      eventProp,
      useCapture,
      processor = Some(_)
    )
    setter(this)
    eventBus.events
  }

  @inline def events[Ev <: dom.Event, V](
    t: EventPropTransformation[Ev, V]
  ): EventStream[V] = {
    EventPropTransformation.toEventStream(t, this)
  }

  def amend(mods: Modifier[this.type]*): Unit = {
    mods.foreach(mod => mod(this))
  }

  /** Set a property / attribute / style on mount.
    * Similarly to other onMount methods, you only need this when:
    * a) you need to access MountContext
    * b) you truly need this to only happen on mount
    *
    * Example usage: `onMountSet(ctx => someAttr := someValue(ctx))`. See docs for details.
    */
  def onMountSet(fn: MountContext[this.type] => Setter[this.type]): Unit = {
    onMountCallback(c => fn(c)(c.thisNode))
  }

  /** Bind a subscription on mount
    *
    * Example usage: `element.onMountBind(ctx => someAttr <-- someObservable(ctx))`. See docs for details.
    */
  def onMountBind(fn: MountContext[this.type] => Binder[this.type]): Unit = {
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

  /** Insert child node(s) into this element on mount.
    *
    * Note: insert position is reserved as soon as you call this method.
    * Basically it will insert elements in the same position, where you'd expect, on every mount.
    *
    * Example usage: `element.onMountInsert(ctx => child <-- someObservable(ctx))`. See docs for details.
    */
  def onMountInsert(fn: MountContext[this.type] => Inserter[this.type]): Unit = {
    var maybeSubscription: Option[DynamicSubscription] = None
    val lockedContext = InsertContext.reserveSpotContext[this.type](this)
    onMountUnmountCallback(
      mount = { c =>
        val inserter = fn(c).withContext(lockedContext)
        maybeSubscription = Some(inserter.bind(c.thisNode))
      },
      unmount = { _ =>
        maybeSubscription.foreach(_.kill())
        maybeSubscription = None
      }
    )
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
    * If you call this on an element that is already mounted at call time,
    * the callback will not fire until and unless it is unmounted and then mounted again.
    */
  def onMountCallback(fn: MountContext[this.type] => Unit): DynamicSubscription = {
    var ignoreNextActivation = dynamicOwner.isActive
    ReactiveElement.bindCallback[this.type](this) { c =>
      if (ignoreNextActivation) {
        ignoreNextActivation = false
      } else {
        fn(c)
      }
    }
  }

  /** Execute a callback on unmount. Good for integrating third party libraries.
    *
    * When the callback is called, the element is still mounted.
    *
    * If you call this on an element that is already unmounted at call time, the callback
    * will not fire until and unless it is mounted and then unmounted again.
    */
  def onUnmountCallback(fn: this.type => Unit): DynamicSubscription = {
    ReactiveElement.bindSubscription(this) { c =>
      new Subscription(c.owner, cleanup = () => fn(this))
    }
  }

  /** Combines onMountCallback and onUnmountCallback for easier integration.
    *
    * Note that the same caveats apply as for those individual methods.
    */
  def onMountUnmountCallback(
    mount: MountContext[this.type] => Unit,
    unmount: this.type => Unit
  ): DynamicSubscription = {
    var ignoreNextActivation = dynamicOwner.isActive
    ReactiveElement.bindSubscription[this.type](this) { c =>
      if (ignoreNextActivation) {
        ignoreNextActivation = false
      } else {
        mount(c)
      }
      new Subscription(c.owner, cleanup = () => unmount(this))
    }
  }

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
    //println(" - setPilotSubscriptionOwner of " + this + " (active = " + dynamicOwner.isActive + ") to " + maybeNextParent)

    // @Warning[Fragile] I had issues with clearOwner requiring a hasOwner check but that should not be necessary anymore.
    //  - If exceptions crop up caused by this, need to find the root cause before rushing to patch this here.
    maybeNextParent.fold(pilotSubscription.clearOwner) { nextParent =>
      pilotSubscription.setOwner(nextParent.dynamicOwner)
    }
  }
}

object ReactiveElement {

  type Base = ReactiveElement[dom.Element]

  private class PilotSubscriptionOwner extends Owner

  /** @return Whether listener was added (false if such a listener has already been present) */
  def addEventListener[Ev <: dom.Event](element: ReactiveElement.Base, listener: EventPropBinder[Ev]): Boolean = {
    val shouldAddListener = indexOfEventListener(element, listener) == -1
    if (shouldAddListener) {
      // 1. Update this node
      if (element._maybeEventListeners.isEmpty) {
        element._maybeEventListeners = Some(mutable.Buffer(listener))
      } else {
        element._maybeEventListeners.foreach { eventListeners =>
          eventListeners += listener
        }
      }
      // 2. Update the DOM
      DomApi.addEventListener(element, listener)
    }
    shouldAddListener
  }

  /** @return Whether listener was removed (false if such a listener was not found) */
  def removeEventListener[Ev <: dom.Event](element: ReactiveElement.Base, listener: EventPropBinder[Ev]): Boolean = {
    val index = indexOfEventListener(element, listener)
    val shouldRemoveListener = index != -1
    if (shouldRemoveListener) {
      // 1. Update this node
      element._maybeEventListeners.foreach(eventListeners => eventListeners.remove(index))
      // 2. Update the DOM
      DomApi.removeEventListener(element, listener)
    }
    shouldRemoveListener
  }

  /** @return -1 if not found */
  def indexOfEventListener[Ev <: dom.Event](element: ReactiveElement.Base, listener: EventPropBinder[Ev]): Int = {
    // Note: Ugly for performance.
    //  - We want to reduce usage of Scala's collections and anonymous functions
    //  - js.Array is unaware of Scala's `equals` method
    val notFoundIndex = -1
    if (element._maybeEventListeners.isEmpty) {
      notFoundIndex
    } else {
      var found = false
      var index = 0
      element._maybeEventListeners.foreach { listeners =>
        while (!found && index < listeners.length) {
          if (listener equals listeners(index)) {
            found = true
          } else {
            index += 1
          }
        }
      }
      if (found) index else notFoundIndex
    }
  }

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

  @inline def bindBus[V](
    element: ReactiveElement.Base,
    eventStream: EventStream[V]
  )(
    writeBus: WriteBus[V]
  ): DynamicSubscription = {
    DynamicSubscription.subscribeBus(element.dynamicOwner, eventStream, writeBus)
  }

  @inline def bindSubscription[El <: ReactiveElement.Base](
    element: El
  )(
    subscribe: MountContext[El] => Subscription
  ): DynamicSubscription = {
    DynamicSubscription(element.dynamicOwner, { owner =>
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

  def isActive(element: ReactiveElement.Base): Boolean = {
    element.dynamicOwner.isActive
  }

  /** You should not need this, we're just exposing this for testing. */
  def numDynamicSubscriptions(element: ReactiveElement.Base): Int = {
    element.dynamicOwner.numSubscriptions
  }
}
