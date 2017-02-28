package com.raquo.laminar.example.components

import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom.raw.HTMLInputElement

import scala.util.Random

class ToggleSpec extends UnitSpec {

  it("renders") {
    val caption = "Caption_" + Random.nextString(5)

    val toggle = Toggle(caption)

    mount(toggle.vnode)

    mountedElement().getAttribute("class") shouldBe "Toggle"

    val input = mountedElement().querySelector("input").asInstanceOf[HTMLInputElement]
    val label = mountedElement().querySelector("label")

    label.textContent shouldBe caption
    input.checked shouldBe false

    simulateClick(input)
    input.checked shouldBe true

    simulateClick(input)
    input.checked shouldBe false

    simulateClick(label)
    input.checked shouldBe true

    simulateClick(label)
    input.checked shouldBe false
  }
}
