package com.raquo.laminar

import com.raquo.snabbdom.nodes.NodeData
import com.raquo.xstream.Subscription

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}

@ScalaJSDefined
class RNodeData extends NodeData[RNode, RNodeData] {

  // @TODO[Integrity] Use shapeless or something instead of using [[Any]] type
  // @TODO[Performance] Do not initialize until it's needed
  var subscriptions: js.Array[Subscription[Any, Nothing]] = js.Array()

  @JSName("__scala_copy")
  override def copy(): RNodeData = {
    val newData = super.copy()
    newData.subscriptions = subscriptions.jsSlice()
    newData
  }
}
