package com.raquo.laminar.utils

import com.raquo.laminar
import com.raquo.laminar.{RNode, RNodeData, ReactiveBuilders}
import com.raquo.snabbdom.NativeModule
import com.raquo.snabbdom.hooks.ModuleHooks
import com.raquo.snabbdom.utils.testing.{DomEventSimulatorSpec, MountSpec}
import com.raquo.snabbdom.utils.testing.matching.RuleImplicits
import org.scalatest.Suite

import scala.scalajs.js
import scala.scalajs.js.|

trait LaminarSpec
  extends MountSpec[RNode, RNodeData]
  with ReactiveBuilders
  with RuleImplicits[RNode, RNodeData]
  with DomEventSimulatorSpec
{ this: Suite =>

  override val snabbdomModules: js.Array[NativeModule | ModuleHooks[RNode, RNodeData]] = laminar.modules
}
