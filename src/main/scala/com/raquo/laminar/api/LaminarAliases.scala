package com.raquo.laminar.api

import com.raquo.laminar.{inserters, keys, lifecycle, modifiers, nodes}
import com.raquo.laminar.domapi
import org.scalajs.dom

import scala.collection.immutable

trait LaminarAliases {

  // Base elements and nodes

  type HtmlElement = nodes.ReactiveHtmlElement.Base

  type SvgElement = nodes.ReactiveSvgElement.Base

  type MathMlElement = nodes.ReactiveMathMlElement

  type Element = nodes.ReactiveElement.Base

  type Node = nodes.ChildNode.Base

  type TextNode = nodes.TextNode

  type CommentNode = nodes.CommentNode

  type RootNode = nodes.RootNode

  @deprecated("`Child` type alias is deprecated. Use ChildNode.Base", "15.0.0-M6")
  type Child = nodes.ChildNode.Base

  @deprecated("`Children`type alias is deprecated. Use immutable.Seq[ChildNode.Base]", "15.0.0-M6")
  type Children = immutable.Seq[nodes.ChildNode.Base]

  @deprecated("`ChildrenCommand` type alias is deprecated. Use CollectionCommand.Base", "15.0.0-M5")
  type ChildrenCommand = CollectionCommand[nodes.ChildNode.Base]

  type CollectionCommand[+Item] = inserters.CollectionCommand[Item]

  lazy val CollectionCommand: inserters.CollectionCommand.type = inserters.CollectionCommand

  // Modifiers

  type Mod[-El <: nodes.ReactiveElement.Base] = modifiers.Modifier[El]

  val Mod: modifiers.Modifier.type = modifiers.Modifier

  type HtmlMod = Mod[HtmlElement]

  type SvgMod = Mod[SvgElement]

  type MathMlMod = Mod[SvgElement]

  //

  type Modifier[-El <: nodes.ReactiveElement.Base] = modifiers.Modifier[El]

  val Modifier: modifiers.Modifier.type = modifiers.Modifier

  //

  type Setter[-El <: Element] = modifiers.Setter[El]

  val Setter: modifiers.Setter.type = modifiers.Setter

  //

  type Binder[-El <: Element] = modifiers.Binder[El]

  val Binder: modifiers.Binder.type = modifiers.Binder

  //

  type Inserter = inserters.Inserter

  type StaticInserter = inserters.StaticInserter

  type DynamicInserter = inserters.DynamicInserter

  //
  // Lifecycle
  //

  type MountContext[+El <: Element] = lifecycle.MountContext[El]

  type InsertContext = inserters.InsertContext

  //
  // Keys & Events
  //

  type EventProp[Ev <: dom.Event] = keys.EventProp[Ev]

  type EventProcessor[Ev <: dom.Event, V] = keys.EventProcessor[Ev, V]

  type HtmlProp[V] = keys.HtmlProp[V]

  type HtmlAttr[V] = keys.HtmlAttr[V]

  type SvgAttr[V] = keys.SvgAttr[V]

  type GlobalAttr[V] = keys.GlobalAttr[V]

  type AriaAttr[V] = keys.AriaAttr[V]

  type CompositeAttr[-El <: nodes.ReactiveElement.Base] = keys.CompositeAttr[El]

  val CompositeAttr: keys.CompositeAttr.type = keys.CompositeAttr

  type HtmlCompositeAttr = keys.CompositeAttr.HtmlCompositeAttr

  type StyleProp[V] = keys.StyleProp[V]

  type DerivedStyleProp[V] = keys.DerivedStyleProp[V]

  //
  // Specific HTML elements
  //

  type Anchor = nodes.ReactiveHtmlElement[dom.html.Anchor]

  type Button = nodes.ReactiveHtmlElement[dom.html.Button]

  type Div = nodes.ReactiveHtmlElement[dom.html.Div]

  type IFrame = nodes.ReactiveHtmlElement[dom.html.IFrame]

  type Image = nodes.ReactiveHtmlElement[dom.html.Image]

  type Input = nodes.ReactiveHtmlElement[dom.html.Input]

  type FormElement = nodes.ReactiveHtmlElement[dom.html.Form]

  type Label = nodes.ReactiveHtmlElement[dom.html.Label]

  type LI = nodes.ReactiveHtmlElement[dom.html.LI]

  type Select = nodes.ReactiveHtmlElement[dom.html.Select]

  type Span = nodes.ReactiveHtmlElement[dom.html.Span]

  type TextArea = nodes.ReactiveHtmlElement[dom.html.TextArea]

  //

  @inline def DomApi: domapi.DomApi.type = domapi.DomApi
}
