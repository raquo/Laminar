package com.raquo.laminar.nodes

import com.raquo.laminar.DomApi
import com.raquo.laminar.setters.EventPropSetter
import org.scalajs.dom

import scala.collection.mutable

trait EventfulNode[+Ref <: dom.Element] { this: ReactiveElement[Ref] =>

  // @TODO[Naming] We reuse EventPropSetter to represent an active event listener. Makes for a bit confusing naming.
  protected[this] var _maybeEventListeners: Option[mutable.Buffer[EventPropSetter[_]]] = None

  @inline def maybeEventListeners: Option[mutable.Buffer[EventPropSetter[_]]] = _maybeEventListeners

  /** @return Whether listener was added (false if such a listener has already been present) */
  def addEventListener[Ev <: dom.Event](eventPropSetter: EventPropSetter[Ev]): Boolean = {
    val shouldAddListener = indexOfEventListener(eventPropSetter) == -1
    if (shouldAddListener) {
      // 1. Update this node
      if (_maybeEventListeners.isEmpty) {
        _maybeEventListeners = Some(mutable.Buffer(eventPropSetter))
      } else {
        _maybeEventListeners.foreach { eventListeners =>
          eventListeners += eventPropSetter
        }
      }
      // 2. Update the DOM
      DomApi.addEventListener(this, eventPropSetter)
    }
    shouldAddListener
  }

  def removeEventListener[Ev <: dom.Event](eventPropSetter: EventPropSetter[Ev]): Boolean = {
    val index = indexOfEventListener(eventPropSetter)
    val shouldRemoveListener = index != -1
    if (shouldRemoveListener) {
      // 1. Update this node
      _maybeEventListeners.foreach(eventListeners => eventListeners.remove(index))
      // 2. Update the DOM
      DomApi.removeEventListener(this, eventPropSetter)
    }
    shouldRemoveListener
  }

  def indexOfEventListener[Ev <: dom.Event](eventPropSetter: EventPropSetter[Ev]): Int = {
    // Note: Ugly for performance.
    //  - We want to reduce usage of Scala's collections and anonymous functions
    //  - js.Array is unaware of Scala's `equals` method
    val notFoundIndex = -1
    if (_maybeEventListeners.isEmpty) {
      notFoundIndex
    } else {
      var found = false
      var index = 0
      _maybeEventListeners.foreach { listeners =>
        while (!found && index < listeners.length) {
          if (eventPropSetter equals listeners(index)) {
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

