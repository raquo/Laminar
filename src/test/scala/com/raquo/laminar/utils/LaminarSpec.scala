package com.raquo.laminar.utils

import com.raquo.laminar.{RNode, ReactiveBuilders}
import com.raquo.snabbdom.utils.testing.{DomEventSimulatorSpec, MountSpec}
import com.raquo.snabbdom.utils.testing.matching.RuleImplicits
import org.scalatest.Suite

trait LaminarSpec
  extends MountSpec[RNode]
  with ReactiveBuilders
  with RuleImplicits[RNode]
  with DomEventSimulatorSpec { this: Suite =>}
