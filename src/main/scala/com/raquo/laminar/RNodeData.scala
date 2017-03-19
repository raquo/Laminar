package com.raquo.laminar

import com.raquo.snabbdom.nodes.NodeData

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

@ScalaJSDefined
class RNodeData extends NodeData[RNode, RNodeData] {
  var subscriptions: js.UndefOr[Subscriptions] = js.undefined
}
