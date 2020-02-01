package com.raquo.laminar.example.components

import com.raquo.laminar.fixtures.example.components.Toggle
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.util.Random

class ToggleSpec extends UnitSpec {

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
