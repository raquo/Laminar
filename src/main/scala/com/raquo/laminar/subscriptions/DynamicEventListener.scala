package com.raquo.laminar.subscriptions

import com.raquo.laminar.RNode
import com.raquo.xstream.Listener

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.|

/** Dynamic listener lets us patch the onNext callback with a new mounted node on the fly
  * This is useful to efficiently copy subscriptions from old node to new node for snabbdom patching
  */
@ScalaJSDefined
class DynamicEventListener[T](
  private var activeNode: RNode,
  onNext: (T, RNode, DynamicEventListener[T]) => Unit
) extends Listener[T, Exception] {

  override def next(value: T): Unit = {
    onNext(value, activeNode, this)
  }

  // @TODO[Elegance] This class should extend Listener[T, Nothing] but that does not compile because
  // @TODO[Elegance] Scala.js would need to match{} against a Nothing type to dispatch the method
  override def error(error: Exception): Unit = {
    // @TODO ???
    throw error
  }

  override def error(error: Exception | js.Error): Unit = {
    // @TODO ???
    error match {
      case e: js.Error => throw js.JavaScriptException(e)
      case _ => throw error.asInstanceOf[Exception]
    }
  }

  override def complete(): Unit = {
    // //@TODO ???
  }

  /** Call this when snabbdom patch() has given us a new node */
  def setActiveNode(newNode: RNode): Unit = {
    activeNode = newNode
  }
}
