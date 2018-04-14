package com.raquo.laminar.example.components

import com.raquo.airstream.eventbus.EventBus
import com.raquo.airstream.eventstream.EventStream
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom
import org.scalajs.dom.raw.{HTMLInputElement, MouseEvent}

import scala.util.Random

class Toggle private (
  val $checked: EventStream[Boolean],
  val node: ReactiveElement[dom.html.Div]
)

object Toggle {
  // @TODO how do we make this a controlled component?
  def apply(caption: String = "TOGGLE MEEEE"): Toggle = {
    val clickBus = new EventBus[MouseEvent]
    val $checked = clickBus.events.map(ev => ev.target.asInstanceOf[HTMLInputElement].checked)//.debug("$checked")

    // This will only be evaluated once
    val rand = Random.nextInt(99)

    val checkbox = input.apply(
      id := "toggle" + rand,
      className := "red",
      `type` := "checkbox",
      onClick --> clickBus.writer
    )

    val $captionNode = $checked.map(checked => span(if (checked) "ON" else "off"))

    val node = div(
      className := "Toggle",
      checkbox,
      label(forId := "toggle" + rand, caption),
      " — ",
      child <-- $captionNode
    )

    new Toggle($checked, node)
  }
}
