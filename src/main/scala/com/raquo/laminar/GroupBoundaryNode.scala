package com.raquo.laminar

import scala.scalajs.js.annotation.ScalaJSDefined

@ScalaJSDefined
class GroupBoundaryNode(
  val groupKey: String,
  val isOpening: Boolean
) extends RNode("!")
