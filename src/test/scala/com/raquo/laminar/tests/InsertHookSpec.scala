package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.ButtonElement
import com.raquo.laminar.utils.UnitSpec

class InsertHookSpec extends UnitSpec {

  it("Slot syntax compilation") {

    // #TODO[Test]: The tests below only assert that the `slot` attribute is set. To also test
    //  actual slot *projection* we'd need HTML custom elements (e.g. a <template> with slots).

    val staticElements = List(div("list-el1"), div("list-el2"))

    val dynamicElements = List(div("children <-- val (el1)"), div("children <-- val (el2)"))

    // Mark as used (it's used in compilable code strings below)
    val _ = ButtonElement

    // Assert compiles
    div(
      ButtonElement.of(
        _.slots.prefix(
          div("hello"),
          span("foo"),
          staticElements,
          child <-- Val(span("child <-- val")),
          child.maybe <-- Val(Some(span("child.maybe <-- val"))),
          text <-- Val("text <-- val"),
          children <-- Val(dynamicElements)
        )
      )
    )

    // Assert compiles
    ButtonElement.of(_.slots.prefix(span("wrapped text")))

    // Assert compiles
    ButtonElement.of(_.slots.prefix(child <-- Val(span("child inserter"))))

    // Assert compiles
    ButtonElement.of(_.slots.prefix(children <-- Val(span("children inserter") :: Nil)))

    assertTypeError(
      """
      ButtonElement.of(_.slots.prefix("text can not be slotted"))
      """
    )

  }

  it("sets the slot attribute on a static element and a static group") {
    val single = span("single")
    val groupA = span("A")
    val groupB = span("B")

    mount(div(
      ButtonElement.of(
        _.slots.prefix(
          single,
          List(groupA, groupB)
        )
      )
    ))

    single.ref.getAttribute("slot") shouldBe "prefix"
    groupA.ref.getAttribute("slot") shouldBe "prefix"
    groupB.ref.getAttribute("slot") shouldBe "prefix"
  }

  it("sets the slot attribute on `child <--` content, and re-applies it on every emission") {
    val bus = new EventBus[HtmlElement]
    val el1 = span("one")
    val el2 = span("two")

    mount(div(
      ButtonElement.of(_.slots.prefix(child <-- bus.events))
    ))

    withClue("Slot attribute is set on the first emitted element:") {
      bus.writer.onNext(el1)
      el1.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("A new emission gets the slot attribute too (hook runs on replace):") {
      bus.writer.onNext(el2)
      el2.ref.getAttribute("slot") shouldBe "prefix"
    }
  }

  it("sets the slot attribute on every `children <--` element, and keeps it across reorders") {
    val bus = new EventBus[List[HtmlElement]]
    val a = span("A")
    val b = span("B")
    val c = span("C")

    mount(div(
      ButtonElement.of(_.slots.prefix(children <-- bus.events))
    ))

    withClue("Every element in the list gets the slot attribute:") {
      bus.writer.onNext(List(a, b, c))
      a.ref.getAttribute("slot") shouldBe "prefix"
      b.ref.getAttribute("slot") shouldBe "prefix"
      c.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("A reorder is a move (no hooks re-run), but the attribute set on insert stays put:") {
      bus.writer.onNext(List(c, a, b))
      a.ref.getAttribute("slot") shouldBe "prefix"
      b.ref.getAttribute("slot") shouldBe "prefix"
      c.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("A newly-added element (after the list already existed) also gets the attribute:") {
      val d = span("D")
      bus.writer.onNext(List(c, a, b, d))
      d.ref.getAttribute("slot") shouldBe "prefix"
    }
  }

}
