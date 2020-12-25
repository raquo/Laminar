package com.raquo.laminar.utils

import com.raquo.domtestutils.matching.{RuleImplicits, TestableHtmlAttr, TestableProp, TestableSvgAttr}
import com.raquo.domtestutils.{EventSimulator, MountOps}
import com.raquo.laminar.api.Laminar.CompositeSvgAttr
import com.raquo.laminar.api._
import com.raquo.laminar.defs.ReactiveComplexHtmlKeys.{CompositeHtmlAttr, CompositeProp}
import com.raquo.laminar.nodes.{ReactiveElement, RootNode}

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
  var root: RootNode = null

  def mount(
    node: ReactiveElement.Base,
    clue: String = defaultMountedElementClue
  ): Unit = {
    mountedElementClue = clue
    assertEmptyContainer("laminar.mount")
    root = L.render(containerNode, node)
  }

  def mount(
    clue: String,
    node: ReactiveElement.Base
  ): Unit = {
    mount(node, clue)
  }

  override def unmount(clue: String = "unmount"): Unit = {
    assertRootNodeMounted("unmount:" + clue)
    doAssert(
      root != null,
      s"ASSERT FAILED [unmount:$clue]: Laminar root not found. Did you use Laminar's mount() method in LaminarSpec? Note: unfortunately this could conceal the true error message."
    )
    doAssert(
      root.child.ref == rootNode,
      s"ASSERT FAILED [unmount:$clue]: Laminar root's ref does not match rootNode. What did you do!?"
    )
    doAssert(
      root.unmount(),
      s"ASSERT FAILED [unmount:$clue]: Laminar root failed to unmount"
    )
    root = null
    // containerNode = null
    mountedElementClue = defaultMountedElementClue
  }

  implicit def makeCompositePropTestable[V](prop: CompositeProp[V]): TestableProp[V, V] = {
    new TestableProp(prop.key)
  }

  implicit def makeCompositeHtmlAttrTestable[V](attr: CompositeHtmlAttr[V]): TestableHtmlAttr[V] = {
    new TestableHtmlAttr(attr.key)
  }

  implicit def makeCompositeSvgAttrTestable[V](attr: CompositeSvgAttr[V]): TestableSvgAttr[V] = {
    new TestableSvgAttr(attr.key)
  }
}
