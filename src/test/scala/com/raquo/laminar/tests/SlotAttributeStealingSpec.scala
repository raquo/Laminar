package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.CollectionCommand
import com.raquo.laminar.inserters.CollectionCommand.{Append, ReplaceAll}
import com.raquo.laminar.nodes.Slot
import com.raquo.laminar.utils.UnitSpec

/** The `slot` attribute of an element can be written by two parties: a `Slot(name)` wrapper
  * (Laminar-applied), and the user directly (`slot := ...` / `slot <-- ...`). Ownership is
  * last-write-wins, like the element-stealing in [[com.raquo.laminar.SlotStealingSpec]]:
  * whoever wrote the `slot` attribute most recently wins, and neither party permanently
  * "locks" the other out. These tests pin that interplay for both the static (`:=`) and the
  * reactive (`<--`) manual-write paths.
  */
class SlotAttributeStealingSpec extends UnitSpec {

  // -- User steals from Slot, Slot steals back on move (static `slot :=`) --

  it("slot := and a destination Slot ping-pong ownership of the slot attribute (last write wins)") {
    // A manual `slot :=` overrides the slot a `Slot()` applied; moving into another `Slot()`
    // re-applies that destination's slot; a further `slot :=` overrides again – all seamlessly.
    val tracker = createEventTracker()
    val e = tracker.createSpan("E")
    tracker.clear()
    val sourceItems = Var(List(e))
    val targetItems = Var(List.empty[HtmlElement])
    val sourceHost = div(new Slot("prefix")(children <-- sourceItems.signal))
    val targetHost = div(new Slot("suffix")(children <-- targetItems.signal))

    mount(div(sourceHost, targetHost))

    withClue("the source Slot applies its slot on mount:") {
      tracker.assertEvents(_.mounted("E")).clear()
      e.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("a manual slot := overrides the Slot-applied slot:") {
      e.amend(slot := "manual")
      tracker.assertNoEvents.clear()
      e.ref.getAttribute("slot") shouldBe "manual"
    }

    withClue("moving into another Slot re-applies that destination's slot (no re-mount):") {
      targetItems.set(List(e))
      sourceItems.set(Nil)
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe targetHost.ref
      e.ref.getAttribute("slot") shouldBe "suffix"
    }

    withClue("a later manual slot := overrides the destination Slot again:") {
      e.amend(slot := "manual2")
      tracker.assertNoEvents.clear()
      e.ref.getAttribute("slot") shouldBe "manual2"
    }

    withClue("moving back to the first Slot re-applies its slot once more:") {
      sourceItems.set(List(e))
      targetItems.set(Nil)
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe sourceHost.ref
      e.ref.getAttribute("slot") shouldBe "prefix"
    }
  }

  // -- User steals from Slot via a live `slot <--`, across a Slot move --

  it("slot <-- overrides a Slot and keeps overriding after the element moves between Slots") {
    // The reactive `slot <--` binding stays live across a move into another Slot: emitting before
    // the move wins, the move re-applies the new Slot's slot, and a later emission wins again –
    // proving the binding's later writes still "steal" the slot back from the Slot.
    val tracker = createEventTracker()
    val slotBus = new EventBus[String]
    val e = tracker.createSpan("E", slot <-- slotBus.events)
    tracker.clear()
    val sourceItems = Var(List(e))
    val targetItems = Var(List.empty[HtmlElement])
    val sourceHost = div(new Slot("prefix")(children <-- sourceItems.signal))
    val targetHost = div(new Slot("suffix")(children <-- targetItems.signal))

    mount(div(sourceHost, targetHost))

    withClue("the Slot applies its slot; the un-emitted binding writes nothing:") {
      tracker.assertEvents(_.mounted("E")).clear()
      e.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("emitting into slot <-- overrides the Slot-applied slot:") {
      slotBus.emit("manual")
      tracker.assertNoEvents.clear()
      e.ref.getAttribute("slot") shouldBe "manual"
    }

    withClue("moving into another Slot re-applies that destination's slot (no re-mount):") {
      targetItems.set(List(e))
      sourceItems.set(Nil)
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe targetHost.ref
      e.ref.getAttribute("slot") shouldBe "suffix"
    }

    withClue("a later emission overrides the destination Slot again:") {
      slotBus.emit("manual2")
      tracker.assertNoEvents.clear()
      e.ref.getAttribute("slot") shouldBe "manual2"
    }
  }

  // -- A Slot reconcile re-asserts its slot after a manual override (last write wins) --

  it("a slotted-list reconcile re-asserts the Slot's slot over a manual override") {
    // A manual `slot :=` on an element inside a live `Slot(...)(children <-- ...)` is provisional:
    // the next reconcile that touches the element (here, a reorder) is a fresh Slot write, so it
    // wins under last-write-wins and restores the Slot's slot – without re-mounting.
    val tracker = createEventTracker()
    val e = tracker.createSpan("E")
    val f = tracker.createSpan("F")
    tracker.clear()
    val items = Var(List(e, f))
    val host = div(new Slot("prefix")(children <-- items.signal))

    mount(div(host))

    withClue("both elements get the Slot's slot on mount:") {
      tracker.assertEvents(_.mounted("E"), _.mounted("F")).clear()
      e.ref.getAttribute("slot") shouldBe "prefix"
      f.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("a manual slot := overrides just that element:") {
      e.amend(slot := "manual")
      tracker.assertNoEvents.clear()
      e.ref.getAttribute("slot") shouldBe "manual"
      f.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("reordering the list re-asserts the Slot's slot on the moved element:") {
      items.set(List(f, e))
      tracker.assertNoEvents.clear()
      e.ref.getAttribute("slot") shouldBe "prefix"
      f.ref.getAttribute("slot") shouldBe "prefix"
    }
  }

  List(true, false).foreach { minimizeDiff =>

    it(s"a slotted `children.command` ReplaceAll(minimizeDiff = $minimizeDiff) re-asserts the Slot's slot over a manual override, even on a node that stays in place") {
      // Same as the reorder above: ReplaceAll is a fresh Slot write for every node it's given,
      // regardless of whether the node needs to move, and whether it's kept or re-inserted.
      val tracker = createEventTracker()
      val e = tracker.createSpan("E")
      val f = tracker.createSpan("F")
      tracker.clear()
      val commandBus = new EventBus[CollectionCommand[HtmlElement]]
      val host = div(new Slot("prefix")(children.command <-- commandBus.events))

      withClue("both elements get the Slot's slot on mount:") {
        mount(div(host))
        commandBus.emit(Append(e))
        commandBus.emit(Append(f))
        tracker
          .assertEvents(
            _.mounted("E"),
            _.mounted("F")
          )
          .clear()
        e.ref.getAttribute("slot") shouldBe "prefix"
        f.ref.getAttribute("slot") shouldBe "prefix"
      }

      withClue("a manual slot := overrides just that element:") {
        e.amend(slot := "manual")
        tracker.assertNoEvents.clear()
        e.ref.getAttribute("slot") shouldBe "manual"
        f.ref.getAttribute("slot") shouldBe "prefix"
      }

      withClue("ReplaceAll(e, f) re-asserts the Slot's slot on e, which stays in place:") {
        commandBus.emit(ReplaceAll(e :: f :: Nil, minimizeDiff))
        if (minimizeDiff) {
          tracker.assertNoEvents.clear()
        } else {
          tracker
            .assertEvents(
              _.unmounted("E"),
              _.unmounted("F"),
              _.mounted("E"),
              _.mounted("F")
            )
            .clear()
        }
        e.ref.getAttribute("slot") shouldBe "prefix"
        f.ref.getAttribute("slot") shouldBe "prefix"
      }
    }
  }

  // -- A `slot <--` with an initial value, mounted into a Slot --

  it("a slot <-- signal with an initial value wins over the Slot on mount") {
    // Common usage: `slot <-- someSignal` whose signal already has a value. The binding's
    // initial write runs after the Slot applies its slot on mount, so the bound value wins
    // (last write wins). Subsequent emissions keep winning, and forgetAppliedSlotName still
    // lets a later Slot move re-apply.
    val tracker = createEventTracker()
    val slotVar = Var("bound")
    val e = tracker.createSpan("E", slot <-- slotVar.signal)
    tracker.clear()
    val sourceItems = Var(List(e))
    val targetItems = Var(List.empty[HtmlElement])
    val sourceHost = div(new Slot("prefix")(children <-- sourceItems.signal))
    val targetHost = div(new Slot("suffix")(children <-- targetItems.signal))

    mount(div(sourceHost, targetHost))

    withClue("the bound initial value wins over the Slot's slot on mount:") {
      tracker.assertEvents(_.mounted("E")).clear()
      e.ref.getAttribute("slot") shouldBe "bound"
    }

    withClue("emitting a new value keeps the binding in control:") {
      slotVar.set("bound2")
      tracker.assertNoEvents.clear()
      e.ref.getAttribute("slot") shouldBe "bound2"
    }

    withClue("moving into another Slot re-applies that destination's slot:") {
      targetItems.set(List(e))
      sourceItems.set(Nil)
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe targetHost.ref
      e.ref.getAttribute("slot") shouldBe "suffix"
    }

    withClue("a later emission overrides the destination Slot again:") {
      slotVar.set("bound3")
      tracker.assertNoEvents.clear()
      e.ref.getAttribute("slot") shouldBe "bound3"
    }
  }
}
