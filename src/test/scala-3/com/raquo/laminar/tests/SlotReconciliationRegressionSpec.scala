package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.nodes.Slot
import com.raquo.laminar.utils.UnitSpec

final class SlotReconciliationRegressionSpec extends UnitSpec {

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
