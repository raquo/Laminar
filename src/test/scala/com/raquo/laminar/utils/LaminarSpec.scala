package com.raquo.laminar.utils

import com.raquo.laminar.{RNode, RNodeData, ReactiveBuilders}
import com.raquo.snabbdom.utils.testing.{DomEventSimulatorSpec, MountSpec}
import com.raquo.snabbdom.utils.testing.matching.RuleImplicits
import org.scalatest.Suite

trait LaminarSpec
  extends MountSpec[RNode, RNodeData]
  with ReactiveBuilders
  with RuleImplicits[RNode, RNodeData]
  with DomEventSimulatorSpec { this: Suite =>}
