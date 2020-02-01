package com.raquo.laminar.nodes

import com.raquo.airstream.core.{Observable, Observer}
import com.raquo.airstream.eventbus.{EventBus, WriteBus}
import com.raquo.airstream.eventstream.EventStream
import com.raquo.airstream.ownership.{DynamicOwner, DynamicSubscription, Owner, Subscription}
import com.raquo.airstream.signal.Signal
import com.raquo.domtypes
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.DomApi
import com.raquo.laminar.lifecycle.{MountEvent, NodeDidMount, NodeWillUnmount, ParentChangeEvent}
import com.raquo.laminar.nodes.ChildNode.isParentMounted
import com.raquo.laminar.nodes.ReactiveElement.noMountEvents
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

  private[this] val dynamicOwner: DynamicOwner = new DynamicOwner

  /** Event bus that emits parent change events.
    * For efficiency, it is only populated when someone accesses [[parentChangeEvents]]
    */
  private[this] var maybeParentChangeBus: Option[EventBus[ParentChangeEvent]] = None

  /** Event bus that emits this node's mount events.
    * For efficiency, it is only populated when someone accesses [[thisNodeMountEvents]]
    */
  private[this] var maybeThisNodeMountEventBus: Option[EventBus[MountEvent]] = None

  /** Stream of parent change events for this node.
    * For efficiency, it is lazy loaded, only being initialized when accessed
    */
  private[this] lazy val parentChangeEvents: EventStream[ParentChangeEvent] = {
    val parentChangeBus = new EventBus[ParentChangeEvent]
    maybeParentChangeBus = Some(parentChangeBus)
    parentChangeBus.events
  }

  private[this] lazy val maybeParentSignal: Signal[Option[ParentNode.Base]] = parentChangeEvents
    .filter(_.alreadyChanged) // @TODO[Integrity] is this important?
    .map(_.maybeNextParent)
    .toSignal(maybeParent)

  /** Emits mount events that were caused by this element's parent changing its parent,
    * or any such changes up the chain. Does not include mount events triggered by this
    * element changing its parent - see [[thisNodeMountEvents]] for that. */
  private[this] lazy val ancestorMountEvents: EventStream[MountEvent] = {
    maybeParentSignal.map {
      case Some(nextParent: ReactiveElement[_]) =>
        nextParent.mountEvents
      case _ =>
        noMountEvents
    }.flatten
  }

  /** Emits mount events caused by this node changing its parent. Does not include mount
    * events triggered by changes higher in the hierarchy (grandparent and up) – see
    * [[ancestorMountEvents]] for that. */
  private[this] lazy val thisNodeMountEvents: EventStream[MountEvent] = {
    val thisNodeMountEventBus = new EventBus[MountEvent]
    maybeThisNodeMountEventBus = Some(thisNodeMountEventBus)
    thisNodeMountEventBus.events
  }

  /** Emits mount events for this node, including mount events fired by all of its ancestors */
  lazy val mountEvents: EventStream[MountEvent] = {
    EventStream.merge(ancestorMountEvents, thisNodeMountEvents)
  }

  // @nc normally this would not work memory management wise, as we create a sub on element init, and never even bother to kill it
  //  - however, this sub only looks at mount events
  //  - thisNodeMountEvents are contained within this node and don't require any leaky resources
  //  - ancestorMountEvents look at parent nodes' mountEvents
  //    - while this node is mounted, all ancestors are also mounted and that is ok
  //    - if this node becomes unmounted, the ancestor chain includes only other unmounted nodes
  //    - so this will lock together that ancestor chain, but that's like meh
  //    - even if we re-mounted this node to different parents / ancestors, this chain would be broken as `mountEvents` would now point elsewhere
  //    - as long as we understand and document this limitation, I think it's ok
  // @nc Can we initialize this conditionally, only for elements that need it?
  {
    val pilotSubscriptionOwner: Owner = new Owner {}

    mountEvents.foreach{
      case NodeDidMount => dynamicOwner.activate()
      case NodeWillUnmount => dynamicOwner.deactivate()
    }(pilotSubscriptionOwner)
  }

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

  /** This hook is exposed by Scala DOM Builder for this exact purpose */
  override private[nodes] def willSetParent(maybeNextParent: Option[ParentNode.Base]): Unit = {
    // super.willSetParent(maybeNextParent) // default implementation is a noop, so no need to call it.
    // dom.console.log(">>>> willSetParent", this.ref.tagName + "(" + this.ref.textContent + ")", maybeParent == maybeNextParent, maybeParent.toString, maybeNextParent.toString)
    if (maybeNextParent != maybeParent) {
      maybeParentChangeBus.foreach(bus => {
        bus.writer.onNext(ParentChangeEvent(
          alreadyChanged = false,
          maybePrevParent = maybeParent,
          maybeNextParent = maybeNextParent
        ))
      })
      if (!isParentMounted(maybeNextParent) && isParentMounted(maybeParent)) {
        maybeThisNodeMountEventBus.foreach { bus =>
          bus.writer.onNext(NodeWillUnmount)
        }
      }
    }
  }

  override private[nodes] def setParent(maybeNextParent: Option[ParentNode.Base]): Unit = {
    // @TODO[Integrity] Beware of calling setParent directly – willSetParent will not be called?
    // @TODO[Integrity] this method (and others) should be protected
    val maybePrevParent = maybeParent
    super.setParent(maybeNextParent)
    // dom.console.log(">>>> setParent", this.ref.tagName + "(" + this.ref.textContent + ")", maybePrevParent == maybeNextParent, maybePrevParent.toString, maybeNextParent.toString)
    if (maybeNextParent != maybePrevParent) {
      maybeParentChangeBus.foreach(bus => {
        val ev = ParentChangeEvent(
          alreadyChanged = true,
          maybePrevParent = maybePrevParent,
          maybeNextParent = maybeNextParent
        )
        bus.writer.onNext(ev)
      })
      val prevParentIsMounted = isParentMounted(maybePrevParent)
      val nextParentIsMounted = isParentMounted(maybeNextParent)
      if (!prevParentIsMounted && nextParentIsMounted) {
        maybeThisNodeMountEventBus.foreach(_.writer.onNext(NodeDidMount))
      }
    }
  }
}

object ReactiveElement {

  type Base = ReactiveElement[dom.Element]

  private val noMountEvents: EventStream[MountEvent] = EventStream.fromSeq(Nil, emitOnce = true)

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
