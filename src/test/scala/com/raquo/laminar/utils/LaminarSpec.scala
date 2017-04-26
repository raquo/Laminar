package com.raquo.laminar.utils

import com.raquo.dombuilder.jsdom.domapi.JsTreeApi
import com.raquo.dombuilder.utils.testing.matching.RuleImplicits
import com.raquo.dombuilder.utils.testing.{DomEventSimulatorSpec, MountSpec}
import com.raquo.laminar
import com.raquo.laminar.builders.ReactiveTextBuilder
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveNode}
import org.scalatest.Suite

trait LaminarSpec
  extends MountSpec[ReactiveElement, ReactiveNode]
    with RuleImplicits[ReactiveNode]
    with DomEventSimulatorSpec {
  this: Suite =>

  override val textNodeBuilder: ReactiveTextBuilder = laminar.textBuilder

  override val treeApi: JsTreeApi[ReactiveNode] = laminar.treeApi
}
