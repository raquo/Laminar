package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.ButtonElement
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.nodes.{ChildNode, Slot, TextNode}
import com.raquo.laminar.utils.UnitSpec

class SlotStealingSpec extends UnitSpec {

  // -- Slot attribute survives stealing (move between separate inserters) --

  it("restores the slot attribute when an element is stolen out of a slot and then stolen back") {
    // Stealing = the same element `val` referenced by two lists; an add-first emission relocates
    // it seamlessly. Its slot must track wherever it currently lives: cleared in the plain list,
    // restored when the slotted list steals it back – all without re-mounting.
    val tracker = createEventTracker()
    val e = tracker.createSpan("E")
    tracker.clear()
    val slotItems = Var[List[HtmlElement]](List(e))
    val plainItems = Var[List[HtmlElement]](Nil)
    val slotHost = div(new Slot("prefix")(children <-- slotItems.signal))
    val plainHost = div(children <-- plainItems.signal)

    mount(div(slotHost, plainHost))

    withClue("starts in the slot:") {
      tracker.assertEvents(_.mounted("E")).clear()
      e.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(
        div.of(sentinel, span.of("E", slot is "prefix"), sentinel),
        div.of(sentinel, sentinel)
      ))
    }

    withClue("stolen into the plain list: slot cleared, no re-mount:") {
      plainItems.set(List(e))
      slotItems.set(Nil)
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe plainHost.ref
      e.ref.getAttribute("slot") shouldBe null
      expectNode(div.of(
        div.of(sentinel, sentinel),
        div.of(sentinel, span.of("E"), sentinel)
      ))
    }

    withClue("stolen back into the slot: slot restored, no re-mount:") {
      slotItems.set(List(e))
      plainItems.set(Nil)
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe slotHost.ref
      e.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(
        div.of(sentinel, span.of("E", slot is "prefix"), sentinel),
        div.of(sentinel, sentinel)
      ))
    }
  }

  it("keeps a stolen nested-group element correctly slotted through an outer reaffirm and reclaim") {
    // Guards the `NestedGroup.applySlot` fast path (which skips when the effective slot is
    // unchanged) against stealing. An element inside a nested `children <--` is stolen away by a
    // foreign UNSLOTTED list, so it must LOSE the slot; while it is away, the outer slotted list
    // re-emits – a fast-path reaffirm that must NOT stomp the stolen element back into the slot;
    // and when the nested list reclaims it, it must REGAIN the slot. All moves are seamless.
    val tracker = createEventTracker()
    val e = tracker.createSpan("E")
    tracker.clear()
    val innerItems = Var[List[HtmlElement]](List(e))
    val nested: Inserter = children <-- innerItems.signal
    val outerItems = Var[List[Inserter]](List(nested))
    val thiefItems = Var[List[HtmlElement]](Nil)
    val slotHost = div(new Slot("prefix")(children <-- outerItems.signal))
    val thiefHost = div(children <-- thiefItems.signal)

    mount(div(slotHost, thiefHost))

    withClue("the nested element starts in the slot:") {
      tracker.assertEvents(_.mounted("E")).clear()
      e.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(
        div.of(sentinel, sentinel, span.of("E", slot is "prefix"), sentinel, sentinel),
        div.of(sentinel, sentinel)
      ))
    }

    withClue("a foreign unslotted list steals it: slot cleared, no re-mount:") {
      thiefItems.set(List(e))
      innerItems.set(Nil)
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe thiefHost.ref
      e.ref.getAttribute("slot") shouldBe null
    }

    withClue("the outer slotted list re-emits (fast-path reaffirm): the stolen element is left alone:") {
      outerItems.set(List(nested))
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe thiefHost.ref
      e.ref.getAttribute("slot") shouldBe null
    }

    withClue("the nested list reclaims it: slot restored, no re-mount:") {
      innerItems.set(List(e))
      thiefItems.set(Nil)
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe slotHost.ref
      e.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(
        div.of(sentinel, sentinel, span.of("E", slot is "prefix"), sentinel, sentinel),
        div.of(sentinel, sentinel)
      ))
    }
  }

  it("an outer fast-path reaffirm does not re-slot an element stolen out of a nested group (stale contentMap)") {
    // The sharpest test of the `NestedGroup.applySlot` fast path. An add-first steal that does NOT
    // also remove the element from the nested list leaves the nested group's contentMap STALE –
    // still referencing an element that now physically lives in the foreign unslotted list. An
    // outer reaffirm then walks that stale entry. The fast path (effective slot unchanged) must
    // skip it: re-slotting here would wrongly stamp `prefix` onto an element that belongs to the
    // thief. This pins the invariant that the group's slot follows its OWN content, not stale refs.
    val tracker = createEventTracker()
    val e = tracker.createSpan("E")
    tracker.clear()
    val innerItems = Var[List[HtmlElement]](List(e))
    val nested: Inserter = children <-- innerItems.signal
    val outerItems = Var[List[Inserter]](List(nested))
    val thiefItems = Var[List[HtmlElement]](Nil)
    val slotHost = div(new Slot("prefix")(children <-- outerItems.signal))
    val thiefHost = div(children <-- thiefItems.signal)

    mount(div(slotHost, thiefHost))
    tracker.assertEvents(_.mounted("E")).clear()

    withClue("add-first steal WITHOUT removing from the nested list leaves its contentMap stale:") {
      thiefItems.set(List(e)) // e now physically in the unslotted thief; nested still 'holds' it
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe thiefHost.ref
      e.ref.getAttribute("slot") shouldBe null
    }

    withClue("the outer fast-path reaffirm must leave the stolen element alone:") {
      outerItems.set(List(nested))
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe thiefHost.ref
      e.ref.getAttribute("slot") shouldBe null // wrongly "prefix" if the reaffirm stomped it
    }
  }

  it("moves the slot attribute when an element is stolen between two DIFFERENT slots and back") {
    // Stealing between two slotted lists must re-slot the element to the destination's slot each
    // time (not merely clear it), tracking whichever slot currently owns it, without re-mounting.
    val tracker = createEventTracker()
    val e = tracker.createSpan("E")
    tracker.clear()
    val prefixItems = Var[List[HtmlElement]](List(e))
    val suffixItems = Var[List[HtmlElement]](Nil)
    val prefixHost = div(new Slot("prefix")(children <-- prefixItems.signal))
    val suffixHost = div(new Slot("suffix")(children <-- suffixItems.signal))

    mount(div(prefixHost, suffixHost))

    withClue("starts in the prefix slot:") {
      tracker.assertEvents(_.mounted("E")).clear()
      e.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(
        div.of(sentinel, span.of("E", slot is "prefix"), sentinel),
        div.of(sentinel, sentinel)
      ))
    }

    withClue("stolen into the suffix slot: attribute replaced, no re-mount:") {
      suffixItems.set(List(e))
      prefixItems.set(Nil)
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe suffixHost.ref
      e.ref.getAttribute("slot") shouldBe "suffix"
      expectNode(div.of(
        div.of(sentinel, sentinel),
        div.of(sentinel, span.of("E", slot is "suffix"), sentinel)
      ))
    }

    withClue("stolen back into the prefix slot: attribute replaced again, no re-mount:") {
      prefixItems.set(List(e))
      suffixItems.set(Nil)
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe prefixHost.ref
      e.ref.getAttribute("slot") shouldBe "prefix"
    }
  }

  it("an outer fast-path reaffirm keeps the destination slot of an element stolen into another slot (stale contentMap)") {
    // Same fast-path hazard as the unslotted-thief case, but the thief is ITSELF slotted. An
    // add-first steal (no removal from the nested list) leaves the nested group's contentMap stale
    // while the element physically carries the THIEF's slot. The outer reaffirm of the original
    // ("prefix") group must NOT stamp `prefix` back onto it – it lives in the `suffix` slot now.
    val tracker = createEventTracker()
    val e = tracker.createSpan("E")
    tracker.clear()
    val innerItems = Var[List[HtmlElement]](List(e))
    val nested: Inserter = children <-- innerItems.signal
    val outerItems = Var[List[Inserter]](List(nested))
    val thiefItems = Var[List[HtmlElement]](Nil)
    val prefixHost = div(new Slot("prefix")(children <-- outerItems.signal))
    val suffixThief = div(new Slot("suffix")(children <-- thiefItems.signal))

    mount(div(prefixHost, suffixThief))
    tracker.assertEvents(_.mounted("E")).clear()

    withClue("add-first steal into the suffix slot leaves the nested contentMap stale:") {
      thiefItems.set(List(e)) // e now physically in the suffix slot; nested still 'holds' it
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe suffixThief.ref
      e.ref.getAttribute("slot") shouldBe "suffix"
    }

    withClue("the outer prefix reaffirm must keep the destination (suffix) slot, not stomp prefix:") {
      outerItems.set(List(nested))
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe suffixThief.ref
      e.ref.getAttribute("slot") shouldBe "suffix" // wrongly "prefix" if the reaffirm stomped it
    }
  }
}
