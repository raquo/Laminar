package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.ButtonElement
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.nodes.{ChildNode, Slot, TextNode}
import com.raquo.laminar.utils.UnitSpec

class SlotSpec extends UnitSpec {

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

  it("re-slotting a moved group re-slots only its live span, leaving a sibling-stolen node's slot with its new host") {
    // A nested group's slot reconcile (`NestedGroup.applySlot`) must re-slot only the inner nodes
    // still in the group's span. A node a THIRD slot stole out of the group keeps that slot's
    // attribute – the group must not rewrite it from under its new host. The reconcile is reached
    // via a same-parent re-steal between two sibling Slots (moveWithinDynamicList's same-parent
    // branch, which runs applySlot); a move between slots (moveToParent) is exercised on the way in.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val gInner = Var[List[Inserter]](List(a, b))
    val group: Inserter = children <-- gInner.signal
    val prefixItems = Var[List[Inserter]](Nil)
    val suffixItems = Var[List[Inserter]](Nil)
    val thiefItems = Var[List[Inserter]](Nil)

    // Three sibling Slots under ONE element (Slot is transparent), so a re-steal between the prefix
    // and suffix lists shares a parent element and takes the SAME-parent reconcile branch.
    mount(
      div(
        new Slot("prefix")(children <-- prefixItems.signal),
        new Slot("suffix")(children <-- suffixItems.signal),
        new Slot("thief")(children <-- thiefItems.signal)
      )
    )

    withClue("the group renders A, B in the prefix slot:") {
      prefixItems.set(List(group))
      tracker.assertEvents(_.mounted("A"), _.mounted("B")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      b.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("the thief slot steals B out of the group; B takes the thief's slot (no re-mount):") {
      thiefItems.set(List(b))
      tracker.assertNoEvents.clear()
      b.ref.getAttribute("slot") shouldBe "thief"
    }

    withClue("the group is stolen into the suffix slot: only A travels and is re-slotted; B untouched:") {
      suffixItems.set(List(group)) // add-first; prefix's map goes stale (do not remove, same parent)
      tracker.assertNoEvents.clear()
      a.ref.getAttribute("slot") shouldBe "suffix"
      b.ref.getAttribute("slot") shouldBe "thief"
    }

    withClue("re-emitting the prefix list steals the group back (same-parent reconcile); B keeps the thief's slot:") {
      prefixItems.set(List(group))
      tracker.assertNoEvents.clear()
      a.ref.getAttribute("slot") shouldBe "prefix" // live span re-slotted
      b.ref.getAttribute("slot") shouldBe "thief" // departed node NOT re-slotted
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

  // https://github.com/raquo/Laminar/pull/198
  it("preserves a static inserter's explicit slot when nested in children <--") {
    // `SlottableChildrenInserter.addToDynamicList` inserts its node using the `hooks`
    // ARGUMENT passed by the enclosing `children <--` (here: none), discarding the
    // inserter's OWN `hooks` field (the Slot attribute hook). So the item's slot is
    // lost on placement, even though rendering the same item directly
    // (`div(Slot("prefix")(a))`) applies it correctly.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val itemA: Inserter = new Slot("prefix")(a).head
    val itemB: Inserter = new Slot("prefix")(b).head
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

  // https://github.com/raquo/Laminar/pull/198
  it("preserves a nested inserter's explicit slot on every emission") {
    val tracker = createEventTracker()
    val bus = EventBus[HtmlElement]()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val item: Inserter = new Slot("prefix")(child <-- bus.events).head

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

  // https://github.com/raquo/Laminar/pull/198
  it("applies the destination slot when an existing nested inserter moves into it") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    val item: Inserter = child <-- Val(a)
    val leftItems = Var(List(item))
    val rightItems = Var(List.empty[Inserter])
    val left = div(children <-- leftItems.signal)
    val right = div(new Slot("prefix")(children <-- rightItems.signal))

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

  // https://github.com/raquo/Laminar/pull/198
  it("applies the destination slot to emissions after moving an empty nested inserter") {
    val tracker = createEventTracker()
    val bus = EventBus[HtmlElement]()
    val a = tracker.createSpan("A")
    tracker.clear()
    val item: Inserter = child <-- bus.events
    val leftItems = Var(List(item))
    val rightItems = Var(List.empty[Inserter])
    val left = div(children <-- leftItems.signal)
    val right = div(new Slot("prefix")(children <-- rightItems.signal))

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

  // -- Slot precedence: item's own slot vs the enclosing list's slot --

  it("an item's own slot wins over the enclosing slotted `children <--` list slot") {
    // `slotName.orElse(listSlotName)`: an item carrying its OWN `Slot` wrapper keeps that slot
    // even inside a list that applies a different slot; a plain sibling takes the list's slot.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val ownSlotItem: Inserter = new Slot("inner")(a).head
    val plainItem: Inserter = child <-- Val(b)
    val items = Var(List(ownSlotItem, plainItem))

    mount(div(new Slot("outer")(children <-- items.signal)))

    withClue("own slot wins for the wrapped item; the list slot applies to the plain sibling:") {
      tracker.assertEvents(_.mounted("A"), _.mounted("B")).clear()
      a.ref.getAttribute("slot") shouldBe "inner"
      b.ref.getAttribute("slot") shouldBe "outer"
      expectNode(div.of(
        sentinel,
        span.of("A", slot is "inner"),
        sentinel,
        span.of("B", slot is "outer"),
        sentinel,
        sentinel
      ))
    }
  }

  // -- Raw text node in a named slot --

  it("reports a re-emitted error for a raw text node in a named slot, still slotting sibling elements") {
    // Named slots only accept elements: a raw text node in a slot is reported as an error (and
    // lands in the default slot). The report is intentionally NOT deduplicated, so a retained
    // list re-reports on every reconcile – consistent, easy-to-reproduce noise by design.
    val t = new TextNode("hello")
    val el = span("E")
    val items = Var[List[ChildNode.Base]](List(t, el))

    withCollectedAirstreamErrors { errors =>
      mount(div(new Slot("prefix")(children <-- items.signal)))

      withClue("first render: one error for the text node; the element still gets the slot:") {
        errors.size shouldBe 1
        el.ref.getAttribute("slot") shouldBe "prefix"
        assert(t.ref.parentNode != null) // text still lands in the DOM (default slot)
      }

      withClue("a retained re-set re-reports the text-node error (dedup is intentionally not done):") {
        items.set(List(t, el))
        errors.size shouldBe 2
        el.ref.getAttribute("slot") shouldBe "prefix"
      }
    }
  }

  // -- Reorder of dynamic-inserter items in a slotted list --

  it("keeps each slot across a reorder of dynamic-inserter items in a slotted `children <--`") {
    // A same-list reorder is a lateral move: the item keeps whatever slot it already has,
    // and its content is not re-mounted.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val itemA: Inserter = child <-- Val(a)
    val itemB: Inserter = child <-- Val(b)
    val items = Var(List(itemA, itemB))

    mount(div(new Slot("prefix")(children <-- items.signal)))

    withClue("both dynamic items get the list slot on first render:") {
      tracker.assertEvents(_.mounted("A"), _.mounted("B")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      b.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("reordering the items is a lateral move – no remount, slot preserved:") {
      items.set(List(itemB, itemA))
      tracker.assertNoEvents.clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      b.ref.getAttribute("slot") shouldBe "prefix"
    }
  }

  // -- Moving a static multi-node group between slots --

  it("re-slots every node when a static group moves between two slots, without remounting") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val group: Inserter = List(a, b)
    val prefixItems = Var(List(group))
    val suffixItems = Var(List.empty[Inserter])
    val prefixHost = div(new Slot("prefix")(children <-- prefixItems.signal))
    val suffixHost = div(new Slot("suffix")(children <-- suffixItems.signal))

    mount(div(prefixHost, suffixHost))

    withClue("the group starts in the prefix slot:") {
      tracker.assertEvents(_.mounted("A"), _.mounted("B")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
      b.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("moving the group into the suffix slot re-slots both nodes without remounting:") {
      suffixItems.set(List(group))
      prefixItems.set(Nil)
      tracker.assertNoEvents.clear()
      a.ref.parentNode shouldBe suffixHost.ref
      b.ref.parentNode shouldBe suffixHost.ref
      a.ref.getAttribute("slot") shouldBe "suffix"
      b.ref.getAttribute("slot") shouldBe "suffix"
    }
  }

  // -- Node -> empty -> node under a slot --

  it("re-applies the slot when a slotted `child.maybe <--` goes node -> empty -> node") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    val bus = EventBus[Option[HtmlElement]]()
    mount(div(new Slot("prefix")(child.maybe <-- bus.events).head))

    withClue("the emitted node gets the slot:") {
      bus.emit(Some(a))
      tracker.assertEvents(_.mounted("A")).clear()
      a.ref.getAttribute("slot") shouldBe "prefix"
    }

    withClue("emitting None removes the node:") {
      bus.emit(None)
      tracker.assertEvents(_.unmounted("A")).clear()
      a.ref.parentNode shouldBe null
    }

    withClue("re-emitting the node re-acquires the slot:") {
      bus.emit(Some(a))
      tracker.assertEvents(_.mounted("A")).clear()
      assert(a.ref.parentNode != null)
      a.ref.getAttribute("slot") shouldBe "prefix"
    }
  }

  // -- Empty static group --

  it("an empty static group in a slot renders nothing (no placeholder, no crash)") {
    mount(div(new Slot("prefix")(List.empty[HtmlElement]).head))

    withClue("the empty group renders nothing:") {
      expectNode(div.of())
    }
  }


}
