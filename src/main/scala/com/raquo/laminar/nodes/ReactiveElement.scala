package com.raquo.laminar.nodes

import com.raquo.dombuilder.generic
import com.raquo.dombuilder.jsdom.JsCallback
import com.raquo.dombuilder.jsdom.domapi.JsTreeApi
import com.raquo.domtypes.generic.keys.{Attr, EventProp, Prop}
import com.raquo.laminar.DomApi
import com.raquo.laminar.emitter.{EventBus, EventPropEmitter}
import com.raquo.laminar.receivers.{ChildReceiver, ChildrenCommandReceiver, ChildrenReceiver, LockedAttrReceiver, LockedPropReceiver, MaybeChildReceiver, TextChildReceiver}
import com.raquo.xstream.XStream
import org.scalajs.dom

class ReactiveElement[+Ref <: dom.Element](
  override val tagName: String,
  override val void: Boolean
) extends ReactiveNode
  with ReactiveChildNode[Ref]
  with generic.nodes.Element[ReactiveNode, Ref, dom.Node]
  with generic.nodes.EventfulNode[ReactiveNode, Ref, dom.Element, dom.Node, dom.Event, JsCallback]
  with generic.nodes.ParentNode[ReactiveNode, Ref, dom.Node] {

  override val treeApi: JsTreeApi[ReactiveNode] = DomApi.treeApi

  override val ref: Ref = DomApi.elementApi.createNode(this)

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

  def $event[Ev <: dom.Event](
    eventProp: EventProp[Ev],
    useCapture: Boolean = false,
    stopPropagation: Boolean = false,
    preventDefault: Boolean = false
  ): XStream[Ev] = {
    // @TODO[Integrity] is EventBus the correct thing to use here? Maybe a manual producer, or would it just be the same?
    val eventBus = new EventBus[Ev]
    val setter = EventPropEmitter.eventPropSetter(
      eventProp,
      sendNext = eventBus.sendNext,
      useCapture = useCapture,
      preventDefault = preventDefault,
      stopPropagation = stopPropagation
    )
    setter(this)
    eventBus.$
  }
}
