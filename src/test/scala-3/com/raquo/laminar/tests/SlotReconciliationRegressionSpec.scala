package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.nodes.Slot
import com.raquo.laminar.utils.UnitSpec

final class SlotReconciliationRegressionSpec extends UnitSpec {

  it("updates a retained slot while removing its preceding sibling") {
    val tracker = createEventTracker()
    val before = tracker.createSpan("before")
    val kept = tracker.createSpan("kept")
    tracker.clear()
    val prefix: Inserter = Slot("prefix")(kept).head
    val suffix: Inserter = Slot("suffix")(kept).head
    val items = Var(List[Inserter](before, prefix))

    mount(div(children <-- items.signal))
    tracker.assertEvents(_.mounted("before"), _.mounted("kept")).clear()

    withClue("Removing the predecessor must still update the retained element's slot: ") {
      items.set(List(suffix))
      tracker.assertEvents(_.unmounted("before")).clear()
      kept.ref.getAttribute("slot") shouldBe "suffix"
      expectNode(div.of(sentinel, span.of("kept", slot is "suffix"), sentinel))
    }
  }

  it("re-emitting a static group steals its child back from another parent (last write wins)") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val group: Inserter = Slot("prefix")(List(a, b)).head
    val groups = Var(List(group))
    val stolen = Var(List.empty[HtmlElement])
    val sourceHost = div(children <-- groups.signal)
    val targetHost = div(children <-- stolen.signal)

    mount(div(sourceHost, targetHost))
    tracker.assertEvents(_.mounted("A"), _.mounted("B")).clear()

    withClue("The destination steals B without remounting it: ") {
      stolen.set(List(b))
      tracker.assertNoEvents.clear()
      b.ref.parentNode shouldBe targetHost.ref
      b.ref.getAttribute("slot") shouldBe null
    }

    withClue("Re-emitting the group re-adopts B (its content) and re-slots it, no remount: ") {
      groups.set(List(group))
      tracker.assertNoEvents.clear()
      b.ref.parentNode shouldBe sourceHost.ref
      b.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(
        div.of(sentinel, span.of("A", slot is "prefix"), span.of("B", slot is "prefix"), sentinel),
        div.of(sentinel, sentinel)
      ))
    }
  }

  it("a destination Slot overrides a later manual slot assignment") {
    val tracker = createEventTracker()
    val e = tracker.createSpan("E")
    tracker.clear()
    val sourceItems = Var(List(e))
    val targetItems = Var(List.empty[HtmlElement])
    val sourceHost = div(Slot("prefix")(children <-- sourceItems.signal))
    val targetHost = div(Slot("prefix")(children <-- targetItems.signal))

    mount(div(sourceHost, targetHost))
    tracker.assertEvents(_.mounted("E")).clear()
    e.amend(slot := "manual")
    e.ref.getAttribute("slot") shouldBe "manual"

    withClue("Moving into a Slot must apply that destination's slot: ") {
      targetItems.set(List(e))
      sourceItems.set(Nil)
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe targetHost.ref
      e.ref.getAttribute("slot") shouldBe "prefix"
    }
  }
}
