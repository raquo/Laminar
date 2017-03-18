package com.raquo.laminar

import com.raquo.snabbdom.collections.Builders

import scala.scalajs.js

trait ReactiveBuilders extends Builders[RNode] {
  override def vnode(tagName: js.UndefOr[String]): RNode = {
    new RNode(tagName)
  }
}
