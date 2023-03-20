package com.raquo.laminar.example

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.util.Random

class ToggleComponentSpec extends UnitSpec {

  class Toggle private(
    val checkedStream: EventStream[Boolean],
    val node: HtmlElement
  )

  object Toggle {
    // @TODO how do we make this a controlled component?
    def apply(caption: String = "TOGGLE MEEEE"): Toggle = {
      val clickBus = new EventBus[dom.MouseEvent]
      val checkedStream = clickBus.events.map(ev => ev.target.asInstanceOf[dom.HTMLInputElement].checked) //.debug("checked")

      // This will only be evaluated once
      val rand = Random.nextInt(99)

      val checkbox = input.apply(
        idAttr := "toggle" + rand,
        className := "red",
        `type` := "checkbox",
        onClick --> clickBus.writer
      )

      val captionNodeStream = checkedStream.map(checked => span(if (checked) "ON" else "off"))

      val node = div(
        className := "Toggle",
        checkbox,
        label(forId := "toggle" + rand, caption),
        " –" + nbsp,
        child <-- captionNodeStream
      )

      new Toggle(checkedStream, node)
    }
  }


  it("renders") {
    val caption = "Caption_" + Random.nextString(5)

    val toggle = Toggle(caption)

    mount(toggle.node)

    root.child.ref.getAttribute("class") shouldBe "Toggle"

    val input = root.child.ref.querySelector("input").asInstanceOf[dom.html.Input]
    val label = root.child.ref.querySelector("label").asInstanceOf[dom.html.Label]

    label.textContent shouldBe caption
    input.checked shouldBe false

    input.click()
    input.checked shouldBe true

    input.click()
    input.checked shouldBe false

    label.click()
    input.checked shouldBe true

    label.click()
    input.checked shouldBe false
  }
}
