package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec

class ReactiveEventPropSpec extends UnitSpec {

  it("adds and removes event listeners as instructed") {

    var clickCount1 = 0
    var clickCount2 = 0
    var scrollCount = 0

    def testClick1(): Unit = {
      clickCount1 += 1
    }

    def testClick2(): Unit = {
      clickCount2 += 1
    }

    def testScroll(): Unit = {
      scrollCount += 1
    }

    val clickSetter1 = onClick --> (_ => testClick1())
    val clickSetter2 = onClick --> (_ => testClick2())
    val scrollSetter2 = onScroll --> (_ => testScroll())

    val clickableDiv = div(
      className := "clickable",
      clickSetter1,
      "world"
    )

    mount(
      div(
        clickableDiv,
        div(className := "unrelated", "Something else")
      )
    )

    // @TODO[Test] Split between testing Element methods and EventPropSetter methods?

    clickableDiv.eventListeners shouldBe List(clickSetter1)
    clickCount1 shouldBe 0

    // One event listener added
    clickableDiv.ref.click()
    clickCount1 shouldBe 1
    clickCount2 shouldBe 0

    // Add a new event listener on the same event type ("click")
    clickableDiv.amend(clickSetter2)
    clickableDiv.eventListeners shouldBe List(clickSetter1, clickSetter2)
    clickCount1 shouldBe 1
    clickCount2 shouldBe 0

    clickableDiv.ref.click()
    clickCount1 shouldBe 2
    clickCount2 shouldBe 1

    // Remove that new event listener
    ReactiveElement.removeEventListener(clickableDiv, clickSetter2)
    clickableDiv.eventListeners shouldBe List(clickSetter1)

    clickableDiv.ref.click()
    clickCount1 shouldBe 3
    clickCount2 shouldBe 1

    // Add a duplicate of the original event listener (duplicate should be ignored)
    clickableDiv.amend(clickSetter1)
    clickableDiv.eventListeners shouldBe List(clickSetter1)
    clickableDiv.ref.click()
    clickCount1 shouldBe 4

    // Add a listener to an unrelated event
    clickableDiv.amend(scrollSetter2)
    clickableDiv.eventListeners shouldBe List(clickSetter1, scrollSetter2)

    clickableDiv.ref.click()
    clickCount1 shouldBe 5
    scrollCount shouldBe 0

    simulateScroll(clickableDiv.ref)
    clickCount1 shouldBe 5
    scrollCount shouldBe 1

    // Remove all event listeners
    ReactiveElement.removeEventListener(clickableDiv, clickSetter1)
    clickableDiv.eventListeners shouldBe List(scrollSetter2)

    ReactiveElement.removeEventListener(clickableDiv, scrollSetter2)
    clickableDiv.eventListeners shouldBe Nil

    clickableDiv.ref.click()
    simulateScroll(clickableDiv.ref)
    clickCount1 shouldBe 5
    clickCount2 shouldBe 1
    scrollCount shouldBe 1
  }

  it("handles click events on nested elements") {
    var callbackCount = 0

    def testEvent(): Unit = {
      callbackCount += 1
    }

    val childSpan = span("Hello")
    val childTextNode: TextNode = "world"

    val clickableDiv = div(
      className := "clickable",
      onClick --> (_ => testEvent()),
      childSpan,
      childTextNode
    )

    val unrelatedDiv = div(className := "unrelated", "Something else")

    mount(
      div(
        clickableDiv,
        unrelatedDiv
      )
    )

    callbackCount shouldBe 0

    // Direct hit
    clickableDiv.ref.click()
    callbackCount shouldBe 1

    // Click event should bubble up
    childSpan.ref.click()
    callbackCount shouldBe 2

    // Click event should bubble up even from a text node
    simulateClick(childTextNode.ref)
    callbackCount shouldBe 3

    // Click should not be counted on unrelated div
    unrelatedDiv.ref.click()
    callbackCount shouldBe 3
  }
}
