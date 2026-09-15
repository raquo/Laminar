package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.nodes.Slot
import com.raquo.laminar.utils.UnitSpec

class NestedInserterHooksRegressionSpec extends UnitSpec {

  it("preserves a nested inserter's explicit slot on every emission") {
    val tracker = createEventTracker()
    val bus = EventBus[HtmlElement]()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val item: Inserter = Slot("prefix")(child <-- bus.events).head

    mount(div(children <-- Val(List(item))))

    withClue("first emission retains the item's slot hook:") {
      bus.emit(a)
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(sentinel, sentinel, span.of("A", slot is "prefix"), sentinel, sentinel))
    }

    withClue("replacement retains the item's slot hook:") {
      bus.emit(b)
      tracker.assertEvents(_.unmounted("A"), _.mounted("B")).clear()
      b.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(sentinel, sentinel, span.of("B", slot is "prefix"), sentinel, sentinel))
    }
  }

  it("applies the destination slot when an existing nested inserter moves into it") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    val item: Inserter = child <-- Val(a)
    val leftItems = Var(List(item))
    val rightItems = Var(List.empty[Inserter])
    val left = div(children <-- leftItems.signal)
    val right = div(Slot("prefix")(children <-- rightItems.signal))

    mount(div(left, right))
    tracker.assertEvents(_.mounted("A")).clear()

    withClue("move into the slot without remounting:") {
      rightItems.set(List(item))
      leftItems.set(Nil)
      tracker.assertNoEvents.clear()
      a.ref.parentNode shouldBe right.ref
      a.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(
        div.of(sentinel, sentinel),
        div.of(sentinel, sentinel, span.of("A", slot is "prefix"), sentinel, sentinel)
      ))
    }
  }

  it("applies the destination slot to emissions after moving an empty nested inserter") {
    val tracker = createEventTracker()
    val bus = EventBus[HtmlElement]()
    val a = tracker.createSpan("A")
    tracker.clear()
    val item: Inserter = child <-- bus.events
    val leftItems = Var(List(item))
    val rightItems = Var(List.empty[Inserter])
    val left = div(children <-- leftItems.signal)
    val right = div(Slot("prefix")(children <-- rightItems.signal))

    mount(div(left, right))

    withClue("move the empty inserter into the slot:") {
      rightItems.set(List(item))
      leftItems.set(Nil)
      tracker.assertNoEvents.clear()
      expectNode(div.of(
        div.of(sentinel, sentinel),
        div.of(sentinel, sentinel, sentinel, sentinel)
      ))
    }

    withClue("future emissions use the destination slot:") {
      bus.emit(a)
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.parentNode shouldBe right.ref
      a.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(
        div.of(sentinel, sentinel),
        div.of(sentinel, sentinel, span.of("A", slot is "prefix"), sentinel, sentinel)
      ))
    }
  }

  it("preserves a static inserter's explicit slot when nested in children <--") {
    // Same latent bug as the first test, but for a STATIC single-node inserter.
    // `HookableChildInserter.addToDynamicList` inserts its node using the `hooks`
    // ARGUMENT passed by the enclosing `children <--` (here: none), discarding the
    // inserter's OWN `hooks` field (the Slot attribute hook). So the item's slot is
    // lost on placement, even though rendering the same item directly
    // (`div(Slot("prefix")(a))`) applies it correctly.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val itemA: Inserter = Slot("prefix")(a).head
    val itemB: Inserter = Slot("prefix")(b).head
    val items = Var(List.empty[Inserter])

    mount(div(children <-- items.signal))

    withClue("first placement retains the item's slot hook:") {
      items.set(List(itemA))
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(sentinel, span.of("A", slot is "prefix"), sentinel))
    }

    withClue("a newly added item also retains its slot hook:") {
      items.set(List(itemA, itemB))
      tracker.assertEvents(_.mounted("B")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      b.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(
        sentinel,
        span.of("A", slot is "prefix"),
        span.of("B", slot is "prefix"),
        sentinel
      ))
    }
  }
}
