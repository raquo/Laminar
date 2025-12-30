package com.raquo.laminar.nodes

import com.raquo.airstream.core.AirstreamError
import com.raquo.laminar.inserters.{Hookable, Inserter, InserterHooks}
import org.scalajs.dom

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

  protected val addSlotAttributeHook = new InserterHooks(
    _onWillInsertNode = { (parent, child) =>
      child.ref match {
        case el: dom.Element =>
          el.setAttribute("slot", name)
        case text: dom.Text =>
          AirstreamError.sendUnhandledError(new Exception(
            s"Error: You tried to insert a raw text node `${text.textContent}` into the `${name}` slot of <${parent.ref.tagName.toLowerCase}>.\n" +
              " - Cause: This is not possible: named slots only accept elements. Your node was inserted into the default slot instead.\n" +
              " - Suggestion: Wrap your text node into `span()`"
          ))
        case _ =>
          () // Do nothing with comment nodes
      }
    }
  )

  def apply[I <: Inserter](children: (I with Hookable[I])*): Seq[I] = {
    children.map { inserter =>
      inserter.withHooks(addSlotAttributeHook)
    }
  }
}
