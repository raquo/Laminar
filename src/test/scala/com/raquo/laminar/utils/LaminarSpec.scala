package com.raquo.laminar.utils

import com.raquo.domtestutils.{EventSimulator, MountOps}
import com.raquo.domtestutils.matching._
import com.raquo.laminar.api._
import com.raquo.laminar.api.L.CompositeSvgAttr
import com.raquo.laminar.codecs.StringAsIsCodec
import com.raquo.laminar.defs.complex.ComplexHtmlKeys.CompositeHtmlAttr
import com.raquo.laminar.keys.{CompositeKey, HtmlAttr, HtmlProp, StyleProp, SvgAttr}
import com.raquo.laminar.nodes.{CommentNode, ReactiveElement, ReactiveHtmlElement, ReactiveSvgElement, RootNode}
import com.raquo.laminar.tags.Tag
import org.scalactic
import org.scalajs.dom

trait LaminarSpec
extends MountOps
with RuleImplicits[
  Tag.Base,
  CommentNode,
  HtmlProp,
  HtmlAttr,
  SvgAttr,
  StyleProp,
  CompositeKey[ReactiveHtmlElement.Base],
  CompositeKey[ReactiveSvgElement.Base]
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

  override implicit def makeAttrTestable[V](attr: HtmlAttr[V]): TestableHtmlAttr[V] = {
    new TestableHtmlAttr[V](attr.name, attr.codec.encode, attr.codec.decode)
  }

  override implicit def makePropTestable[V, DomV](prop: HtmlProp[V, DomV]): TestableProp[V, DomV] = {
    new TestableProp[V, DomV](prop.name, prop.codec.decode)
  }

  override implicit def makeStyleTestable[V](style: StyleProp[V]): TestableStyleProp[V] = {
    new TestableStyleProp[V](style.name)
  }

  override implicit def makeSvgAttrTestable[V](svgAttr: SvgAttr[V]): TestableSvgAttr[V] = {
    new TestableSvgAttr[V](svgAttr.name, svgAttr.codec.encode, svgAttr.codec.decode, svgAttr.namespaceUri)
  }

  override implicit def makeCompositeHtmlKeyTestable(key: CompositeKey[ReactiveHtmlElement.Base]): TestableCompositeKey = {
    new TestableCompositeKey(key.name, key.separator, getRawDomValue = {
      case htmlEl: dom.html.Element => htmlEl.getAttribute(key.name)
    })
  }

  override implicit def makeCompositeSvgKeyTestable(key: CompositeKey[ReactiveSvgElement.Base]): TestableCompositeKey = {
    new TestableCompositeKey(key.name, key.separator, getRawDomValue = {
      case svgEl: dom.svg.Element => svgEl.getAttributeNS(namespaceURI = null, key.name)
    })
  }
}
