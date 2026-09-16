package com.raquo.laminar

import com.raquo.domtestutils.matching.{ExpectedNode, Rule}
import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.nodes.Slot
import com.raquo.laminar.utils.UnitSpec

class SlotStealingSpec extends UnitSpec {

  // -- Fixtures for sibling-stealing choreographies --

  // Two `children <--` lists can be SIBLINGS under one parent, or live under two separate
  // parents. The same-parent layout is the sharp one: the lists no longer differ by DOM
  // `parentNode`, which is the heuristic the inserters use to tell "I still own this content"
  // from "another list stole it". `SiblingSlots` builds both layouts so a steal choreography can
  // run against each without duplicating it.

  private sealed trait Layout { def name: String }
  private case object SameParent extends Layout { val name = "same-parent" }
  private case object CrossParent extends Layout { val name = "cross-parent" }

  /** Two sibling slotted `children <--` lists, either under ONE shared parent ([[SameParent]])
    * or under two separate sibling parent divs ([[CrossParent]]). `items1` / `items2` feed the
    * lists (elements or dynamic inserters, as `Inserter`); the caller mounts [[root]].
    */
  private class SiblingSlots(val layout: Layout, slot1: String, slot2: String) {
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val root = layout match {
      case SameParent =>
        div(new Slot(slot1)(children <-- items1.signal), new Slot(slot2)(children <-- items2.signal))
      case CrossParent =>
        div(
          div(new Slot(slot1)(children <-- items1.signal)),
          div(new Slot(slot2)(children <-- items2.signal))
        )
    }

    /** Assert the whole DOM given each list's CONTENT nodes: this wraps each list's content in
      * that list's own leading + trailing sentinels, then composes the two per the layout.
      */
    def expectRoot(list1: List[ExpectedNode], list2: List[ExpectedNode]): Unit = {
      def listSpan(content: List[ExpectedNode]): List[Rule] =
        (sentinel +: content :+ sentinel).map(expectedNodeAsExpectedChildRule)
      val expected = layout match {
        case SameParent => div.of((listSpan(list1) ++ listSpan(list2)): _*)
        case CrossParent => div.of(div.of(listSpan(list1): _*), div.of(listSpan(list2): _*))
      }
      expectNode(expected)
    }
  }

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

  // -- Sibling stealing: same-parent vs cross-parent (run against both layouts) --

  List[Layout](SameParent, CrossParent).foreach { layout =>

    it(s"[${layout.name}] re-emitting a slotted list steals a nested-group ITEM back and re-slots it (last write wins)") {
      // The stolen item is a nested `children <--` group ITSELF (a DynamicInserter), re-stolen by
      // a stale re-emit. The re-steal must restore the group's context slot, not merely relocate
      // its nodes: we prove it by appending NEW content after the re-steal – that content is only
      // slotted correctly if the group's `currentSlotName` was restored to list1's slot.
      val tracker = createEventTracker()
      val a = tracker.createSpan("A")
      val b = tracker.createSpan("B")
      tracker.clear()
      val inner = Var[List[Inserter]](List(a))
      val nested: Inserter = children <-- inner.signal
      val f = new SiblingSlots(layout, slot1 = "prefix", slot2 = "suffix")
      f.items1.set(List(nested))
      mount(f.root)

      withClue("the nested item's content starts in list1's slot: ") {
        tracker.assertEvents(_.mounted("A")).clear()
        a.ref.getAttribute("slot") shouldBe "prefix"
      }
      withClue("list2 steals the whole nested item add-first, so list1's map goes stale: ") {
        f.items2.set(List(nested))
        tracker.assertNoEvents.clear()
        a.ref.getAttribute("slot") shouldBe "suffix"
      }
      withClue("list1 re-emits and steals it back, re-slotting its content, no re-mount: ") {
        f.items1.set(List(nested))
        tracker.assertNoEvents.clear()
        a.ref.getAttribute("slot") shouldBe "prefix"
      }
      withClue("new content added AFTER the re-steal lands in list1's slot: ") {
        inner.set(List(a, b))
        tracker.assertEvents(_.mounted("B")).clear()
        b.ref.getAttribute("slot") shouldBe "prefix" // wrongly "suffix" if currentSlotName wasn't restored
        f.expectRoot(
          list1 = List(sentinel, span.of("A", slot is "prefix"), span.of("B", slot is "prefix"), sentinel),
          list2 = List()
        )
      }
    }

    it(s"[${layout.name}] re-emitting a slotted list steals a LEAF back and re-slots it (last write wins)") {
      // Leaf items re-slot correctly even same-parent (their move runs the full `insertChildAfter`
      // path, which applies the slot). This pins that and contrasts with the DynamicInserter case.
      val tracker = createEventTracker()
      val e = tracker.createSpan("E")
      tracker.clear()
      val f = new SiblingSlots(layout, slot1 = "prefix", slot2 = "suffix")
      f.items1.set(List(e))
      mount(f.root)

      withClue("starts in list1's slot: ") {
        tracker.assertEvents(_.mounted("E")).clear()
        e.ref.getAttribute("slot") shouldBe "prefix"
      }
      withClue("list2 steals add-first: ") {
        f.items2.set(List(e))
        tracker.assertNoEvents.clear()
        e.ref.getAttribute("slot") shouldBe "suffix"
      }
      withClue("list1 re-emits WITH e and re-steals it back, no re-mount: ") {
        f.items1.set(List(e))
        tracker.assertNoEvents.clear()
        e.ref.getAttribute("slot") shouldBe "prefix"
        f.expectRoot(list1 = List(span.of("E", slot is "prefix")), list2 = List())
      }
    }

    it(s"[${layout.name}] a victim removing a stolen item after the theft leaves the thief undisturbed") {
      // The removal-direction counterpart. After list2 steals add-first, list1 re-emits WITHOUT
      // the item. list1 must NOT tear it down – the stolen item has left list1's span, so list1's
      // span-scoped diff never touches it – and the thief keeps it mounted and live.
      val tracker = createEventTracker()
      val a = tracker.createSpan("A")
      val z = tracker.createSpan("Z")
      tracker.clear()
      val inner = Var[List[Inserter]](List(a))
      val nested: Inserter = children <-- inner.signal
      val f = new SiblingSlots(layout, slot1 = "prefix", slot2 = "suffix")
      f.items1.set(List(nested))
      mount(f.root)
      tracker.assertEvents(_.mounted("A")).clear()

      withClue("list2 steals the nested item add-first: ") {
        f.items2.set(List(nested))
        tracker.assertNoEvents.clear()
        a.ref.getAttribute("slot") shouldBe "suffix"
      }
      withClue("list1 re-emits WITHOUT it: the thief keeps it, no re-mount: ") {
        f.items1.set(Nil)
        tracker.assertNoEvents.clear()
        a.ref.getAttribute("slot") shouldBe "suffix"
      }
      withClue("the item stays live in the thief: new content lands in list2's slot: ") {
        inner.set(List(a, z))
        tracker.assertEvents(_.mounted("Z")).clear()
        z.ref.getAttribute("slot") shouldBe "suffix"
        f.expectRoot(
          list1 = List(),
          list2 = List(sentinel, span.of("A", slot is "suffix"), span.of("Z", slot is "suffix"), sentinel)
        )
      }
    }
  }

  // https://github.com/raquo/Laminar/pull/205
  it("updates a retained slot while removing its preceding sibling") {
    val tracker = createEventTracker()
    val before = tracker.createSpan("before")
    val kept = tracker.createSpan("kept")
    tracker.clear()
    val prefix: Inserter = new Slot("prefix")(kept).head
    val suffix: Inserter = new Slot("suffix")(kept).head
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

  // https://github.com/raquo/Laminar/pull/205 but amended to reverse expectations
  it("re-emitting a static group steals its child back from another parent (last write wins)") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val group: Inserter = new Slot("prefix")(List(a, b)).head
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

  // -- Nested thief: the thief shares the victim's DOM parent AND lives inside its span --

  // The steal tests above use a SEPARATE thief list (sibling or cross-parent): the stolen item
  // always leaves the victim's span into a DISJOINT span. Here the thief is instead a NESTED
  // `children <--` that is itself an ITEM of the victim list, so the stolen item ends up *inside*
  // the victim's own sentinel span (still under the same parent). This is the sharp case for the
  // removal-direction guard: `parentNode` cannot tell victim from thief, and "physically in the
  // victim's span" no longer implies "owned by the victim". Safety rests on the diff walking its
  // own span item-by-item via `lastNode.nextSibling`, stepping over each sub-inserter's whole span
  // atomically – so it never descends into the nested thief to reach the stolen item.

  it("[nested-thief] victim drops a stolen leaf but keeps the thief: the thief keeps it live") {
    val tracker = createEventTracker()
    val x = tracker.createSpan("X")
    val y = tracker.createSpan("Y")
    tracker.clear()
    val innerT = Var[List[Inserter]](Nil)
    val thief: Inserter = children <-- innerT.signal
    val victim = Var[List[Inserter]](List(thief, x))
    mount(div(children <-- victim.signal))
    tracker.assertEvents(_.mounted("X")).clear()

    withClue("the nested thief steals x (x moves inside the thief's span, still under P): ") {
      innerT.set(List(x))
      tracker.assertNoEvents.clear()
    }
    withClue("victim re-emits WITHOUT x but WITH the thief: x must stay live inside the thief: ") {
      victim.set(List(thief))
      tracker.assertNoEvents.clear()
    }
    withClue("the thief is still live (new content mounts alongside the stolen x): ") {
      innerT.set(List(x, y))
      tracker.assertEvents(_.mounted("Y")).clear()
    }
  }

  it("[nested-thief] victim drops BOTH the thief and its stolen leaf: one clean teardown") {
    val tracker = createEventTracker()
    val x = tracker.createSpan("X")
    tracker.clear()
    val innerT = Var[List[Inserter]](Nil)
    val thief: Inserter = children <-- innerT.signal
    val victim = Var[List[Inserter]](List(thief, x))
    mount(div(children <-- victim.signal))
    tracker.assertEvents(_.mounted("X")).clear()

    withClue("the nested thief steals x: ") {
      innerT.set(List(x))
      tracker.assertNoEvents.clear()
    }
    withClue("victim drops everything: x goes down with the thief exactly once: ") {
      victim.set(Nil)
      tracker.assertEvents(_.unmounted("X")).clear()
    }
  }

  it("[nested-thief] opposite direction: a leaf stolen OUT of the thief is not torn down by it") {
    val tracker = createEventTracker()
    val x = tracker.createSpan("X")
    val y = tracker.createSpan("Y")
    tracker.clear()
    val innerT = Var[List[Inserter]](List(x)) // x starts INSIDE the nested inserter
    val thief: Inserter = children <-- innerT.signal
    val victim = Var[List[Inserter]](List(thief))
    mount(div(children <-- victim.signal))
    tracker.assertEvents(_.mounted("X")).clear()

    withClue("victim also lists x directly, pulling x OUT of the nested inserter: ") {
      victim.set(List(thief, x))
      tracker.assertNoEvents.clear()
    }
    withClue("the nested inserter re-emits WITHOUT x: must not tear down the now victim-owned x: ") {
      innerT.set(Nil)
      tracker.assertNoEvents.clear()
    }
    withClue("x is still live under the victim: ") {
      victim.set(List(thief, x, y))
      tracker.assertEvents(_.mounted("Y")).clear()
    }
  }

  it("[nested-thief] the stolen item is itself a nested inserter: its subscription stays live") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val innerX = Var[List[Inserter]](List(a))
    val x: Inserter = children <-- innerX.signal // the STOLEN item is a nested inserter
    val innerT = Var[List[Inserter]](Nil)
    val thief: Inserter = children <-- innerT.signal
    val victim = Var[List[Inserter]](List(thief, x))
    mount(div(children <-- victim.signal))
    tracker.assertEvents(_.mounted("A")).clear()

    withClue("the nested thief steals x (a whole nested inserter) into its own span: ") {
      innerT.set(List(x))
      tracker.assertNoEvents.clear()
    }
    withClue("victim drops x but keeps the thief: x must stay live under the thief: ") {
      victim.set(List(thief))
      tracker.assertNoEvents.clear()
    }
    withClue("x's own subscription is still live: its content updates: ") {
      innerX.set(List(a, b))
      tracker.assertEvents(_.mounted("B")).clear()
    }
  }

  it("[nested-thief] steal a MIDDLE leaf into the thief while a trailing sibling survives") {
    // Stresses the diff's cursor/count bookkeeping: the stolen leaf sits inside the thief's
    // sub-span while a real trailing item (c) still follows. A mis-step into the thief's interior
    // would either tear down the stolen leaf or throw on a node missing from the victim's map.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val c = tracker.createSpan("C")
    val d = tracker.createSpan("D")
    tracker.clear()
    val innerT = Var[List[Inserter]](Nil)
    val thief: Inserter = children <-- innerT.signal
    val victim = Var[List[Inserter]](List(a, thief, c)) // leaf, nested thief, leaf
    mount(div(children <-- victim.signal))
    tracker.assertEvents(_.mounted("A"), _.mounted("C")).clear()

    withClue("the thief steals the leading leaf a into its span: ") {
      innerT.set(List(a))
      tracker.assertNoEvents.clear()
    }
    withClue("victim re-emits [thief, c] (drops a, keeps the thief and trailing c): ") {
      victim.set(List(thief, c))
      tracker.assertNoEvents.clear() // a survives inside the thief; c untouched
    }
    withClue("everything still live (append d after c): ") {
      victim.set(List(thief, c, d))
      tracker.assertEvents(_.mounted("D")).clear()
    }
  }

  it("[nested-thief] reorder the thief (holding a stolen leaf) past a surviving sibling") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val c = tracker.createSpan("C")
    val z = tracker.createSpan("Z")
    tracker.clear()
    val innerT = Var[List[Inserter]](Nil)
    val thief: Inserter = children <-- innerT.signal
    val victim = Var[List[Inserter]](List(a, thief, c))
    mount(div(children <-- victim.signal))
    tracker.assertEvents(_.mounted("A"), _.mounted("C")).clear()

    withClue("the thief steals a: ") {
      innerT.set(List(a))
      tracker.assertNoEvents.clear()
    }
    withClue("reorder to [c, thief]: the thief (with a inside) moves past c, a stays live: ") {
      victim.set(List(c, thief))
      tracker.assertNoEvents.clear()
    }
    withClue("the thief is still live after the reorder: ") {
      innerT.set(List(a, z))
      tracker.assertEvents(_.mounted("Z")).clear()
    }
  }

  it("re-emitting a slotted single node steals it back into its slot (last write wins)") {
    val tracker = createEventTracker()
    val e = tracker.createSpan("E")
    tracker.clear()
    val item: Inserter = new Slot("prefix")(e).head
    val slotItems = Var(List(item))
    val thiefItems = Var(List.empty[HtmlElement])
    val slotHost = div(children <-- slotItems.signal)
    val thiefHost = div(children <-- thiefItems.signal)

    mount(div(slotHost, thiefHost))
    tracker.assertEvents(_.mounted("E")).clear()
    e.ref.getAttribute("slot") shouldBe "prefix"

    withClue("The thief steals E, clearing its slot, no remount: ") {
      thiefItems.set(List(e))
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe thiefHost.ref
      e.ref.getAttribute("slot") shouldBe null
    }

    withClue("Re-emitting the slotted item re-adopts E and re-slots it, no remount: ") {
      slotItems.set(List(item))
      tracker.assertNoEvents.clear()
      e.ref.parentNode shouldBe slotHost.ref
      e.ref.getAttribute("slot") shouldBe "prefix"
      expectNode(div.of(
        div.of(sentinel, span.of("E", slot is "prefix"), sentinel),
        div.of(sentinel, sentinel)
      ))
    }
  }
}
