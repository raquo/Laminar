package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.ButtonElement
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.nodes.Slot
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

  // -- Slot attribute across moves --

  // A slot is a property of the position an element occupies, not of the element itself.
  // So relocating an element must reconcile its `slot` attribute to match wherever it lands:
  // set when moving into a slot, replaced when moving between slots, and cleared when moving
  // out into a plain (non-slot) position – all without re-mounting the moved element.

  it("clears the slot attribute when a nested inserter moves out of a slot into a plain list") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    val item: Inserter = child <-- Val(a)
    val plainItems = Var(List.empty[Inserter])
    val slotItems = Var(List(item))
    val plain = div(children <-- plainItems.signal)
    val slotted = div(new Slot("prefix")(children <-- slotItems.signal))

    mount(div(plain, slotted))

    withClue("the element enters the slot with the destination's slot attribute:") {
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(
        div.of(sentinel, sentinel),
        div.of(sentinel, sentinel, span.of("A", slot is "prefix"), sentinel, sentinel)
      ))
    }

    withClue("moving out into a plain list clears the slot without re-mounting:") {
      plainItems.set(List(item))
      slotItems.set(Nil)
      tracker.assertNoEvents.clear()
      a.ref.parentNode shouldBe plain.ref
      a.ref.getAttribute("slot") shouldBe null
      expectNode(div.of(
        div.of(sentinel, sentinel, span.of("A"), sentinel, sentinel),
        div.of(sentinel, sentinel)
      ))
    }
  }

  it("replaces the slot attribute when a nested inserter moves between two different slots") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    val item: Inserter = child <-- Val(a)
    val prefixItems = Var(List(item))
    val suffixItems = Var(List.empty[Inserter])
    val prefixHost = div(new Slot("prefix")(children <-- prefixItems.signal))
    val suffixHost = div(new Slot("suffix")(children <-- suffixItems.signal))

    mount(div(prefixHost, suffixHost))

    withClue("the element starts in the prefix slot:") {
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("moving into the suffix slot replaces the attribute without re-mounting:") {
      suffixItems.set(List(item))
      prefixItems.set(Nil)
      tracker.assertNoEvents.clear()
      a.ref.parentNode shouldBe suffixHost.ref
      a.ref.getAttribute("slot") shouldBe "suffix"
      expectNode(div.of(
        div.of(sentinel, sentinel),
        div.of(sentinel, sentinel, span.of("A", slot is "suffix"), sentinel, sentinel)
      ))
    }
  }

  it("a Slot wrapper overrides a manual slot attribute, and moving out clears it entirely") {
    // Contract: a `Slot(name)` wrapper OWNS the slot attribute of what it wraps. A manual
    // `slot := ...` set by the user is overridden while wrapped, and is NOT restored when the
    // element later leaves the slot – leaving a slot always clears the attribute outright.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A", slot := "manual")
    tracker.clear()
    val item: Inserter = child <-- Val(a)
    val plainItems = Var(List.empty[Inserter])
    val slotItems = Var(List(item))
    val plain = div(children <-- plainItems.signal)
    val slotted = div(new Slot("prefix")(children <-- slotItems.signal))

    mount(div(plain, slotted))

    withClue("the wrapper's slot overrides the element's manual slot on insert:") {
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("moving out clears the attribute entirely (manual value is not restored):") {
      plainItems.set(List(item))
      slotItems.set(Nil)
      tracker.assertNoEvents.clear()
      a.ref.getAttribute("slot") shouldBe null
    }
  }

  it("leaves a manual slot attribute untouched on an element that never entered a Slot") {
    // The reconcile must only clear slots that Laminar itself applied, so an element that was
    // never wrapped in a `Slot` keeps its own `slot := ...` no matter how it moves around.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A", slot := "manual")
    tracker.clear()
    val item: Inserter = child <-- Val(a)
    val leftItems = Var(List(item))
    val rightItems = Var(List.empty[Inserter])
    val left = div(children <-- leftItems.signal)
    val right = div(children <-- rightItems.signal)

    mount(div(left, right))

    withClue("the manual slot survives the initial mount into a plain list:") {
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe "manual"
    }

    withClue("moving between two plain lists does not disturb the manual slot:") {
      rightItems.set(List(item))
      leftItems.set(Nil)
      tracker.assertNoEvents.clear()
      a.ref.parentNode shouldBe right.ref
      a.ref.getAttribute("slot") shouldBe "manual"
    }
  }

}
