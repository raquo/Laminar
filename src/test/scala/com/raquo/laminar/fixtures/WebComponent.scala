package com.raquo.laminar.fixtures

import com.raquo.laminar.keys.EventProcessor
import com.raquo.laminar.modifiers.Modifier
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.tags.CustomHtmlTag
import org.scalajs.dom

abstract class WebComponent(tagName: String) {

  type Ref <: dom.HTMLElement

  type Element = ReactiveHtmlElement[Ref]

  type ModFunction = this.type => Modifier[Element]

  protected lazy val tag: CustomHtmlTag[Ref] = new CustomHtmlTag(tagName)

  def on[Ev <: dom.Event, V](ev: EventProcessor[Ev, V]): EventProcessor[Ev, V] = ev

  def of(mods: ModFunction*): Element = {
    val el = tag()
    mods.foreach(_(this)(el))
    el
  }
}
