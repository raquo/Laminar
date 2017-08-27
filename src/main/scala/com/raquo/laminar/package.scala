package com.raquo

import com.raquo.dombuilder.generic.modifiers.{StringStyleSetter, StyleSetter}
import com.raquo.dombuilder.jsdom.builders.StyleBuilder
import com.raquo.dombuilder.jsdom.nodes.ChildNode

import com.raquo.domtypes.generic.builders.{BoundedBuilder, SpecializedBuilder}
import com.raquo.domtypes.generic.defs.attrs.{Attrs, GlobalAttrs, InputAttrs}
import com.raquo.domtypes.generic.defs.props.{NodeProps, Props}
import com.raquo.domtypes.generic.defs.styles.{Styles, Styles2}
import com.raquo.domtypes.generic.keys.{Attr, EventProp, Prop}
import com.raquo.domtypes.jsdom.defs.eventProps.{ClipboardEventProps, ErrorEventProps, FormEventProps, KeyboardEventProps, MouseEventProps}
import com.raquo.domtypes.jsdom.defs.tags.{DocumentTags, EmbedTags, FormTags, GroupingTags, MiscTags, SectionTags, TableTags, TextTags}

import com.raquo.laminar.builders.{ReactiveTag, ReactiveTagBuilder}
import com.raquo.laminar.nodes.{ReactiveNode, ReactiveRoot}
import com.raquo.laminar.receivers.{ChildReceiver, ChildrenReceiver, MaybeChildReceiver}

import org.scalajs.dom

package object laminar extends Implicits {

  object tags
    extends DocumentTags[ReactiveTag]
    with GroupingTags[ReactiveTag]
    with TextTags[ReactiveTag]
    with FormTags[ReactiveTag]
    with SectionTags[ReactiveTag]
    with EmbedTags[ReactiveTag]
    with TableTags[ReactiveTag]
    with ReactiveTagBuilder

  object tags2
    extends MiscTags[ReactiveTag]
    with ReactiveTagBuilder

  object attrs
    extends Attrs[Attr]
    with InputAttrs[Attr]
    with GlobalAttrs[Attr]
    with SpecializedBuilder[Attr]
  {
    @inline override def build[V](key: String): Attr[V] = new Attr(key)
  }

  object props
    extends Props[Prop]
    with NodeProps[Prop]
    with SpecializedBuilder[Prop]
  {
    @inline override def build[V](key: String): Prop[V] = new Prop(key)
  }

  object events
    extends MouseEventProps[EventProp]
    with FormEventProps[EventProp]
    with KeyboardEventProps[EventProp]
    with ClipboardEventProps[EventProp]
    with ErrorEventProps[EventProp]
    with BoundedBuilder[EventProp, dom.Event]
  {
    @inline override def build[Ev <: dom.Event](key: String): EventProp[Ev] = new EventProp(name = key)
  }

  object styles
    extends Styles[StyleSetter, StringStyleSetter]
    with StyleBuilder

  object styles2
    extends Styles2[StyleSetter, StringStyleSetter]
    with StyleBuilder

  val child: ChildReceiver.type = ChildReceiver

  val maybeChild: MaybeChildReceiver.type = MaybeChildReceiver

  val children: ChildrenReceiver.type = ChildrenReceiver

  @inline def render(
    container: dom.Element,
    rootNode: ReactiveNode with ChildNode[ReactiveNode, dom.Element]
  ): ReactiveRoot = {
    new ReactiveRoot(container, rootNode)
  }
}
