package com.raquo.laminar

import com.raquo.snabbdom.nodes.NodeData

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

// WARNING - When we want to use this trait, we just call .asInstanceOf[]!
// So don't add anything to this trait that doesn't default to js.undefined

// @TODO[Integrity] This is not a good way to do things. Make Node generic of NodeData!

@ScalaJSDefined
trait RichRNodeData extends NodeData[RNode] {
  var subscriptions: js.UndefOr[Subscriptions] = js.undefined
}
