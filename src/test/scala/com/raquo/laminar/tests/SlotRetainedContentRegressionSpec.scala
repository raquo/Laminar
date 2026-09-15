package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.nodes.Slot
import com.raquo.laminar.utils.UnitSpec

class SlotRetainedContentRegressionSpec extends UnitSpec {

  it("updates a retained element when its Slot wrapper changes") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    val prefix: Inserter = Slot("prefix")(a).head
    val suffix: Inserter = Slot("suffix")(a).head
    val items = Var(List(prefix))

    withClue("Initial slot: ") {
      mount(div(children <-- items.signal))
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(sentinel, span.of("A", slot is "prefix"), sentinel))
    }

    withClue("Change the wrapper without remounting the element: ") {
      items.set(List(suffix))
      tracker.assertNoEvents.clear()
      a.ref.getAttribute("slot") shouldBe "suffix"
      expectNode(div.of(sentinel, span.of("A", slot is "suffix"), sentinel))
    }
  }

  it("clears the slot when onMountInsert remounts retained children without a Slot") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    var useSlot = true
    val host = div(onMountInsert { _ =>
      val inserter = children <-- Val(List(a))
      if (useSlot) {
        Slot("prefix")(inserter).head
      } else {
        inserter
      }
    })

    withClue("Initial slot: ") {
      mount(host)
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(sentinel, span.of("A", slot is "prefix"), sentinel))
    }

    withClue("Remount the same children without a Slot: ") {
      unmount()
      tracker.assertEvents(_.unmounted("A")).clear()
      useSlot = false
      mount(host)
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe null
      expectNode(div.of(sentinel, span.of("A"), sentinel))
    }
  }
}
