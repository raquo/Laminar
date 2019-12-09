package com.raquo.laminar.setters

import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.nodes.EventfulNode
import org.scalajs.dom

import scala.scalajs.js

/** @param useCapture – see https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener about "useCapture"
  */
class EventPropSetter[Ev <: dom.Event](
  val key: EventProp[Ev],
  val value: Ev => Unit,
  val useCapture: Boolean
) extends Modifier[EventfulNode[dom.Element]] {

  /** To make sure that you remove the event listener successfully in JS DOM, you need to
    * provide the same Javascript callback function that was originally added as a listener.
    * However, the implicit conversion from a Scala function to a JS function creates a new
    * JS function every time, so we would never get referentially equal JS functions if we
    * used the Scala-to-JS conversion more than once. Therefore, we need to perform that
    * conversion only once and save the result. This method encapsulates such conversion.
    */
  val domValue: js.Function1[Ev, Unit] = value

  override def apply(node: EventfulNode[dom.Element]): Unit = {
    node.addEventListener(this)
  }

  override def equals(that: Any): Boolean = {
    that match {
      case setter: EventPropSetter[_] if (key == setter.key) && (domValue == setter.domValue) => true
      case _ => false
    }
  }
}
