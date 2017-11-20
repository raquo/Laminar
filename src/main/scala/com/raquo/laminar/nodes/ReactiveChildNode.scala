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
  private var maybeParentChangeBus: Option[EventBus[ParentChangeEvent]] = None

  /** Stream of parent change events for this node.
    * For efficiency, it is lazy loaded, only being initialized when accessed,
    * either directly or (more commonly) as a dependency of [[$mountEvent]]
    */
  lazy val $parentChange: XStream[ParentChangeEvent] = {
    val parentChangeBus = new EventBus[ParentChangeEvent]
    maybeParentChangeBus = Some(parentChangeBus)
    parentChangeBus.$
  }

  lazy val $mountEvent: XStream[MountEvent] = {
    // $parentMountEvent emits mount events from the current node's parent,
    // making sure to account for parent changes.
    val $parentMountEvent: XStream[MountEvent] = $parentChange.collect {
      case ParentChangeEvent(false, _, maybeNextParent) =>
        maybeNextParent match {
          case Some(nextParent: ReactiveElement[_]) => nextParent.$mountEvent
          case _ => $noMountEvents
        }
    }.flatten

    // isParentMounted is expensive – it accesses DOM recursively until it finds the top level parent,
    // so we optimize by calculating this only once (in XStream all stream executions are shared)
    // @TODO[Performance] Further optimization is possible (e.g. don't compute anything when neither event would fire) but needs benchmarking.
    // @TODO[API] Should we move some of this into ParentChangeEvent class?
    val $parentChangeWithMountStatus = $parentChange.map(parentChange => (
      parentChange,
      isParentMounted(parentChange.maybePrevParent),
      isParentMounted(parentChange.maybeNextParent)
    ))

    val $thisNodeDidMount = $parentChangeWithMountStatus
      .filter3((parentChange, isPrevMounted, isNextMounted) =>
        parentChange.alreadyChanged && !isPrevMounted && isNextMounted
      )
      .mapTo(NodeDidMount)
    val $thisNodeWillUnmount = $parentChangeWithMountStatus
      .filter3((parentChange, isPrevMounted, isNextMounted) =>
        !parentChange.alreadyChanged && isPrevMounted && !isNextMounted
      )
      .mapTo(NodeWillUnmount)
    val $thisNodeWillBeDiscarded = $thisNodeWillUnmount.mapTo(NodeWillBeDiscarded)

    XStream.merge($parentMountEvent, $thisNodeDidMount, $thisNodeWillUnmount, $thisNodeWillBeDiscarded)
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
    maybeParentChangeBus.foreach(_.sendNext(ParentChangeEvent(
      alreadyChanged = false,
      maybePrevParent = maybeParent,
      maybeNextParent = maybeNextParent
    )))
  }

  override def setParent(maybeNextParent: Option[BaseParentNode]): Unit = {
    val maybePrevParent = maybeParent
    super.setParent(maybeNextParent)
    maybeParentChangeBus.foreach(_.sendNext(ParentChangeEvent(
      alreadyChanged = true,
      maybePrevParent = maybePrevParent,
      maybeNextParent = maybeNextParent
    )))
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

  // @TODO What to do with SDB implementation?
  @tailrec final def isDescendantOf(node: dom.Node, ancestor: dom.Node): Boolean = {
    node.parentNode match {
      case null => false
      case `ancestor` => true
      case _ => isDescendantOf(node.parentNode, ancestor)
    }
  }
}
