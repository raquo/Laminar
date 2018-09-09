package com.raquo.laminar.utils

import com.raquo.domtestutils.{EventSimulator, MountOps}
import com.raquo.domtestutils.matching.{RuleImplicits, TestableHtmlAttr, TestableSvgAttr}
import com.raquo.domtypes.generic.keys.{HtmlAttr, SvgAttr}
import com.raquo.laminar.api._
import com.raquo.laminar.DomApi
import com.raquo.laminar.keys.CompositeAttr
import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveRoot}
import org.scalajs.dom

trait LaminarSpec
  extends MountOps
  with RuleImplicits
  with EventSimulator
{
  // === On nullable variables ===
  // `root` is nullable because if it was an Option it would be too easy to
  // forget to handle the `None` case when mapping or foreach-ing over it.
  // In test code, We'd rather have a null pointer exception than an assertion that you don't
  // realize isn't running because it's inside a None.foreach.
  var root: ReactiveRoot = null

  def mount(
    node: ReactiveChildNode[dom.Element],
    clue: String = defaultMountedElementClue
  ): Unit = {
    mountedElementClue = clue
    assertEmptyContainer("laminar.mount")
    root = L.render(containerNode, node)
  }

  def mount(
    clue: String,
    node: ReactiveChildNode[dom.Element]
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
      root.unmount()(DomApi.treeApi),
      "ASSERT FAILED [laminar.unmount]: Laminar root failed to unmount"
    )
    mountedElementClue = defaultMountedElementClue
  }

  implicit def makeCompositeHtmlAttrTestable[V](attr: CompositeAttr[HtmlAttr[V]]): TestableHtmlAttr[V] = {
    new TestableHtmlAttr(attr.key)
  }

  implicit def makeCompositeSvgAttrTestable[V](attr: CompositeAttr[SvgAttr[V]]): TestableSvgAttr[V] = {
    new TestableSvgAttr(attr.key)
  }
}
