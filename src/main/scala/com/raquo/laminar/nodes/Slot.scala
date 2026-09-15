package com.raquo.laminar.nodes

import com.raquo.laminar.inserters.{Inserter, Slottable}

/** A [[Slot]] represents a special child component of web components.
  *
  * Many web components reserve `slot` names for some of their children, with a particular meaning.
  *
  * For example, if you specify slot="prefix" when passing an icon to a button web component,
  * the web component will know to put that icon into a "prefix" location in its DOM – whatever
  * that might mean in that web component.
  *
  * Typical usage:
  *
  * {{{
  *   // Define slot in the web component:
  *   object ButtonElement extends WebComponent("sl-button") {
  *     object slots {
  *       val prefix = new Slot("prefix")
  *     }
  *   }
  *
  *   // Create a button with a div element in its prefix slot:
  *   ButtonElement.of(
  *     _.slots.prefix(div("This div goes into the prefix slot"))
  *   )
  * }}}
  *
  */
class Slot(val name: String) {

  def apply[I <: Inserter](children: (I with Slottable[I])*): Seq[I with Slottable[I]] = {
    children.map { inserter =>
      inserter.withSlotName(name)
    }
  }
}
