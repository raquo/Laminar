package com.raquo.laminar.utils

import com.raquo.dombuilder.generic.nodes.Root
import com.raquo.dombuilder.jsdom.nodes.ChildNode
import com.raquo.dombuilder.utils.testing.matching.{ExpectedNode, RuleImplicits}
import com.raquo.dombuilder.utils.testing.{DomEventSimulatorSpec, MountSpec}
import com.raquo.laminar
import com.raquo.laminar.builders.{ReactiveCommentBuilder, ReactiveTextBuilder}
import com.raquo.laminar.nodes.ReactiveNode
import org.scalajs.dom
import org.scalatest.Suite

trait LaminarSpec
  extends MountSpec[ReactiveNode]
  with RuleImplicits[ReactiveNode]
  with DomEventSimulatorSpec
{
  this: Suite =>

  override def comment: ExpectedNode[ReactiveNode] = {
    new ExpectedNode(ReactiveCommentBuilder)
  }

  override def textNode: ExpectedNode[ReactiveNode] = {
    new ExpectedNode(ReactiveTextBuilder)
  }

  override def makeRoot(
    container: dom.Element,
    child: ReactiveNode with ChildNode[ReactiveNode, dom.Element]
  ): Root[ReactiveNode, ReactiveNode with ChildNode[ReactiveNode, dom.Element], dom.Element, dom.Node] = {
    laminar.render(container, child)
  }
}
