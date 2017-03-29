package com.raquo.laminar

import com.raquo.snabbdom.collections.Builders

import scala.scalajs.js

trait ReactiveBuilders extends Builders[RNode, RNodeData] {

  override def node(tagName: js.UndefOr[String]): RNode = {
    new RNode(tagName)
  }

  override def textNode(text: String): RNode = {
    val node = new RNode(js.undefined)
    node.text = text
    node
  }

  override def nodeData(): RNodeData = {
    new RNodeData
  }
}
