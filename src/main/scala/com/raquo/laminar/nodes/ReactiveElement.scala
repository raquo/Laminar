package com.raquo.laminar.nodes

import com.raquo.airstream.core.{Observable, Observer}
import com.raquo.airstream.eventbus.{EventBus, WriteBus}
import com.raquo.airstream.eventstream.EventStream
import com.raquo.airstream.ownership.{DynamicSubscription, Owner, Subscription, TransferableSubscription}
import com.raquo.domtypes
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.DomApi
import com.raquo.laminar.lifecycle.MountContext
import com.raquo.laminar.receivers.{ChildReceiver, ChildrenCommandReceiver, ChildrenReceiver, MaybeChildReceiver, TextChildReceiver}
import com.raquo.laminar.setters.EventPropSetter
import org.scalajs.dom

import scala.collection.mutable

trait ReactiveElement[+Ref <: dom.Element]
  extends ChildNode[Ref]
  with ParentNode[Ref]
  with domtypes.generic.nodes.Element {

  // @TODO[Naming] We reuse EventPropSetter to represent an active event listener. Makes for a bit confusing naming.
  private[ReactiveElement] var _maybeEventListeners: Option[mutable.Buffer[EventPropSetter[_]]] = None

  @inline def maybeEventListeners: Option[List[EventPropSetter[_]]] = _maybeEventListeners.map(_.toList)

  /** This subscription activates and deactivates this element's subscriptions when mounting and unmounting this element. */
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
    val setter = EventPropSetter[Ev, Ev, this.type](
      eventBus.writer,
      eventProp,
      useCapture,
      processor = Some(_)
    )
    setter(this)
    eventBus.events
  }

  /** Note:
    * - These methods provide the first `<--` in the auxiliary syntax `myElement <-- child <-- childSignal`.
    * - The second `<--` in that auxiliary syntax is provided by `class ChildReceiver`.
    * - The more common syntax `myElement(child <-- childSignal)` relies on the `<--` method defined in `object ChildReceiver`
    *
    * The alias `val child: ChildReceiver.type = ChildReceiver` is defined in Laminar.scala
    */
  final def <--[V](childReceiver: ChildReceiver.type): ChildReceiver = new ChildReceiver(this)

  final def <--[V](maybeChildReceiver: MaybeChildReceiver.type): MaybeChildReceiver = new MaybeChildReceiver(this)

  final def <--[V](textChildReceiver: TextChildReceiver.type): TextChildReceiver = new TextChildReceiver(this)

  final def <--[V](childrenReceiver: ChildrenReceiver.type): ChildrenReceiver = new ChildrenReceiver(this)

  final def <--[V](childrenCommandReceiver: ChildrenCommandReceiver.type): ChildrenCommandReceiver = new ChildrenCommandReceiver(this)

  // @TODO[Naming] Not a fan of `subscribeS` / `subscribeO` names, but it needs to be different from `subscribe` for type inference to work
  // @TODO[API] Consider having subscribe() return a Subscribe object that has several apply methods on it to reign in this madness
  //  - Also, do we really need currying here?

  def onMount(fn: MountContext[this.type, Ref] => Unit): DynamicSubscription = {
    onLifecycle(mount = fn, unmount = _ => ())
  }

  def onUnmount(fn: this.type => Unit): DynamicSubscription = {
    onLifecycle(mount = _ => (), unmount = fn)
  }

  // @TODO[API] should `unmount` callback require a MountContext instead of this.type?
  //  That would be consistent with `mount`, but I think exposing an Owner that's about to be killed would be confusing.
  def onLifecycle(
    mount: MountContext[this.type, Ref] => Unit,
    unmount: this.type => Unit
  ): DynamicSubscription = {
    var ignoreNextActivation = dynamicOwner.isActive
    subscribeS(owner => {
      if (ignoreNextActivation) {
        ignoreNextActivation = false
      } else {
        mount(new MountContext[this.type, Ref](thisNode = this, owner))
      }
      new Subscription(owner, cleanup = () => unmount(this))
    })
  }

  // @TODO[API] I'm not sure if this method is a good idea.
  //  Is it obvious enough? Need a better name too.
  //  You can just copy it if you need something like that.
  /** WARNING: init() will be called not only on mount, but also
    *          when you call this method on an already mounted element.
    *          This is needed in order to obtain an A for the subsequent unmount call.
    */
  //def onLifecyclePaired[A](
  //  init: MountContext[this.type, Ref] => A
  //)(
  //  unmount: (A, this.type) => Unit
  //): DynamicSubscription = {
  //  subscribeS(owner => {
  //    val mountState = init(new MountContext[this.type, Ref](thisNode = this, owner))
  //    new Subscription(owner, cleanup = () => unmount(mountState, this))
  //  })
  //}

  def subscribeS(getSubscription: Owner => Subscription): DynamicSubscription = {
    new DynamicSubscription(dynamicOwner, getSubscription)
  }

  def subscribeO[V](observable: Observable[V])(observer: Observer[V]): DynamicSubscription = {
    subscribeS(owner => observable.addObserver(observer)(owner))
  }

  // @TODO[Naming] Rename to subscribeThisO?
  def subscribeO[V](getObservable: this.type => Observable[V])(observer: Observer[V]): DynamicSubscription = {
    subscribeO(getObservable(this))(observer)
  }

  def subscribe[V](observable: Observable[V])(onNext: V => Unit): DynamicSubscription = {
    subscribeO(observable)(Observer(onNext))
  }

  // @TODO[Naming] Rename to subscribeThis?
  def subscribe[V](getObservable: this.type => Observable[V])(onNext: V => Unit): DynamicSubscription = {
    subscribeO(getObservable(this))(Observer(onNext))
  }

  def subscribeBus[V](
    sourceStream: EventStream[V],
    targetBus: WriteBus[V]
  ): DynamicSubscription = {
    subscribeS(owner => targetBus.addSource(sourceStream)(owner))
  }

  override private[nodes] def willSetParent(maybeNextParent: Option[ParentNode.Base]): Unit = {
    // super.willSetParent(maybeNextParent) // default implementation is a noop, so no need to call it.
    // dom.console.log(">>>> willSetParent", this.ref.tagName + "(" + this.ref.textContent + ")", maybeParent == maybeNextParent, maybeParent.toString, maybeNextParent.toString)
    // println(s"> willSetParent of ${this.ref.tagName} to ${maybeNextParent.map(_.ref.tagName)}")

    // @Note this should cover ALL cases not covered by setParent
    if (isUnmounting(maybePrevParent = maybeParent, maybeNextParent = maybeNextParent)) {
      setPilotSubscriptionOwner(maybeNextParent)
    }
  }

  /** Don't call setParent directly – willSetParent will not be called. Use methods like `appendChild` defined on `ParentNode` instead. */
  override private[nodes] def setParent(maybeNextParent: Option[ParentNode.Base]): Unit = {
    // dom.console.log(">>>> setParent", this.ref.tagName + "(" + this.ref.textContent + ")", maybePrevParent == maybeNextParent, maybePrevParent.toString, maybeNextParent.toString)
    // println(s"> setParent of ${this.ref.tagName} to ${maybeNextParent.map(_.ref.tagName)}")

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
    maybeNextParent.fold(
      pilotSubscription.clearOwner()
    )(
      nextParent => pilotSubscription.setOwner(nextParent.dynamicOwner)
    )
  }
}

object ReactiveElement {

  type Base = ReactiveElement[dom.Element]

  private class PilotSubscriptionOwner extends Owner

  /** @return Whether listener was added (false if such a listener has already been present) */
  def addEventListener[Ev <: dom.Event](element: ReactiveElement.Base, listener: EventPropSetter[Ev]): Boolean = {
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
  def removeEventListener[Ev <: dom.Event](element: ReactiveElement.Base, listener: EventPropSetter[Ev]): Boolean = {
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
  def indexOfEventListener[Ev <: dom.Event](element: ReactiveElement.Base, listener: EventPropSetter[Ev]): Int = {
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
}
