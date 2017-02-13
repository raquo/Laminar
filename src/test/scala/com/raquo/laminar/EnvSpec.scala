package com.raquo.laminar

import org.scalajs.dom
import org.scalajs.dom.raw.{Element, MouseEvent}

import scala.scalajs.js
import scala.util.Random

/**
  * Sanity checks on the testing environment.
  * This does not use Laminar library at all.
  */
class EnvSpec extends UnitSpec with RenderSpec {

  "Env / DOM" should {
    "render elements with attributes" in {
      val spanId = "spanId_" + Random.nextString(5)
      val span = dom.document.createElement("span")
      span.setAttribute("id", spanId)
      dom.document.body.appendChild(span)

      span.id shouldBe spanId
    }

    "handle click events" in {
      var callbackCount = 0
      def testEvent(ev: MouseEvent): Unit = {
        callbackCount += 1
      }

      val div = dom.document.createElement("div")
      val div2 = dom.document.createElement("div")
      val span = dom.document.createElement("span")

      div.addEventListener[MouseEvent]("click", testEvent _)

      div.appendChild(span)
      dom.document.body.appendChild(div)
      dom.document.body.appendChild(div2)

      // Direct hit
      simulateClick(div)
      callbackCount shouldBe 1

      // Click event should bubble up
      simulateClick(span)
      callbackCount shouldBe 2

      // Click should not be counted on unrelated div
      simulateClick(div2)
      callbackCount shouldBe 2
    }
  }
}
