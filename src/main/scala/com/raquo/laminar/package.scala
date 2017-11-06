package com.raquo

import com.raquo.dombuilder.generic.builders.SetterBuilders
import com.raquo.dombuilder.generic.nodes.Element
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.builders.canonical.{CanonicalAttrBuilder, CanonicalEventPropBuilder, CanonicalPropBuilder, CanonicalReflectedAttrBuilder}
import com.raquo.domtypes.generic.defs.attrs.{AriaAttrs, Attrs}
import com.raquo.domtypes.generic.defs.props.Props
import com.raquo.domtypes.generic.defs.reflectedAttrs.ReflectedAttrs
import com.raquo.domtypes.generic.defs.styles.{Styles, Styles2}
import com.raquo.domtypes.generic.keys.{Attr, EventProp, Prop}
import com.raquo.domtypes.jsdom.defs.eventProps.{ClipboardEventProps, ErrorEventProps, FormEventProps, KeyboardEventProps, MediaEventProps, MiscellaneousEventProps, MouseEventProps, WindowEventProps}
import com.raquo.domtypes.jsdom.defs.tags.{DocumentTags, EmbedTags, FormTags, GroupingTags, MiscTags, SectionTags, TableTags, TextTags}
import com.raquo.laminar.builders.{ReactiveTag, ReactiveTagBuilder}
import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveNode, ReactiveRoot}
import com.raquo.laminar.receivers.{ChildReceiver, ChildrenReceiver, FocusReceiver, MaybeChildReceiver}
import org.scalajs.dom

// @TODO[API,WTF] I can't make laminar extend `Implicits` – test code does not pick up the implicits. Why?

package object laminar {

  type ReflectedAttr[V, DomV] = Attr[V]
  type StyleSetter = Modifier[ReactiveNode with Element[ReactiveNode, dom.Element, dom.Node]]

  object bundle
    // Attrs
    extends Attrs[Attr]
    with AriaAttrs[Attr]
    // Event Props
    with ClipboardEventProps[EventProp]
    with ErrorEventProps[EventProp]
    with FormEventProps[EventProp]
    with KeyboardEventProps[EventProp]
    with MediaEventProps[EventProp]
    with MiscellaneousEventProps[EventProp]
    with MouseEventProps[EventProp]
    with WindowEventProps[EventProp]
    // Props
    with Props[Prop]
    // Reflected Attrs
    with ReflectedAttrs[ReflectedAttr]
    // Styles
    with Styles[StyleSetter]
    with Styles2[StyleSetter]
    // Tags
    with DocumentTags[ReactiveTag]
    with EmbedTags[ReactiveTag]
    with FormTags[ReactiveTag]
    with GroupingTags[ReactiveTag]
    with MiscTags[ReactiveTag]
    with SectionTags[ReactiveTag]
    with TableTags[ReactiveTag]
    with TextTags[ReactiveTag]
    // Builders
    with CanonicalAttrBuilder
    with CanonicalReflectedAttrBuilder
    with CanonicalEventPropBuilder[dom.Event]
    with CanonicalPropBuilder
    with ReactiveTagBuilder
    with SetterBuilders[ReactiveNode, dom.Element, dom.Node]
    // Other things
    with DomApi
    with Implicits {

    val focus: FocusReceiver.type = FocusReceiver

    val child: ChildReceiver.type = ChildReceiver

    val maybeChild: MaybeChildReceiver.type = MaybeChildReceiver

    val children: ChildrenReceiver.type = ChildrenReceiver
  }

  /** Import `implicits._` if you don't want to import `bundle._` */
  object implicits extends Implicits

  @inline def render(
    container: dom.Element,
    rootNode: ReactiveChildNode[dom.Element]
  ): ReactiveRoot = {
    new ReactiveRoot(container, rootNode)
  }
}
