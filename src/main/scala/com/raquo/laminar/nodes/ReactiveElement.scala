package com.raquo.laminar.nodes

import com.raquo.airstream.core.{Observable, Observer, Subscription}
import com.raquo.airstream.eventbus.{EventBus, EventBusSource, WriteBus}
import com.raquo.airstream.eventstream.EventStream
import com.raquo.airstream.ownership.{Owned, Owner}
import com.raquo.airstream.signal.Signal
import com.raquo.dombuilder.generic
import com.raquo.dombuilder.jsdom.JsCallback
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.emitter.EventPropEmitter
import com.raquo.laminar.lifecycle.{MountEvent, NodeDidMount, NodeWasDiscarded, NodeWillUnmount, ParentChangeEvent}
import com.raquo.laminar.nodes.ReactiveChildNode.isParentMounted
import com.raquo.laminar.nodes.ReactiveElement.noMountEvents
import com.raquo.laminar.receivers.{ChildReceiver, ChildrenCommandReceiver, ChildrenReceiver, MaybeChildReceiver, TextChildReceiver}
import org.scalajs.dom

trait ReactiveElement[+Ref <: dom.Element]
  extends ReactiveNode
  with ReactiveChildNode[Ref]
  with generic.nodes.Element[ReactiveNode, Ref, dom.Node]
  with generic.nodes.EventfulNode[ReactiveNode, Ref, dom.Element, dom.Node, dom.Event, JsCallback]
  with generic.nodes.ParentNode[ReactiveNode, Ref, dom.Node]
  with Owner {

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

  lazy val maybeParentSignal: Signal[Option[BaseParentNode]] = parentChangeEvents
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

  def subscribeO[V](getObservable: this.type => Observable[V])(observer: Observer[V]): Subscription = {
    subscribeO(getObservable(this))(observer)
  }

  def subscribe[V](observable: Observable[V])(onNext: V => Unit): Subscription = {
    subscribeO(observable)(Observer(onNext))
  }

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
  override def willSetParent(maybeNextParent: Option[BaseParentNode]): Unit = {
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

  override def setParent(maybeNextParent: Option[BaseParentNode]): Unit = {
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

  private val noMountEvents: EventStream[MountEvent] = EventStream.fromSeq(Nil)
}
