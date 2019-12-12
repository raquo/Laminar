package com.raquo.laminar.nodes

import com.raquo.airstream.core.{Observable, Observer, Subscription}
import com.raquo.airstream.eventbus.{EventBus, EventBusSource, WriteBus}
import com.raquo.airstream.eventstream.EventStream
import com.raquo.airstream.ownership.{Owned, Owner}
import com.raquo.airstream.signal.Signal
import com.raquo.domtypes
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.DomApi
import com.raquo.laminar.emitter.EventPropEmitter
import com.raquo.laminar.lifecycle.{MountEvent, NodeDidMount, NodeWasDiscarded, NodeWillUnmount, ParentChangeEvent}
import com.raquo.laminar.nodes.ChildNode.isParentMounted
import com.raquo.laminar.nodes.ReactiveElement.noMountEvents
import com.raquo.laminar.receivers.{ChildReceiver, ChildrenCommandReceiver, ChildrenReceiver, MaybeChildReceiver, TextChildReceiver}
import com.raquo.laminar.setters.EventPropSetter
import org.scalajs.dom

import scala.collection.mutable

trait ReactiveElement[+Ref <: dom.Element]
  extends ChildNode[Ref]
  with ParentNode[Ref]
  with domtypes.generic.nodes.Element
  with Owner {

  // @TODO[Naming] We reuse EventPropSetter to represent an active event listener. Makes for a bit confusing naming.
  private[ReactiveElement] var _maybeEventListeners: Option[mutable.Buffer[EventPropSetter[_]]] = None

  @inline def maybeEventListeners: Option[List[EventPropSetter[_]]] = _maybeEventListeners.map(_.toList)

  /** Event bus that emits parent change events.
    * For efficiency, it is only populated when someone accesses [[parentChangeEvents]]
    */
  private[laminar] var maybeParentChangeBus: Option[EventBus[ParentChangeEvent]] = None

  /** Event bus that emits this node's mount events.
    * For efficiency, it is only populated when someone accesses [[thisNodeMountEvents]]
    */
  private[laminar] var maybeThisNodeMountEventBus: Option[EventBus[MountEvent]] = None

  /** Stream of parent change events for this node.
    * For efficiency, it is lazy loaded, only being initialized when accessed,
    * either directly or (more commonly) as a dependency of [[mountEvents]]
    */
  lazy val parentChangeEvents: EventStream[ParentChangeEvent] = {
    val parentChangeBus = new EventBus[ParentChangeEvent]
    maybeParentChangeBus = Some(parentChangeBus)
    parentChangeBus.events
  }

  lazy val maybeParentSignal: Signal[Option[ParentNode.Base]] = parentChangeEvents
    .filter(_.alreadyChanged) // @TODO[Integrity] is this important?
    .map(_.maybeNextParent)
    .toSignal(maybeParent)

  /** Emits mount events that were caused by this element's parent changing its parent,
    * or any such changes up the chain. Does not include mount events triggered by this
    * element changing its parent - see [[thisNodeMountEvents]] for that. */
  private lazy val ancestorMountEvents: EventStream[MountEvent] = {
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
  private lazy val thisNodeMountEvents: EventStream[MountEvent] = {
    val thisNodeMountEventBus = new EventBus[MountEvent]
    maybeThisNodeMountEventBus = Some(thisNodeMountEventBus)
    thisNodeMountEventBus.events
  }

  /** Emits mount events for this node, including mount events fired by all of its ancestors */
  lazy val mountEvents: EventStream[MountEvent] = {
    EventStream.merge(ancestorMountEvents, thisNodeMountEvents)
  }

  /** Create and get a stream of events on this node */
  def events[Ev <: dom.Event](
    eventProp: EventProp[Ev],
    useCapture: Boolean = false,
    stopPropagation: Boolean = false, // @TODO[API] This is inconsistent with EventPropEmitter API. Fix or ok?
    preventDefault: Boolean = false // @TODO[API] This is inconsistent with EventPropEmitter API. Fix or ok?
  ): EventStream[Ev] = {
    val eventBus = new EventBus[Ev]
    val setter = new EventPropEmitter[Ev, Ev, this.type](
      eventBus.writer,
      eventProp,
      useCapture,
      processor = Some(_)
    )
    setter(this)
    eventBus.events
  }

  final def <--[V](childReceiver: ChildReceiver.type): ChildReceiver = new ChildReceiver(this)

  final def <--[V](maybeChildReceiver: MaybeChildReceiver.type): MaybeChildReceiver = new MaybeChildReceiver(this)

  final def <--[V](textChildReceiver: TextChildReceiver.type): TextChildReceiver = new TextChildReceiver(this)

  final def <--[V](childrenReceiver: ChildrenReceiver.type): ChildrenReceiver = new ChildrenReceiver(this)

  final def <--[V](childrenCommandReceiver: ChildrenCommandReceiver.type): ChildrenCommandReceiver = new ChildrenCommandReceiver(this)

  // @TODO[Naming] Not a fan of `subscribeO` name, but it needs to be different from `subscribe` for type inference to work

  def subscribeO[V](observable: Observable[V])(observer: Observer[V]): Subscription = {
    observable.addObserver(observer)(owner = this)
  }

  // @TODO[Naming] Rename to subscribeThisO?
  def subscribeO[V](getObservable: this.type => Observable[V])(observer: Observer[V]): Subscription = {
    subscribeO(getObservable(this))(observer)
  }

  def subscribe[V](observable: Observable[V])(onNext: V => Unit): Subscription = {
    subscribeO(observable)(Observer(onNext))
  }

  // @TODO[Naming] Rename to subscribeThis?
  def subscribe[V](getObservable: this.type => Observable[V])(onNext: V => Unit): Subscription = {
    subscribeO(getObservable(this))(Observer(onNext))
  }

  def subscribeBus[V](
    sourceStream: EventStream[V],
    targetBus: WriteBus[V]
  ): EventBusSource[V] = {
    targetBus.addSource(sourceStream)(owner = this)
  }

  /** Check whether the node is currently mounted.
    *
    * You can use this method to simplify your code and possibly improve performance
    * where you'd otherwise need to subscribe to and transform [[mountEvents]]
    */
  def isMounted: Boolean = {
    isParentMounted(maybeParent)
  }

  /** This hook is exposed by Scala DOM Builder for this exact purpose */
  override def willSetParent(maybeNextParent: Option[ParentNode.Base]): Unit = {
    // @TODO[Integrity] In dombuilder, willSetParent is called at incorrect time in ParentNode.appendChild
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

  override def setParent(maybeNextParent: Option[ParentNode.Base]): Unit = {
    // @TODO[Integrity] Beware of calling setParent directly – willSetParent will not be called?
    // @TODO[Integrity] this method (and others) should be protected
    val maybePrevParent = maybeParent
    super.setParent(maybeNextParent)
    // dom.console.log(">>>> setParent", this.ref.tagName + "(" + this.ref.textContent + ")", maybePrevParent == maybeNextParent, maybePrevParent.toString, maybeNextParent.toString)
    if (maybeNextParent != maybePrevParent) {
      maybeParentChangeBus.foreach( bus => {
        bus.writer.onNext(ParentChangeEvent(
          alreadyChanged = true,
          maybePrevParent = maybePrevParent,
          maybeNextParent = maybeNextParent
        ))
      })
      val prevParentIsMounted = isParentMounted(maybePrevParent)
      val nextParentIsMounted = isParentMounted(maybeNextParent)
      if (!prevParentIsMounted && nextParentIsMounted) {
        maybeThisNodeMountEventBus.foreach(_.writer.onNext(NodeDidMount))
      }
      if (prevParentIsMounted && !nextParentIsMounted) {
        maybeThisNodeMountEventBus.foreach { bus =>
          bus.writer.onNext(NodeWasDiscarded) // @TODO this should be optional
        }
      }
    }
  }

  override protected[this] def onOwned(owned: Owned): Unit = {
    val isFirstSubscription = possessions.length == 1
    if (isFirstSubscription) {
      // Create a subscription that will kill all subscriptions that this element owns when it is discarded.
      // Note: Once this subscription is created, it will persist until this element is discarded.
      //       This might be suboptimal in a narrow range of conditions, I don't think anyone will run into those.
      // @TODO[Performance] ^^^^ The above can be addressed by e.g. overriding onKilledExternally (but permissions)
      mountEvents.filter(_ == NodeWasDiscarded).foreach(_ => killPossessions() )(owner = this)
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
