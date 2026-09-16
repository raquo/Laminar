package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.nodes.Slot
import com.raquo.laminar.utils.UnitSpec

/** Slot reconciliation on RETAINED content – nodes that stay in place (no re-mount)
  * while their target slot changes. Covers both the `children <--` list path (where an
  * item already at the right position must still update its slot) and the single-child
  * `child <--` path.
  */
class SlotRetainedContentSpec extends UnitSpec {

  it("updates a retained element when its Slot wrapper changes") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    val prefix: Inserter = new Slot("prefix")(a).head
    val suffix: Inserter = new Slot("suffix")(a).head
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

  it("reconciles the slot when a retained item switches between a bare element and a Slot wrapper") {
    // A `children <--` item keyed by the same node ref can switch between a bare element
    // (no slot) and a `SlottableChildInserter` (Slot wrapper). Both share `stableFirstNode`
    // = the node's ref, so the diff retains it in place; the in-place branch must still
    // reconcile the slot – add it when a wrapper appears, clear it when it goes away –
    // without re-mounting the node.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    val bare: Inserter = a
    val slotted: Inserter = new Slot("prefix")(a).head
    val items = Var[List[Inserter]](List(bare))

    withClue("Initial: bare element has no slot: ") {
      mount(div(children <-- items.signal))
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe null
      expectNode(div.of(sentinel, span.of("A"), sentinel))
    }

    withClue("Switching to the Slot wrapper adds the slot without remounting: ") {
      items.set(List(slotted))
      tracker.assertNoEvents.clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(sentinel, span.of("A", slot is "prefix"), sentinel))
    }

    withClue("Switching back to the bare element clears the slot without remounting: ") {
      items.set(List(bare))
      tracker.assertNoEvents.clear()
      a.ref.getAttribute("slot") shouldBe null
      expectNode(div.of(sentinel, span.of("A"), sentinel))
    }
  }

  it("reconciles the slot when a switching item also moves position") {
    // Same bare<->Slot switch as above, but the item also changes position, so it goes
    // through the diff's MOVE branch (`moveWithinDynamicList`) instead of the in-place
    // branch. The move must re-affirm the correct slot without re-mounting the node.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val bareA: Inserter = a
    val slottedA: Inserter = new Slot("prefix")(a).head
    val items = Var[List[Inserter]](List(bareA, b))

    withClue("Initial: bare A before B, no slots: ") {
      mount(div(children <-- items.signal))
      tracker.assertEvents(_.mounted("A"), _.mounted("B")).clear()
      a.ref.getAttribute("slot") shouldBe null
      expectNode(div.of(sentinel, span.of("A"), span.of("B"), sentinel))
    }

    withClue("A moves after B and gains a slot, both retained: ") {
      items.set(List(b, slottedA))
      tracker.assertNoEvents.clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(sentinel, span.of("B"), span.of("A", slot is "prefix"), sentinel))
    }

    withClue("A moves back before B and loses its slot, both retained: ") {
      items.set(List(bareA, b))
      tracker.assertNoEvents.clear()
      a.ref.getAttribute("slot") shouldBe null
      expectNode(div.of(sentinel, span.of("A"), span.of("B"), sentinel))
    }
  }

  it("falls back to the list's slot (not null) when a switching item drops its own Slot") {
    // When the `children <--` list is itself slotted, a bare item takes the LIST's slot,
    // a wrapped item's OWN slot wins, and reverting to bare must fall back to the list's
    // slot – not clear the attribute entirely.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    val bareA: Inserter = a
    val slottedA: Inserter = new Slot("own")(a).head
    val items = Var[List[Inserter]](List(bareA))

    withClue("Bare item takes the list's slot: ") {
      mount(div(new Slot("list")(children <-- items.signal)))
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe "list"
    }

    withClue("The item's own slot wins over the list's: ") {
      items.set(List(slottedA))
      tracker.assertNoEvents.clear()
      a.ref.getAttribute("slot") shouldBe "own"
    }

    withClue("Reverting to bare falls back to the list's slot: ") {
      items.set(List(bareA))
      tracker.assertNoEvents.clear()
      a.ref.getAttribute("slot") shouldBe "list"
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
        new Slot("prefix")(inserter).head
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

  it("updates a retained node Seq when its Slot wrapper changes") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val nodes = List(a, b)
    val prefix: Inserter = new Slot("prefix")(nodes).head
    val suffix: Inserter = new Slot("suffix")(nodes).head
    val items = Var(List(prefix))

    withClue("Initial slot: ") {
      mount(div(children <-- items.signal))
      tracker.assertEvents(_.mounted("A"), _.mounted("B")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      b.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(
        sentinel,
        span.of("A", slot is "prefix"),
        span.of("B", slot is "prefix"),
        sentinel
      ))
    }

    withClue("Change the wrapper without remounting the nodes: ") {
      items.set(List(suffix))
      tracker.assertNoEvents.clear()
      a.ref.getAttribute("slot") shouldBe "suffix"
      b.ref.getAttribute("slot") shouldBe "suffix"
      expectNode(div.of(
        sentinel,
        span.of("A", slot is "suffix"),
        span.of("B", slot is "suffix"),
        sentinel
      ))
    }
  }

  it("keeps a retained nested inserter's slot while sibling items are added and removed") {
    // The retained-in-place branch re-affirms `applySlot` on every reconcile. A nested
    // `child <--` item that stays put while other items come and go must keep its slot,
    // and its content must not re-mount.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val nested: Inserter = child <-- Val(a)
    val sibling: Inserter = child <-- Val(b)
    val items = Var[List[Inserter]](List(nested))

    mount(div(new Slot("prefix")(children <-- items.signal)))

    withClue("the nested item gets the list slot: ") {
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("adding a sibling leaves the retained item's slot and mount state untouched: ") {
      items.set(List(nested, sibling))
      tracker.assertEvents(_.mounted("B")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      b.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("removing the sibling again leaves the retained item untouched: ") {
      items.set(List(nested))
      tracker.assertEvents(_.unmounted("B")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
    }
  }

  // -- Single-child (`child <--`) path --

  it("reconciles the slot when onMountInsert remounts a retained `child <--`") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    var maybeSlot: Option[Slot] = Some(new Slot("prefix"))
    val host = div(onMountInsert { _ =>
      val inserter = child <-- Val(a)
      maybeSlot.fold(inserter)(_(inserter).head)
    })

    withClue("Initial slot: ") {
      mount(host)
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(sentinel, span.of("A", slot is "prefix")))
    }

    withClue("Remount without a Slot clears the slot: ") {
      unmount()
      tracker.assertEvents(_.unmounted("A")).clear()
      maybeSlot = None
      mount(host)
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe null
      expectNode(div.of(sentinel, span.of("A")))
    }

    withClue("Remount into a different Slot updates the slot: ") {
      unmount()
      tracker.assertEvents(_.unmounted("A")).clear()
      maybeSlot = Some(new Slot("suffix"))
      mount(host)
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe "suffix"
      expectNode(div.of(sentinel, span.of("A", slot is "suffix")))
    }
  }

  it("keeps the slot when a `child <--` re-emits the same retained node") {
    val tracker = createEventTracker()
    val bus = EventBus[HtmlElement]()
    val a = tracker.createSpan("A")
    tracker.clear()
    mount(div(new Slot("prefix")(child <-- bus.events).head))

    withClue("Initial slot: ") {
      bus.emit(a)
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(sentinel, span.of("A", slot is "prefix")))
    }

    withClue("Re-emitting the same node keeps the slot without remounting: ") {
      bus.emit(a)
      tracker.assertNoEvents.clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(sentinel, span.of("A", slot is "prefix")))
    }
  }
}
