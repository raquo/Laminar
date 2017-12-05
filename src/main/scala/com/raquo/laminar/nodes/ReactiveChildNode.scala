package com.raquo.laminar.nodes

import com.raquo.dombuilder.generic.nodes.{ChildNode, ParentNode}
import com.raquo.dombuilder.jsdom.domapi.JsTreeApi
import com.raquo.laminar.emitter.EventBus
import com.raquo.laminar.emitter.EventBus.WriteBus
import com.raquo.laminar.lifecycle.{MountEvent, NodeDidMount, NodeWillBeDiscarded, NodeWillUnmount, ParentChangeEvent}
import com.raquo.laminar.nodes.ReactiveChildNode.{$noMountEvents, isParentMounted}
import com.raquo.laminar.{DomApi, DynamicSubscription}
import com.raquo.xstream.{Listener, XStream}
import org.scalajs.dom

import scala.annotation.tailrec
import scala.scalajs.js

trait ReactiveChildNode[+Ref <: dom.Node]
  extends ReactiveNode
  with ChildNode[ReactiveNode, Ref, dom.Node] {

  override val treeApi: JsTreeApi[ReactiveNode] = DomApi.treeApi

  /** Event bus that emits parent change events.
    * For efficiency, it is only initialized when someone accesses [[$parentChange]].
    * Normally this happens when subscribing to [[$parentChange]] or [[$mountEvent]].
    */
  private[laminar] var maybeParentChangeBus: Option[EventBus[ParentChangeEvent]] = None

  private[laminar] var maybeThisNodeMountEventBus: Option[EventBus[MountEvent]] = None

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
    val $maybeParent = $parentChange
      .filter(!_.alreadyChanged) // @TODO[Integrity] is this important?
      .map(_.maybeNextParent)
      .startWith(maybeParent)

    val $$parentMountEvent: XStream[XStream[MountEvent]] = $maybeParent.map { maybeParent =>
      maybeParent match { // @TODO[Elegance] can we shorten this without creating a partial function? @TODO[Performance] Maybe move all this to ReactiveElement?
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

  private lazy val mountEventSubscription: DynamicSubscription[MountEvent] = new DynamicSubscription(
    $mountEvent,
    Listener(onNext = {
      case `NodeDidMount` => subscriptions.foreach(_.activate())
      case `NodeWillUnmount` => subscriptions.foreach(_.deactivate())
      case `NodeWillBeDiscarded` => mountEventSubscription.deactivate()
    })
  )

  private lazy val subscriptions: js.Array[DynamicSubscription[_]] = js.Array()

  /** Check whether the node is currently mounted.
    * You can use this method to simplify your code and possibly improve performance
    * where you'd otherwise need to subscribe to and transform [[$mountEvent]]
    */
  @inline def isMounted: Boolean = {
    isParentMounted(maybeParent)
  }

  def subscribe[V](
    $value: XStream[V],
    listener: Listener[V]
  ): Unit = {
    val subscription = new DynamicSubscription($value, listener)
    subscriptions.push(subscription)
    mountEventSubscription.activate()
    if (isMounted) {
      subscription.activate()
      // Otherwise, subscription will activate if/when node is mounted
    }
  }

  @inline def subscribeBus[V](
    $source: XStream[V],
    targetBus: WriteBus[V]
  ): Unit = {
    subscribe($value = $source, listener = Listener(onNext = targetBus.sendNext))
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

object ReactiveChildNode {

  type BaseParentNode = ReactiveNode with ParentNode[ReactiveNode, dom.Node, dom.Node]
  //  type BaseChildNode = ReactiveNode with ChildNode[ReactiveNode, dom.Node, dom.Node]

  val $noMountEvents: XStream[MountEvent] = XStream.fromSeq(Nil)

  def isParentMounted(maybeParent: Option[BaseParentNode]): Boolean = {
    maybeParent.exists(parent => isNodeMounted(parent.ref))
  }

  @inline def isNodeMounted(node: dom.Node): Boolean = {
    isDescendantOf(node = node, ancestor = dom.document)
  }

  @tailrec final def isDescendantOf(node: dom.Node, ancestor: dom.Node): Boolean = {
    // @TODO[Performance] Maybe use https://developer.mozilla.org/en-US/docs/Web/API/Node/contains instead (but IE only supports it for Elements)
    node.parentNode match {
      case null => false
      case `ancestor` => true
      case intermediateParent => isDescendantOf(intermediateParent, ancestor)
    }
  }
}
