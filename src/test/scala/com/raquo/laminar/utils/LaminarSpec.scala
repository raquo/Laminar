package com.raquo.laminar.utils

import com.raquo.domtestutils.{EventSimulator, MountOps}
import com.raquo.domtestutils.matching._
import com.raquo.laminar.api._
import com.raquo.laminar.keys._
import com.raquo.laminar.nodes._
import com.raquo.laminar.tags.Tag
import org.scalactic

trait LaminarSpec
extends MountOps
with RuleImplicits[
  Tag.Base,
  CommentNode,
  HtmlProp,
  GlobalAttr,
  HtmlAttr,
  SvgAttr,
  MathMlAttr,
  StyleProp,
  CompositeAttr[_],
]
with EventSimulator {
  // === On nullable variables ===
  // `root` is nullable because if it was an Option it would be too easy to
  // forget to handle the `None` case when mapping or foreach-ing over it.
  // In test code, We'd rather have a null pointer exception than an assertion that you don't
  // realize isn't running because it's inside a None.foreach.
  var root: RootNode = null

  def sentinel: ExpectedNode = ExpectedNode.comment

  /** You can use this when `sentinel` does not make sense semantically */
  def emptyCommentNode: ExpectedNode = ExpectedNode.comment

  def mount(
    node: ReactiveElement.Base,
    clue: String = defaultMountedElementClue
  )(implicit
    prettifier: scalactic.Prettifier,
    pos: scalactic.source.Position
  ): Unit = {
    mountedElementClue = clue
    assertEmptyContainer("laminar.mount")
    root = L.render(containerNode, node)
  }

  def mount(
    clue: String,
    node: ReactiveElement.Base
  )(implicit
    prettifier: scalactic.Prettifier,
    pos: scalactic.source.Position
  ): Unit = {
    mount(node, clue)(prettifier, pos)
  }

  override def unmount(
    clue: String = "unmount"
  )(implicit
    prettifier: scalactic.Prettifier,
    pos: scalactic.source.Position
  ): Unit = {
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

  override implicit def makeTagTestable(tag: Tag.Base): ExpectedNode = {
    ExpectedNode.element(tag.name)
  }

  override implicit def makeCommentBuilderTestable(commentBuilder: () => CommentNode): ExpectedNode = {
    ExpectedNode.comment
  }

  override implicit def makeHtmlPropTestable[V, _DomV](prop: HtmlProp[V] { type DomV = _DomV }): TestableHtmlProp[V, _DomV] = {
    new TestableHtmlProp[V, _DomV](prop.name, prop.codec.decode)
  }

  override implicit def makeStyleTestable[V](style: StyleProp[V]): TestableStyleProp[V] = {
    new TestableStyleProp[V](style.name)
  }

  override implicit def makeGlobalAttrTestable[V](attr: GlobalAttr[V]): TestableGlobalAttr[V] = {
    new TestableGlobalAttr[V](attr.name, attr.codec.encode, attr.codec.decode)
  }

  override implicit def makeHtmlAttrTestable[V](attr: HtmlAttr[V]): TestableHtmlAttr[V] = {
    new TestableHtmlAttr[V](attr.name, attr.codec.encode, attr.codec.decode)
  }

  override implicit def makeSvgAttrTestable[V](svgAttr: SvgAttr[V]): TestableSvgAttr[V] = {
    new TestableSvgAttr[V](svgAttr.name, svgAttr.codec.encode, svgAttr.codec.decode, svgAttr.namespaceUri)
  }

  override implicit def makeMathMlAttrTestable[V](attr: MathMlAttr[V]): TestableMathMlAttr[V] = {
    new TestableMathMlAttr[V](attr.name, attr.codec.encode, attr.codec.decode)
  }

  override implicit def makeCompositeKeyTestable(key: CompositeAttr[_]): TestableCompositeKey = {
    new TestableCompositeKey(key.name, key.separator, getRawDomValue = _.getAttribute(key.name))
  }

}
