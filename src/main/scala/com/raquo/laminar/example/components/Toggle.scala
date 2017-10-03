package com.raquo.laminar.example.components

import com.raquo.laminar.attrs._
import com.raquo.laminar.child
import com.raquo.laminar.events._
import com.raquo.laminar.implicits._
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.tags._
import com.raquo.xstream.XStream
import org.scalajs.dom
import org.scalajs.dom.raw.{HTMLInputElement, MouseEvent}

import scala.util.Random

class Toggle private (
  val $checked: XStream[Boolean],
  val node: ReactiveElement[dom.html.Div]
)

object Toggle {
  // @TODO how do we make this a controlled component?
  def apply(caption: String = "TOGGLE MEEEE"): Toggle = {
    val $click = XStream.create[MouseEvent]()
    val $checked = $click.map(ev => ev.target.asInstanceOf[HTMLInputElement].checked)//.debug("$checked")

    // This will only be evaluated once
    val rand = Random.nextInt(99)

    val checkbox = input.apply(
      id := "toggle" + rand,
      cls := "red",
      `type` := "checkbox",
      onClick --> $click
    )

    val $captionNode = $checked.map(checked => span(if (checked) "ON" else "off"))

    val node = div(
      cls := "Toggle",
      checkbox,
      label(forId := "toggle" + rand, caption),
      " — ",
      child <-- $captionNode
    )

    new Toggle($checked, node)
  }
}
