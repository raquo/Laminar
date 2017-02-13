package com.raquo.laminar.example.components

import com.raquo.laminar._
import com.raquo.snabbdom.VNode
import com.raquo.snabbdom.attrs._
import com.raquo.snabbdom.events._
import com.raquo.snabbdom.tags._
import com.raquo.xstream.XStream
import org.scalajs.dom.raw.{HTMLInputElement, MouseEvent}

import scala.util.Random

class Toggle private (
  val $checked: XStream[Boolean],
  val vnode: VNode
)

object Toggle {
  // @TODO how do we make this a controlled component?
  def apply(caption: String = "TOGGLE MEEEE"): Toggle = {
    val $click = XStream.create[MouseEvent]()
    val $checked = $click.map(ev => ev.target.asInstanceOf[HTMLInputElement].checked)//.debug("$checked")

    val rand = Random.nextInt(99)

    val checkbox = input(
      id := "toggle" + rand,
      cls := "red",
      `type` := "checkbox",
      onClick --> $click
    )

    val $captionNode = $checked.map(checked => span(if (checked) "ON" else "off"))

    val vnode = div(
      cls := "Toggle",
      checkbox,
      label(forId := "toggle" + rand, caption),
      " — ",
      $captionNode
    )

    new Toggle($checked, vnode)
  }
}
