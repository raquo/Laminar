package com.raquo.laminar.utils

import com.raquo.dombuilder.generic.nodes.Root
import com.raquo.dombuilder.jsdom.nodes.ChildNode

import com.raquo.domtestutils.EventSimulator
import com.raquo.domtestutils.matching.{ExpectedNode, RuleImplicits}
import com.raquo.domtestutils.scalatest.MountSpec

import com.raquo.laminar
import com.raquo.laminar.builders.{ReactiveCommentBuilder, ReactiveTextBuilder}
import com.raquo.laminar.nodes.ReactiveNode

import org.scalajs.dom

trait LaminarSpec
  extends MountSpec[ReactiveNode]
  with RuleImplicits[ReactiveNode]
  with EventSimulator
{
  // === On nullable variables ===
  // `root` is nullable because if it was an Option it would be too easy to
  // forget to handle the `None` case when mapping or foreach-ing over it.
  // In test code, We'd rather have a null pointer exception than an assertion that you don't
  // realize isn't running because it's inside a None.foreach.
  var root: Root[ReactiveNode, ReactiveNode with ChildNode[ReactiveNode, dom.Element], dom.Element, dom.Node] = null

  def mount(
    node: ReactiveNode with ChildNode[ReactiveNode, dom.Element],
    clue: String = defaultMountedElementClue
  ): Unit = {
    mountedElementClue = clue
    assertEmptyContainer("laminar.mount")
    root = laminar.render(containerNode, node)
  }

  def mount(
    clue: String,
    node: ReactiveNode with ChildNode[ReactiveNode, dom.Element]
  ): Unit = {
    mount(node, clue)
  }

  override def unmount(): Unit = {
    assertRootNodeMounted("laminar.unmount")
    doAssert(
      root != null,
      "ASSERT FAILED [laminar.unmount]: Laminar root not found. Did you use Laminar's mount() method in LaminarSpec?"
    )
    doAssert(
      root.child.ref == rootNode,
      "ASSERT FAILED [laminar.unmount]: Laminar root's ref does not match rootNode. What did you do!?"
    )
    doAssert(
      root.unmount(),
      "ASSERT FAILED [laminar.unmount]: Laminar root failed to unmount"
    )
    mountedElementClue = defaultMountedElementClue
  }

  override def comment: ExpectedNode[ReactiveNode] = {
    new ExpectedNode(ReactiveCommentBuilder)
  }

  override def textNode: ExpectedNode[ReactiveNode] = {
    new ExpectedNode(ReactiveTextBuilder)
  }
}
