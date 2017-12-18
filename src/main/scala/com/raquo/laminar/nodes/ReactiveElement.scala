package com.raquo.laminar.nodes

import com.raquo.dombuilder.generic
import com.raquo.dombuilder.jsdom.JsCallback
import com.raquo.domtypes.generic.keys.{Attr, EventProp, Prop}
import com.raquo.laminar.emitter.EventPropEmitter
import com.raquo.laminar.lifecycle.{MountEvent, NodeDidMount, NodeWillBeDiscarded, NodeWillUnmount, ParentChangeEvent}
import com.raquo.laminar.nodes.ReactiveElement.$noMountEvents
import com.raquo.laminar.nodes.ReactiveChildNode.isParentMounted
import com.raquo.laminar.receivers.{ChildReceiver, ChildrenCommandReceiver, ChildrenReceiver, LockedAttrReceiver, LockedPropReceiver, MaybeChildReceiver, TextChildReceiver}
import com.raquo.laminar.streams.{EventBus, MergeWriteBus}
import com.raquo.laminar.{DomApi, DynamicSubscription}
import com.raquo.xstream.{Listener, XStream}
import org.scalajs.dom

import scala.scalajs.js

class ReactiveElement[+Ref <: dom.Element](
  override val tagName: String,
  override val void: Boolean
) extends ReactiveNode
  with ReactiveChildNode[Ref]
  with generic.nodes.Element[ReactiveNode, Ref, dom.Node]
  with generic.nodes.EventfulNode[ReactiveNode, Ref, dom.Element, dom.Node, dom.Event, JsCallback]
  with generic.nodes.ParentNode[ReactiveNode, Ref, dom.Node] {

  /** Event bus that emits parent change events.
    * For efficiency, it is only initialized when someone accesses [[$parentChange]]
    */
  private[laminar] var maybeParentChangeBus: Option[EventBus[ParentChangeEvent]] = None

  /** Event bus that emits this node's mount events.
    * For efficiency, it is only initialized when someone accesses [[$thisNodeMountEvent]]
    */
  private[laminar] var maybeThisNodeMountEventBus: Option[EventBus[MountEvent]] = None

  /** This built-in subscription monitors the element's mount events, and activates / deactivates other subscriptions accordingly */
  private lazy val mountEventSubscription: DynamicSubscription[MountEvent] = new DynamicSubscription(
    $mountEvent,
    Listener[MountEvent](onNext = {
      case NodeDidMount => subscriptions.foreach(_.activate())
      case NodeWillUnmount => subscriptions.foreach(_.deactivate())
      case NodeWillBeDiscarded => mountEventSubscription.deactivate()
    })
  )

  private lazy val subscriptions: js.Array[DynamicSubscription[_]] = js.Array()

  override val ref: Ref = DomApi.elementApi.createNode(this)

  /** Stream of parent change events for this node.
    * For efficiency, it is lazy loaded, only being initialized when accessed,
    * either directly or (more commonly) as a dependency of [[$mountEvent]]
    */
  lazy val $parentChange: XStream[ParentChangeEvent] = {
    val parentChangeBus = new EventBus[ParentChangeEvent]
    maybeParentChangeBus = Some(parentChangeBus)
    parentChangeBus.$
  }

  /** Emits mount events from all ancestors of this node, making sure to account for all hierarchy changes. */
  lazy val $ancestorMountEvent: XStream[MountEvent] = {
    val $maybeParent = $parentChange // @TODO[API] expose this stream?
      .filter(!_.alreadyChanged) // @TODO[Integrity] is this important?
      .map(_.maybeNextParent)
      .startWith(maybeParent)

    val $$parentMountEvent: XStream[XStream[MountEvent]] = $maybeParent.map { maybeParent =>
      maybeParent match { // @TODO[Elegance] can we shorten this without creating a partial function? Can we avoid an instanceof check?
        case Some(nextParent: ReactiveElement[_]) =>
          nextParent.$mountEvent
        case _ =>
          $noMountEvents
      }
    }

    $$parentMountEvent.flatten // Important: `flatten` outputs a stream that happens to not be a MemoryStream anymore. This ensures that listeners of this stream don't immediately get the last event on subscription.
  }

  /** Emits mount events caused by this node changing its parent */
  lazy val $thisNodeMountEvent: XStream[MountEvent] = {
    val thisNodeMountEventBus = new EventBus[MountEvent]
    maybeThisNodeMountEventBus = Some(thisNodeMountEventBus)
    thisNodeMountEventBus.$
  }

  /** Emits mount events for this node, including mount events fired by all of its ancestors */
  lazy val $mountEvent: XStream[MountEvent] = {
    XStream.merge($ancestorMountEvent, $thisNodeMountEvent) // @TODO[Integrity] Does the order of merge matter here?
  }

  /** Create and get a stream of events on this node */
  def $event[Ev <: dom.Event](
    eventProp: EventProp[Ev],
    useCapture: Boolean = false,
    stopPropagation: Boolean = false, // @TODO[API] This is inconsistent with EventPropEmitter API. Fix or ok?
    preventDefault: Boolean = false // @TODO[API] This is inconsistent with EventPropEmitter API. Fix or ok?
  ): XStream[Ev] = {
    val eventBus = new EventBus[Ev]
    val setter = new EventPropEmitter[Ev, Ev, Ev, this.type](
      eventBus,
      eventProp,
      useCapture,
      processor = (ev: Ev, _: this.type) => Some(ev)
    )
    setter(this)
    eventBus.$
  }

  @inline def <--[V](childReceiver: ChildReceiver.type): ChildReceiver = new ChildReceiver(this)

  @inline def <--[V](maybeChildReceiver: MaybeChildReceiver.type): MaybeChildReceiver = new MaybeChildReceiver(this)

  @inline def <--[V](textChildReceiver: TextChildReceiver.type): TextChildReceiver = new TextChildReceiver(this)

  @inline def <--[V](childrenReceiver: ChildrenReceiver.type): ChildrenReceiver = new ChildrenReceiver(this)

  @inline def <--[V](childrenCommandReceiver: ChildrenCommandReceiver.type): ChildrenCommandReceiver = new ChildrenCommandReceiver(this)

  @inline def <--[V](attr: Attr[V]): LockedAttrReceiver[V] = new LockedAttrReceiver(attr, this)

  @inline def <--[V, DomV](prop: Prop[V, DomV]): LockedPropReceiver[V, DomV] = new LockedPropReceiver(prop, this)

  //  // @TODO This needs the string magic thing
  //  def <-- [V](style: Style[V]): StyleReceiver[V] = new StyleReceiver(style)

  //  def -->[Ev <: dom.Event](eventProp: EventProp[Ev])

  // @TODO These methods should be replaced bysubscription lifecycle context functionality

  def subscribeDynamic[V](dynamicSubscription: DynamicSubscription[V]): Unit = {
    subscriptions.push(dynamicSubscription)
    mountEventSubscription.activate()
    if (isMounted) {
      dynamicSubscription.activate()
      // Otherwise, subscription will activate if/when node is mounted
    }
  }

  def subscribe[V](
    $value: XStream[V],
    listener: Listener[V]
  ): Unit = {
    subscribeDynamic(new DynamicSubscription($value, listener))
  }

  def subscribeBus[V](
    sourceStream: XStream[V],
    targetBus: MergeWriteBus[V]
  ): Unit = {
    subscribeDynamic(new DynamicSubscription(
      subscribe = () => targetBus.addSource(sourceStream),
      unsubscribe = () => targetBus.removeSource(sourceStream)
    ))
  }

  /** Check whether the node is currently mounted.
    * You can use this method to simplify your code and possibly improve performance
    * where you'd otherwise need to subscribe to and transform [[$mountEvent]]
    */
  @inline def isMounted: Boolean = {
    isParentMounted(maybeParent)
  }

  /** This hook is exposed by Scala DOM Builder for this exact purpose */
  override def willSetParent(maybeNextParent: Option[BaseParentNode]): Unit = {
    if (maybeNextParent != maybeParent) {
      maybeParentChangeBus.foreach(_.sendNext(ParentChangeEvent(
        alreadyChanged = false,
        maybePrevParent = maybeParent,
        maybeNextParent = maybeNextParent
      )))
      if (!isParentMounted(maybeNextParent) && isParentMounted(maybeParent)) {
        maybeThisNodeMountEventBus.foreach { bus =>
          bus.sendNext(NodeWillUnmount)
          bus.sendNext(NodeWillBeDiscarded) // @TODO this should be optional
        }
      }
    }
  }

  override def setParent(maybeNextParent: Option[BaseParentNode]): Unit = {
    // @TODO[Integrity] Beware of calling setParent directly – willSetParent will not be called?
    // @TODO[Integrity] this method (and others) should be protected
    val maybePrevParent = maybeParent
    super.setParent(maybeNextParent)
    if (maybeNextParent != maybePrevParent) {
      maybeParentChangeBus.foreach(_.sendNext(ParentChangeEvent(
        alreadyChanged = true,
        maybePrevParent = maybePrevParent,
        maybeNextParent = maybeNextParent
      )))
      if (!isParentMounted(maybePrevParent) && isParentMounted(maybeNextParent)) {
        maybeThisNodeMountEventBus.foreach(_.sendNext(NodeDidMount))
      }
    }
  }
}

object ReactiveElement {

  val $noMountEvents: XStream[MountEvent] = XStream.fromSeq(Nil)
}
