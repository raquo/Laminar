package com.raquo.laminar.tests

import com.raquo.domtestutils.matching.{ExpectedNode, Rule}
import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.{CollectionCommand, Inserter}
import com.raquo.laminar.nodes.TextNode
import com.raquo.laminar.utils.UnitSpec

/** THE move / transfer suite for inserters: one home for every "relocate an item, then assert the
  * lifecycle consequence" test. It walks the move taxonomy — FROM x TO x direction x item-type x
  * depth — with each case asserting whether the move is SEAMLESS (no re-mount; the DOM span is
  * relocated and subscriptions transferred) or DELIBERATELY re-mounts (documented as such).
  *
  * Sections, in order:
  *   1. Classic single-node `child <--` moves (pre-#157 path: ChildInserter.switchToChild).
  *   2. Classic `children <--` plain-element moves (pre-#157 reconcile: updateChildren).
  *   3. A dynamic inserter reordered WITHIN one `children <--` list.
  *   4. A dynamic inserter moved BETWEEN two `children <--` lists (add-first steal / remove-first /
  *      the #163 two-bindings characterization), including nested and depth-2/3 spans.
  *   4b. Steal-BACK from a sibling (stale-re-emit re-steal), run against both same-parent and
  *      cross-parent layouts via the `TwoLists` fixture — element, nested group, `child`, `text`,
  *      `children.command` items.
  *   4c. A moved span relocates exactly its LIVE DOM span (DOM order + departed nodes left behind).
  *   4d. Re-placing a torn-down group: with no live span to move, rebuild + re-mount, like re-adding
  *      a removed element.
  *   4e. Un-nesting: a leaving nested item releases the nodes / inserters the list keeps.
  *   5. Promote / demote across static application and a list (static <-> list, static <-> static).
  *   6. The inserter-TYPE matrix: `children.command <--` and `text <--` as moved items.
  *   7. The degenerate same-transaction double-add.
  *
  * The #157 guarantee under test throughout: a move relocates an item's DOM span and transfers its
  * subscriptions WITHOUT re-mounting its content. See notes/Testing.md for assertion conventions.
  */
class InserterMoveSpec extends UnitSpec {

  // -- Fixture: two sibling `children <--` lists, either under ONE shared parent (`SameParent`) or
  //    under two separate parent divs (`CrossParent`). This is the move-suite analog of
  //    `SlotStealingSpec.SiblingSlots` (minus the slots). The same-parent layout is the sharp one
  //    for steal-BACK: the two lists no longer differ by DOM `parentNode`, so a DynamicInserter
  //    re-stolen from a sibling takes `NestedGroup.moveTo`'s SAME-parent branch (a raw
  //    reposition, no `moveToParent`) rather than the cross-parent transfer. A steal choreography is
  //    written once and registered against both layouts. `expectRoot` wraps each list's content in
  //    that list's own leading + trailing sentinels, then composes the two per the layout.

  private sealed trait Layout { def name: String }
  private case object SameParent extends Layout { val name = "same-parent" }
  private case object CrossParent extends Layout { val name = "cross-parent" }

  private class TwoLists(val layout: Layout) {
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val root = layout match {
      case SameParent => div(children <-- items1.signal, children <-- items2.signal)
      case CrossParent => div(div(children <-- items1.signal), div(children <-- items2.signal))
    }
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

  // ----------------------------------------------------------------------------------
  // 1. Classic single-node `child <--` moves (ChildInserter.switchToChild)
  // ----------------------------------------------------------------------------------

  it("can move child from one receiver to another") {

    val tracker = createEventTracker()
    val spanA = tracker.createSpan("a")
    val spanB = tracker.createSpan("b")
    val spanC = tracker.createSpan("c")
    val spanD = tracker.createSpan("d")
    val spanE = tracker.createSpan("e")

    val bus1 = new EventBus[HtmlElement]
    val bus2 = new EventBus[HtmlElement]

    val el = div(
      child <-- bus1,
      child <-- bus2,
    )

    mount(el)
    tracker.clear() // drop the upfront element-create logs

    // --

    expectNode(
      div of (
        sentinel,
        sentinel
      )
    )
    tracker.assertNoEvents.clear()

    // --

    EventBus.emit(
      bus1 -> spanA,
      bus2 -> spanD
    )

    expectNode(
      div of (
        sentinel,
        span of "a",
        sentinel,
        span of "d",
      )
    )
    tracker.assertEvents(_.mounted("a"), _.mounted("d")).clear()

    // -- Steal D from inserter #2 to inserter #1

    EventBus.emit(
      bus1 -> spanD
    )

    expectNode(
      div of (
        sentinel,
        span of "d",
        sentinel
      )
    )
    // Seamless steal: D relocates from #2 into #1 with no re-mount; only the displaced A unmounts.
    tracker.assertEvents(_.unmounted("a")).clear()

    // -- Request invalid state (same element in both places)

    EventBus.emit(
      bus1 -> spanA,
      bus2 -> spanA
    )

    expectNode(
      div of (
        sentinel,
        sentinel,
        span of "a"
      )
    )
    // #1 goes D->A (unmount D, mount A); #2 then steals A out of #1 seamlessly, leaving #1 empty.
    tracker.assertEvents(_.unmounted("d"), _.mounted("a")).clear()

    // -- Recover from invalid state

    EventBus.emit(
      bus1 -> spanA,
      bus2 -> spanB
    )

    expectNode(
      div of (
        sentinel,
        span of "a",
        sentinel,
        span of "b",
      )
    )
    // #1 steals A back out of #2 seamlessly; #2 then mounts a fresh B. Only B is a new mount.
    tracker.assertEvents(_.mounted("b")).clear()

    // -- Unmount and re-mount

    unmount()
    tracker.assertEvents(_.unmounted("a"), _.unmounted("b")).clear()

    mount(el)

    expectNode(
      div of (
        sentinel,
        span of "a",
        sentinel,
        span of "b",
      )
    )
    // Retained across the unmount/re-mount cycle: both nodes re-mount in place.
    tracker.assertEvents(_.mounted("a"), _.mounted("b")).clear()

    // --

    EventBus.emit(
      bus1 -> spanC,
      bus2 -> spanD
    )

    expectNode(
      div of (
        sentinel,
        span of "c",
        sentinel,
        span of "d"
      )
    )
    // Independent self-replaces in each receiver: A->C in #1, B->D in #2.
    tracker.assertEvents(_.unmounted("a"), _.mounted("c"), _.unmounted("b"), _.mounted("d")).clear()

    // --

    EventBus.emit(
      bus1 -> spanD,
      bus2 -> spanE
    )

    expectNode(
      div of (
        sentinel,
        span of "d",
        sentinel,
        span of "e"
      )
    )
    // #1 steals D from #2 seamlessly (displacing C -> unmount C); #2, now empty, mounts a fresh E.
    tracker.assertEvents(_.unmounted("c"), _.mounted("e")).clear()

    // --

    bus1.emit(spanD)
    bus2.emit(spanC)

    expectNode(
      div of (
        sentinel,
        span of "d",
        sentinel,
        span of "c"
      )
    )
    // #1 is already showing D (a no-op); #2 self-replaces E->C.
    tracker.assertEvents(_.unmounted("e"), _.mounted("c")).clear()

    // --

    bus2.emit(spanD)
    bus1.emit(spanC)

    expectNode(
      div of (
        sentinel,
        span of "c",
        sentinel,
        span of "d"
      )
    )
    // Remove-first ordering: #2 steals D from #1 seamlessly (displacing C -> unmount C), then #1
    // mounts a fresh C. C is torn down and re-mounted because the two emits are separate steps.
    tracker.assertEvents(_.unmounted("c"), _.mounted("c"))
  }

  it("`child <--` swap and steal: lifecycle events on the classic single-node path") {
    // The classic single-node path (ChildInserter.switchToChild), asserted for lifecycle:
    //  - a self-replace SWAP tears the old node down BEFORE mounting the new one, so a
    //    `child <--` update has one predictable ordering, whatever it switches from.
    //  - STEALING the node another `child <--` shows relocates it WITHOUT re-mounting:
    //    only the displaced destination node unmounts; the stolen node keeps its mount
    //    across the move between receivers.
    val tracker = createEventTracker()
    val spanA = tracker.createSpan("a")
    val spanB = tracker.createSpan("b")
    val spanY = tracker.createSpan("y")

    val bus1 = new EventBus[HtmlElement]
    val bus2 = new EventBus[HtmlElement]
    mount(div(child <-- bus1, child <-- bus2))
    tracker.clear() // drop the element-create logs from the tracked spans above

    withClue("emit a into receiver #1: mounts:") {
      bus1.emit(spanA)
      expectNode(div of (sentinel, span of "a", sentinel))
      tracker.assertEvents(_.mounted("a")).clear()
    }

    withClue("emit b into receiver #1: swap unmounts the old node before mounting the new one:") {
      bus1.emit(spanB)
      expectNode(div of (sentinel, span of "b", sentinel))
      tracker.assertEvents(_.unmounted("a"), _.mounted("b")).clear()
    }

    withClue("emit y into receiver #2: mounts alongside, no effect on #1:") {
      bus2.emit(spanY)
      expectNode(div of (sentinel, span of "b", sentinel, span of "y"))
      tracker.assertEvents(_.mounted("y")).clear()
    }

    withClue("steal y into #1: #2's slot empties; the stolen node keeps its mount, only b unmounts:") {
      bus1.emit(spanY)
      expectNode(div of (sentinel, span of "y", sentinel))
      tracker.assertEvents(_.unmounted("b")).clear()
    }
  }

  it("re-emitting a `child <--` steals its node back from another binding (last write wins)") {
    // The single-node analog of the `children <--` re-steal. switchToChild detects that its
    // last-seen child was moved away (no longer at the sentinel) and re-inserts it, so re-emitting
    // the same node after a theft re-steals it — last-write-wins — with no re-mount. (The
    // replaceChild `old eq new` early-return only applies when the node was NOT moved.)
    val tracker = createEventTracker()
    val a = tracker.createSpan("a")

    val bus1 = new EventBus[HtmlElement]
    val bus2 = new EventBus[HtmlElement]
    val host1 = div(child <-- bus1)
    val host2 = div(child <-- bus2)
    mount(div(host1, host2))
    tracker.clear()

    withClue("emit a into #1:") {
      bus1.emit(a)
      a.ref.parentNode shouldBe host1.ref
      tracker.assertEvents(_.mounted("a")).clear()
    }

    withClue("#2 steals a add-first, WITHOUT #1 re-emitting, so #1's last-seen goes stale:") {
      bus2.emit(a)
      a.ref.parentNode shouldBe host2.ref
      tracker.assertNoEvents.clear()
    }

    withClue("#1 re-emits the SAME node a: it steals a back, no re-mount:") {
      bus1.emit(a)
      a.ref.parentNode shouldBe host1.ref
      tracker.assertNoEvents.clear()
      // `child <--` has only a leading sentinel (no trailing one).
      expectNode(div of (
        div of (sentinel, span of "a"),
        div of sentinel
      ))
    }
  }

  it("re-emitting a `text <--` steals its TextNode back from another binding (last write wins)") {
    // A TextNode handed to `text <--` is created OUTSIDE the inserter, so another binding can hold
    // and steal it – so `text <--` routes such nodes through ChildInserter (see ChildTextReceiver),
    // NOT through ChildTextInserter's fast path (which assumes its text nodes can never be stolen).
    // This pins the guarantee that path buys: a TextNode moved away by another binding is re-stolen
    // when this one re-emits it, with no duplicate node left behind. Text-node analog of the
    // `child <--` re-steal above.
    val node = TextNode("x")

    val bus1 = new EventBus[TextNode]
    val bus2 = new EventBus[TextNode]
    val host1 = div(text <-- bus1.events)
    val host2 = div(text <-- bus2.events)
    mount(div(host1, host2))

    withClue("emit the node into #1:") {
      bus1.emit(node)
      node.ref.parentNode shouldBe host1.ref
      expectNode(div of (div of (sentinel, "x"), div of sentinel))
    }

    withClue("#2 steals the node add-first, WITHOUT #1 re-emitting, so #1's last-seen goes stale:") {
      bus2.emit(node)
      node.ref.parentNode shouldBe host2.ref
      expectNode(div of (div of sentinel, div of (sentinel, "x")))
    }

    withClue("#1 re-emits the SAME node: it steals it back, no duplicate node:") {
      bus1.emit(node)
      node.ref.parentNode shouldBe host1.ref
      expectNode(div of (div of (sentinel, "x"), div of sentinel))
    }
  }

  it("re-emitting a `text.maybe <--` steals its TextNode back from another binding (last write wins)") {
    // Same steal guarantee for the optional receiver: `text.maybe <-- Observable[Option[TextNode]]`
    // also routes through ChildInserter (mapping None to a placeholder comment), because the nodes
    // are external and stealable – the ChildTextInserter.option fast path is NOT used for them.
    // Re-emitting Some(sameNode) after a theft must re-steal it back, with no duplicate node.
    val node = TextNode("x")

    val bus1 = new EventBus[Option[TextNode]]
    val bus2 = new EventBus[Option[TextNode]]
    val host1 = div(text.maybe <-- bus1.events)
    val host2 = div(text.maybe <-- bus2.events)
    mount(div(host1, host2))

    withClue("emit Some(node) into #1:") {
      bus1.emit(Some(node))
      node.ref.parentNode shouldBe host1.ref
      expectNode(div of (div of (sentinel, "x"), div of sentinel))
    }

    withClue("#2 steals the node add-first via Some(node), WITHOUT #1 re-emitting:") {
      bus2.emit(Some(node))
      node.ref.parentNode shouldBe host2.ref
      expectNode(div of (div of sentinel, div of (sentinel, "x")))
    }

    withClue("#1 re-emits Some(SAME node): it steals it back, no duplicate node:") {
      bus1.emit(Some(node))
      node.ref.parentNode shouldBe host1.ref
      expectNode(div of (div of (sentinel, "x"), div of sentinel))
    }
  }

  // ----------------------------------------------------------------------------------
  // 2. Classic `children <--` plain-element moves (ChildrenInserter.updateChildren)
  // ----------------------------------------------------------------------------------

  it("can move children from one dynamic list to another") {

    val tracker = createEventTracker()
    val spanA = tracker.createSpan("a")
    val spanB = tracker.createSpan("b")
    val spanC = tracker.createSpan("c")
    val spanD = tracker.createSpan("d")
    val spanE = tracker.createSpan("e")
    val spanF = tracker.createSpan("f")

    val bus1 = new EventBus[List[HtmlElement]]
    val bus2 = new EventBus[List[HtmlElement]]

    val el = div(
      children <-- bus1,
      span("--"),
      children <-- bus2,
    )

    mount(el)
    tracker.clear() // drop the upfront element-create logs

    // --

    expectNode(
      div of (
        sentinel,
        span of "--",
        sentinel
      )
    )
    tracker.assertNoEvents.clear()

    // --

    EventBus.emit(
      bus1 -> List(spanA, spanB, spanC),
      bus2 -> List(spanD, spanE, spanF)
    )

    expectNode(
      div of (
        sentinel,
        span of "a",
        span of "b",
        span of "c",
        sentinel,
        span of "--",
        sentinel,
        span of "d",
        span of "e",
        span of "f",
        sentinel
      )
    )
    tracker
      .assertEvents(
        _.mounted("a"),
        _.mounted("b"),
        _.mounted("c"),
        _.mounted("d"),
        _.mounted("e"),
        _.mounted("f")
      )
      .clear()

    EventBus.emit(
      bus1 -> List(spanA),
      bus2 -> List(spanD)
    )

    expectNode(
      div of (
        sentinel,
        span of "a",
        sentinel,
        span of "--",
        sentinel,
        span of "d",
        sentinel,
      )
    )
    tracker
      .assertEvents(
        _.unmounted("b"),
        _.unmounted("c"),
        _.unmounted("e"),
        _.unmounted("f")
      )
      .clear()

    // --

    EventBus.emit(
      bus1 -> List(spanA, spanD),
      bus2 -> List(spanE)
    )

    expectNode(
      div of (
        sentinel,
        span of "a",
        span of "d",
        sentinel,
        span of "--",
        sentinel,
        span of "e",
        sentinel,
      )
    )
    // Add-first steal: L1 grabs D (bus1 emitted first, D still in L2) seamlessly; L2 then mounts a
    // fresh E in D's place. Only E is a new mount.
    tracker.assertEvents(_.mounted("e")).clear()

    // --

    EventBus.emit(
      bus1 -> List(spanA),
      bus2 -> List(spanE, spanD)
    )

    expectNode(
      div of (
        sentinel,
        span of "a",
        sentinel,
        span of "--",
        sentinel,
        span of "e",
        span of "d",
        sentinel,
      )
    )
    // Remove-first ordering: bus1 emitted first drops D from L1 (unmount), then bus2 re-adds it to
    // L2 (mount). D is torn down and re-mounted because the two emits are separate steps.
    tracker.assertEvents(_.unmounted("d"), _.mounted("d")).clear()

    // --

    EventBus.emit(
      bus1 -> List(spanD, spanA),
      bus2 -> List(spanE)
    )

    expectNode(
      div of (
        sentinel,
        span of "d",
        span of "a",
        sentinel,
        span of "--",
        sentinel,
        span of "e",
        sentinel,
      )
    )
    // Add-first steal again: bus1 grabs D into L1 while it is still in L2, then bus2's removal of D
    // is a no-op. Fully seamless — no events.
    tracker.assertNoEvents.clear()

    // --

    EventBus.emit(
      bus1 -> List(spanF, spanC),
      bus2 -> List(spanE, spanA, spanD)
    )

    expectNode(
      div of (
        sentinel,
        span of "f",
        span of "c",
        sentinel,
        span of "--",
        sentinel,
        span of "e",
        span of "a",
        span of "d",
        sentinel,
      )
    )
    // L1 [d,a] -> [f,c]: f, c mount fresh and d, a leave L1 (bus1 processed first). L2 [e] -> [e,a,d]
    // then re-mounts a, d — remove-first, so the pair churns rather than transferring seamlessly.
    tracker
      .assertEvents(
        _.mounted("f"),
        _.mounted("c"),
        _.unmounted("d"),
        _.unmounted("a"),
        _.mounted("a"),
        _.mounted("d")
      )
      .clear()

    // --

    EventBus.emit(
      bus1 -> List(spanF, spanA, spanC, spanD),
      bus2 -> List(spanE)
    )

    expectNode(
      div of (
        sentinel,
        span of "f",
        span of "a",
        span of "c",
        span of "d",
        sentinel,
        span of "--",
        sentinel,
        span of "e",
        sentinel,
      )
    )
    // Add-first steal: bus1 grabs A, D into L1 while they are still in L2, then bus2's removal of
    // them is a no-op. Seamless — no events.
    tracker.assertNoEvents.clear()

    // --

    EventBus.emit(
      bus1 -> List(spanE),
      bus2 -> List(spanF, spanA, spanC, spanD)
    )

    expectNode(
      div of (
        sentinel,
        span of "e",
        sentinel,
        span of "--",
        sentinel,
        span of "f",
        span of "a",
        span of "c",
        span of "d",
        sentinel
      )
    )
    // Mixed: E transfers L2 -> L1 seamlessly (add-first, bus1 grabbed it while still in L2), while
    // F, A, C, D move L1 -> L2 remove-first (bus1 dropped them before bus2 re-added them), so that
    // group is torn down and re-mounted. Hence events for f,a,c,d but none for e.
    tracker.assertEvents(
      _.unmounted("f"),
      _.unmounted("a"),
      _.unmounted("c"),
      _.unmounted("d"),
      _.mounted("f"),
      _.mounted("a"),
      _.mounted("c"),
      _.mounted("d")
    )
  }

  it("moving elements within / between classic `children <--` lists never re-mounts them") {
    // The classic reconcile (ChildrenInserter.updateChildren) routes reorders and steals
    // through addToDynamicList, so relocating a plain element must not tear its DOM
    // span down and re-add it. We pin this via lifecycle events: reorders and an
    // add-first steal fire NOTHING, while a genuine removal DOES unmount — proving the
    // moves are real no-ops, not luck.
    val tracker = createEventTracker()
    val spanA = tracker.createSpan("a")
    val spanB = tracker.createSpan("b")
    val spanC = tracker.createSpan("c")

    val bus1 = new EventBus[List[HtmlElement]]
    val bus2 = new EventBus[List[HtmlElement]]
    mount(div(children <-- bus1, span("--"), children <-- bus2))
    tracker.clear() // drop the element-create logs from the tracked spans above

    withClue("fill L1 with a, b, c:") {
      bus1.emit(List(spanA, spanB, spanC))
      expectNode(
        div of (sentinel, span of "a", span of "b", span of "c", sentinel, span of "--", sentinel)
      )
      tracker.assertEvents(_.mounted("a"), _.mounted("b"), _.mounted("c")).clear()
    }

    withClue("reorder within L1 (c, a, b): a pure move re-mounts nothing:") {
      bus1.emit(List(spanC, spanA, spanB))
      expectNode(
        div of (sentinel, span of "c", span of "a", span of "b", sentinel, span of "--", sentinel)
      )
      tracker.assertNoEvents.clear()
    }

    withClue("steal a into L2 add-first, then drop it from L1: the element transfers, no re-mount:") {
      bus2.emit(List(spanA)) // add to L2 while still in L1 -> transfer
      bus1.emit(List(spanC, spanB)) // removal from L1 is a no-op: already stolen
      expectNode(
        div of (sentinel, span of "c", span of "b", sentinel, span of "--", sentinel, span of "a", sentinel)
      )
      tracker.assertNoEvents.clear()
    }

    withClue("genuine removal of b from L1 DOES unmount (the moves above were real no-ops):") {
      bus1.emit(List(spanC))
      expectNode(
        div of (sentinel, span of "c", sentinel, span of "--", sentinel, span of "a", sentinel)
      )
      tracker.assertEvents(_.unmounted("b")).clear()
    }
  }

  it("re-emitting an unslotted static group steals its child back from another list (last write wins)") {
    // The no-slot analog of the slotted static-group re-steal in SlotReconciliationRegressionSpec.
    // A `Seq[Node]` list item is transparent to the diff, so re-emitting the group re-adopts a
    // child another list took — last-write-wins — with no re-mount. Confirms the behaviour is not
    // slot-specific: it rides the same leaf diff whether or not a Slot wraps the group.
    val tracker = createEventTracker()
    val a = tracker.createSpan("a")
    val b = tracker.createSpan("b")
    val group: Inserter = List(a, b)

    val bus1 = new EventBus[List[Inserter]]
    val bus2 = new EventBus[List[HtmlElement]]
    val host1 = div(children <-- bus1)
    val host2 = div(children <-- bus2)
    mount(div(host1, host2))
    tracker.clear()

    withClue("fill L1 with the group [a, b]:") {
      bus1.emit(List(group))
      tracker.assertEvents(_.mounted("a"), _.mounted("b")).clear()
      // L2 has never emitted yet, so it has only its leading sentinel.
      expectNode(div of (
        div of (sentinel, span of "a", span of "b", sentinel),
        div of sentinel
      ))
    }

    withClue("L2 steals b add-first, WITHOUT L1 re-emitting, so L1's contentMap goes stale:") {
      bus2.emit(List(b))
      b.ref.parentNode shouldBe host2.ref
      tracker.assertNoEvents.clear()
    }

    withClue("L1 re-emits the SAME group: it steals b back, no re-mount:") {
      bus1.emit(List(group))
      b.ref.parentNode shouldBe host1.ref
      tracker.assertNoEvents.clear()
      expectNode(div of (
        div of (sentinel, span of "a", span of "b", sentinel),
        div of (sentinel, sentinel)
      ))
    }
  }

  // ----------------------------------------------------------------------------------
  // 3. Dynamic inserter reordered WITHIN one `children <--` list
  // ----------------------------------------------------------------------------------

  it("reordering never re-mounts items: static and dynamic inserters move without re-running") {
    // A move must relocate an item's DOM span WITHOUT tearing it down
    // and re-adding it: the logical parent is unchanged, so hooks/subscriptions must not re-run.
    // We pin this via lifecycle events – zero mount/unmount for any moved item, covering BOTH the
    // DYNAMIC item's override (its content span) AND the STATIC items' base-class move (the pure
    // static swap at the end) – plus observeCount for the dynamic item's subscription (paused, not
    // rebuilt on a move).
    val tracker = createEventTracker()
    var observeCount = 0
    val dynVar = Var("d0")
    val itemsVar = Var[List[Inserter]](Nil)

    val staticA = tracker.createSpan("A")
    val staticB = tracker.createSpan("B")
    val dyn: Inserter = child <-- dynVar.signal.map { v =>
      observeCount += 1
      tracker.createSpan(v)
    }
    tracker.clear()

    withClue("initial: A, dynamic (d0), B:") {
      itemsVar.set(List(staticA, dyn, staticB))
      mount(div("H", children <-- itemsVar.signal))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "d0", sentinel, span of "B", sentinel))
      observeCount shouldBe 1
      tracker
        .assertEvents(
          _.mounted("A"),
          _.elementCreated("d0"),
          _.mounted("d0"),
          _.mounted("B")
        )
        .clear()
    }

    withClue("move the dynamic item to the front (backward move) – no re-mount, observer must NOT re-run:") {
      itemsVar.set(List(dyn, staticA, staticB))
      expectNode(div.of("H", sentinel, sentinel, span of "d0", sentinel, span of "A", span of "B", sentinel))
      observeCount shouldBe 1
      tracker.assertNoEvents.clear()
    }

    withClue("move the dynamic item to the back (forward move, past both statics) – still no re-mount / re-run:") {
      itemsVar.set(List(staticA, staticB, dyn))
      expectNode(div.of("H", sentinel, span of "A", span of "B", sentinel, span of "d0", sentinel, sentinel))
      observeCount shouldBe 1
      tracker.assertNoEvents.clear()
    }

    withClue("dynamic item is still live after being moved twice (updates in place, old content swaps out):") {
      dynVar.set("d1")
      expectNode(div.of("H", sentinel, span of "A", span of "B", sentinel, span of "d1", sentinel, sentinel))
      observeCount shouldBe 2
      // A genuine update (not a move): the new content mounts and the old unmounts.
      tracker
        .assertEvents(
          _.elementCreated("d1"),
          _.unmounted("d0"),
          _.mounted("d1")
        )
        .clear()
    }

    withClue("swap the two static neighbours (pure static-inserter moves) – no re-mount:") {
      itemsVar.set(List(staticB, staticA, dyn))
      expectNode(div.of("H", sentinel, span of "B", span of "A", sentinel, span of "d1", sentinel, sentinel))
      observeCount shouldBe 2
      tracker.assertNoEvents
    }
  }

  it("multi-node dynamic span moves forward and backward, of varying length (no re-mount)") {
    // Exercises NestedGroup.moveTo's same-parent branch: the whole nested span (leading
    // sentinel .. content nodes .. trailing sentinel) is relocated as a unit. Each move must relocate
    // the span WITHOUT re-mounting any of its content or the static neighbour it passes (asserted as
    // zero lifecycle events per move). We grow / shrink the span between moves so the internal walk
    // covers spans of different lengths; node identity is preserved across those resizes so every
    // resize is a clean add-only / remove-only step.
    val tracker = createEventTracker()
    val innerVar = Var[List[Node]](Nil)
    val itemsVar = Var[List[Inserter]](Nil)

    val staticA = tracker.createSpan("A")
    val n1 = tracker.createSpan("n1")
    val n2 = tracker.createSpan("n2")
    val n3 = tracker.createSpan("n3")
    val n4 = tracker.createSpan("n4")
    val n5 = tracker.createSpan("n5")
    tracker.clear()

    val nested: Inserter = children <-- innerVar.signal

    withClue("initial: A, then the nested span holding n1, n2:") {
      innerVar.set(List(n1, n2))
      itemsVar.set(List(staticA, nested))
      mount(div("H", children <-- itemsVar.signal))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n1", span of "n2", sentinel, sentinel))
      tracker
        .assertEvents(
          _.mounted("A"),
          _.mounted("n1"),
          _.mounted("n2")
        )
        .clear()
    }

    withClue("move the (2-node) span backward to the front – relocates as a unit, nothing re-mounts:") {
      itemsVar.set(List(nested, staticA))
      expectNode(div.of("H", sentinel, sentinel, span of "n1", span of "n2", sentinel, span of "A", sentinel))
      tracker.assertNoEvents.clear()
    }

    withClue("move it forward again (back behind A) – still no re-mount:") {
      itemsVar.set(List(staticA, nested))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n1", span of "n2", sentinel, sentinel))
      tracker.assertNoEvents.clear()
    }

    withClue("grow the span to three content nodes (only the new node mounts):") {
      innerVar.set(List(n1, n2, n3))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n1", span of "n2", span of "n3", sentinel, sentinel))
      tracker.assertEvents(_.mounted("n3")).clear()
    }

    withClue("move the grown (3-node) span backward – the whole span relocates, no re-mount:") {
      itemsVar.set(List(nested, staticA))
      expectNode(div.of("H", sentinel, sentinel, span of "n1", span of "n2", span of "n3", sentinel, span of "A", sentinel))
      tracker.assertNoEvents.clear()
    }

    withClue("shrink the span to a single content node while moved (the trailing two unmount):") {
      innerVar.set(List(n1))
      expectNode(div.of("H", sentinel, sentinel, span of "n1", sentinel, span of "A", sentinel))
      // #Note: `children <--` tears down in contentMap insertion order (n2, n3).
      tracker
        .assertEvents(
          _.unmounted("n2"),
          _.unmounted("n3")
        )
        .clear()
    }

    withClue("move the shrunken (1-node) span forward – no re-mount:") {
      itemsVar.set(List(staticA, nested))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n1", sentinel, sentinel))
      tracker.assertNoEvents.clear()
    }

    withClue("inner list is still live after all the moves (grow again, keeping n1):") {
      innerVar.set(List(n1, n4, n5))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n1", span of "n4", span of "n5", sentinel, sentinel))
      tracker.assertEvents(
        _.mounted("n4"),
        _.mounted("n5")
      )
    }
  }

  // ----------------------------------------------------------------------------------
  // 4. Dynamic inserter moved BETWEEN two `children <--` lists (steal / remove-first / #163)
  // ----------------------------------------------------------------------------------

  //  A `children <--` list item CAN be a dynamic inserter, and the SAME inserter `val`
  //  can be referenced by two different lists, exactly like a plain element `val` can.
  //
  //  Two orderings arise when the same instance leaves list 1 and joins list 2:
  //    - remove-first: list 1 emits Nil, then list 2 emits List(item)   (separate transactions)
  //    - add-first ("steal"): list 2 emits List(item) while item is still in list 1, then
  //                           list 1 emits Nil
  //
  //  The REFERENCE test below pins how plain ELEMENTS behave, which is the bar the TODO
  //  refers to ("similarly to how we can transfer elements").
  // #TODO probe(addFirst = false) should be solved by https://github.com/raquo/Laminar/issues/163
  it("REFERENCE: plain ELEMENT moved between two `children <--` lists (both orderings)") {
    // Establishes the target semantics for the inserter case below.
    def probe(addFirst: Boolean): (Int, Int) = {
      var mountCount = 0
      var unmountCount = 0
      val items1 = Var[List[Inserter]](Nil)
      val items2 = Var[List[Inserter]](Nil)
      val el: Inserter = span(
        "X",
        onMountCallback(_ => mountCount += 1),
        onUnmountCallback(_ => unmountCount += 1)
      )
      mount(
        div(
          div("L1", children <-- items1.signal),
          div("L2", children <-- items2.signal)
        )
      )
      items1.set(List(el))
      if (addFirst) {
        items2.set(List(el)) // steal into L2 while still in L1
        items1.set(Nil)
      } else {
        items1.set(Nil)
        items2.set(List(el))
      }
      // In both orderings the element lands in L2 and is gone from L1.
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, span of "X", sentinel)
        )
      )
      val result = (mountCount, unmountCount)
      unmount()
      result
    }

    // Remove-first re-mounts (two separate transactions – nothing links them).
    probe(addFirst = false) shouldBe (2, 1) // #TODO https://github.com/raquo/Laminar/issues/163
    // Add-first is a true transfer: the element is stolen into L2 with NO re-mount.
    probe(addFirst = true) shouldBe (1, 0)
  }

  it("CHARACTERIZATION (remove-first): same dynamic inserter moved between two lists re-mounts, like an element") {
    // #TODO Review this when working on https://github.com/raquo/Laminar/issues/163 too
    // Documents CURRENT behaviour, which is consistent with the element reference above:
    // the DOM span is unmounted from L1 and re-mounted into L2 (mount hooks re-run).
    // The inner `child <--` observer does NOT re-run here only because Var.signal.map
    // memoizes its value across the brief teardown; a non-memoizing source would re-run.
    var observeCount = 0
    var mountCount = 0
    var unmountCount = 0
    val valueVar = Var("A")
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val dyn: Inserter = child <-- valueVar.signal.map { v =>
      observeCount += 1
      span(
        v,
        onMountCallback(_ => mountCount += 1),
        onUnmountCallback(_ => unmountCount += 1)
      )
    }

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    items1.set(List(dyn))
    withClue("in L1:") {
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "A", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      (observeCount, mountCount, unmountCount) shouldBe (1, 1, 0)
    }

    // Remove-first ordering (two transactions).
    items1.set(Nil)
    items2.set(List(dyn))
    withClue("moved to L2 (re-mounted):") {
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "A", sentinel, sentinel)
        )
      )
      // observe stays 1 (Signal value memoized); the span DOM node re-mounts: unmount 1, mount 2.
      (observeCount, mountCount, unmountCount) shouldBe (1, 2, 1)
    }

    withClue("still live in L2:") {
      valueVar.set("B")
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "B", sentinel, sentinel)
        )
      )
      observeCount shouldBe 2
    }
  }

  it("CHARACTERIZATION (issue #163): moving an element between two sibling `child <--` bindings re-mounts in one direction only") {
    // Shape of https://github.com/raquo/Laminar/issues/163: ONE element shows via one of
    // two independent `child <--` bindings (different parents), toggled by one signal.
    // Whether the move re-mounts is ORDER-DEPENDENT: the open #163 hazard, and why the
    // issue notes a `delaySync` workaround only fixes one direction.
    //
    // Mechanism: the two bindings react to the same signal in subscription (declaration)
    // order, so the first-declared binding fires first. On a toggle one binding LOSES the
    // element and one GAINS it. In DomTree.replaceChild the losing side runs
    // elem.willSetParent(None), and ReactiveElement.isUnmounting unmounts it only if it
    // has no other active parent yet:
    //  - GAINING binding fires FIRST: it steals the element while the old parent is still
    //    active, so the pilot owner is transferred (setOwner), not cleared -> NO events;
    //  - LOSING binding fires FIRST: the element is detached to None (unmount) before the
    //    gaining binding re-attaches it (mount) -> it RE-MOUNTS.
    // So it re-mounts exactly when the first-declared binding is the one losing it.
    //
    // `probe` mounts a fresh pair (declaration order = `topDeclaredFirst`), does one
    // toggle, and returns the events it produced. When #163 is fixed, all four go to Nil.
    def probe(topDeclaredFirst: Boolean, startOnTop: Boolean): List[String] = {
      val tracker = createEventTracker()
      val onTop = Var(startOnTop)
      val elem = tracker.createDiv("elem")
      val topOff = span("off-top")
      val bottomOff = span("off-bottom")
      val topBox = div("top", child <-- onTop.signal.map(t => if (t) elem else topOff))
      val bottomBox = div("bottom", child <-- onTop.signal.map(t => if (t) bottomOff else elem))
      mount(div(if (topDeclaredFirst) Seq(topBox, bottomBox) else Seq(bottomBox, topBox)))
      tracker.clear() // drop element-create + initial mount; keep only the toggle's events
      onTop.set(!startOnTop)
      val toggleLog = tracker.log.toList
      unmount()
      toggleLog
    }

    val reMount = List("unmount:elem", "mount:elem")

    withClue("top declared first, toggle top->bottom (top LOSES first) — re-mounts:") {
      probe(topDeclaredFirst = true, startOnTop = true) shouldBe reMount
    }
    withClue("top declared first, toggle bottom->top (top GAINS first) — seamless:") {
      probe(topDeclaredFirst = true, startOnTop = false) shouldBe Nil
    }
    withClue("bottom declared first, toggle top->bottom (bottom GAINS first) — seamless:") {
      probe(topDeclaredFirst = false, startOnTop = true) shouldBe Nil
    }
    withClue("bottom declared first, toggle bottom->top (bottom LOSES first) — re-mounts:") {
      probe(topDeclaredFirst = false, startOnTop = false) shouldBe reMount
    }
  }

  it("add-first / steal: same dynamic inserter added to a 2nd list before removal transfers, no re-mount") {
    // This is the case the Inserter.scala TODO called out. For a plain element (see REFERENCE)
    // this "steal" ordering transfers with NO re-mount. DynamicInserter.addToDynamicList now
    // does the same: it transfers the group's span + subscriptions to the new parent instead of
    // throwing, and the later removal from L1 is a no-op because the span was already stolen.
    var observeCount = 0
    var mountCount = 0
    var unmountCount = 0
    val valueVar = Var("A")
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val dyn: Inserter = child <-- valueVar.signal.map { v =>
      observeCount += 1
      span(
        v,
        onMountCallback(_ => mountCount += 1),
        onUnmountCallback(_ => unmountCount += 1)
      )
    }

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    items1.set(List(dyn))
    (observeCount, mountCount, unmountCount) shouldBe (1, 1, 0)

    withClue("steal into L2 before removing from L1:") {
      items2.set(List(dyn)) // adds to L2 while still in L1 -> transfer (not throw, not re-mount)
      items1.set(Nil) // removal from L1 is a no-op: the span was already stolen into L2
      // Matches the element reference: item is now in L2, gone from L1, transferred with no re-mount.
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "A", sentinel, sentinel)
        )
      )
      (observeCount, mountCount, unmountCount) shouldBe (1, 1, 0)
    }

    withClue("still live in L2 after the steal:") {
      valueVar.set("B")
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "B", sentinel, sentinel)
        )
      )
      observeCount shouldBe 2
    }
  }

  it("add-first / steal of a nested `children <--` item (multi-node span transfers, inner list stays live)") {
    // Exercises the recursive part of moveToParent: the stolen item is itself a `children <--`
    // group with several content nodes and its own inner trailing sentinel. The whole span must
    // move to L2 as a unit (asserted seamless — zero lifecycle events on the steal), and the inner
    // list must remain live afterwards, its grow/shrink landing at the NEW host. Nodes are prebuilt
    // with stable identity (like the multi-node reorder test), so each resize is a clean add-only /
    // remove-only step rather than a rebuild.
    val tracker = createEventTracker()
    val n1 = tracker.createSpan("n1")
    val n2 = tracker.createSpan("n2")
    val n3 = tracker.createSpan("n3")
    val n7 = tracker.createSpan("n7")
    tracker.clear() // drop the upfront element-create logs

    val innerVar = Var[List[Node]](List(n1, n2))
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val nested: Inserter = children <-- innerVar.signal

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    // Sentinel layout for a nested `children <--` item inside an outer `children <--` list:
    //   [outer-leading, group-leading, ...content..., trailing, outer-trailing]
    withClue("in L1: the nested group renders its two content nodes:") {
      items1.set(List(nested))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "n1", span of "n2", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      tracker.assertEvents(_.mounted("n1"), _.mounted("n2")).clear()
    }

    withClue("steal into L2 (whole multi-node span moves) before removing from L1 — seamless:") {
      items2.set(List(nested))
      items1.set(Nil)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "n1", span of "n2", sentinel, sentinel)
        )
      )
      tracker.assertNoEvents.clear() // transferred as a unit, not rebuilt
    }

    withClue("inner list still live in L2 (grow), directing emissions to the new parent:") {
      innerVar.set(List(n1, n2, n3))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "n1", span of "n2", span of "n3", sentinel, sentinel)
        )
      )
      tracker.assertEvents(_.mounted("n3")).clear() // clean add-only: only the new node mounts
    }

    withClue("inner list still live in L2 (shrink to a single, previously-absent node):") {
      innerVar.set(List(n7))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "n7", sentinel, sentinel)
        )
      )
      // #Note: `children <--` mounts the new node first, then tears the old ones down in contentMap order.
      tracker
        .assertEvents(
          _.mounted("n7"),
          _.unmounted("n1"),
          _.unmounted("n2"),
          _.unmounted("n3")
        )
        .clear()
    }

    withClue("normal removal from its new host L2 tears it down:") {
      items2.set(Nil)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      tracker.assertEvents(_.unmounted("n7"))
    }
  }

  it("add-first / steal keeps the item's per-item lifecycle intact (owner transferred, not rebuilt)") {
    // The stolen item's observer must NOT re-run (its owner is transferred, not torn down and
    // rebuilt), and after the steal the item must react to a source that changed WHILE it was
    // being moved – proving the same live subscription is still in place.
    var observeCount = 0
    val valueVar = Var("A")
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val dyn: Inserter = child <-- valueVar.signal.map { v =>
      observeCount += 1
      span(v)
    }

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    items1.set(List(dyn))
    observeCount shouldBe 1

    withClue("steal into L2:") {
      items2.set(List(dyn))
      items1.set(Nil)
      observeCount shouldBe 1 // owner transferred, observer not re-run
    }

    withClue("reacts in L2 to further updates:") {
      valueVar.set("B")
      observeCount shouldBe 2
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "B", sentinel, sentinel)
        )
      )
    }
  }

  it("add-first / steal recurses through nested dynamic content (depth-2: children <-- containing child <--)") {
    // The stolen item is a `children <--` whose single content item is itself a `child <--`
    // (a nested DynamicInserter). moveToParent must recurse into that inner group, transferring
    // its own subscription owner. We verify the depth-2 leaf stays live (no re-run) after the steal.
    var leafObserveCount = 0
    val leafVar = Var("x")
    val leaf: Inserter = child <-- leafVar.signal.map { v =>
      leafObserveCount += 1
      span(v)
    }
    val innerItemsVar = Var[List[Inserter]](List(leaf))
    val nested: Inserter = children <-- innerItemsVar.signal

    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    items1.set(List(nested))
    withClue("depth-2 nesting in L1:") {
      // L1 sentinels: outer-leading, nested-group-leading, leaf-group-leading, <span>,
      //   leaf-trailing, nested-trailing, outer-trailing (each group has a single trailing sentinel)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, sentinel, span of "x", sentinel, sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      leafObserveCount shouldBe 1
    }

    withClue("steal the depth-2 span into L2:") {
      items2.set(List(nested))
      items1.set(Nil)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, sentinel, span of "x", sentinel, sentinel, sentinel)
        )
      )
      leafObserveCount shouldBe 1 // inner-inner owner transferred, leaf observer not re-run
    }

    withClue("depth-2 leaf still live in L2:") {
      leafVar.set("y")
      leafObserveCount shouldBe 2
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, sentinel, span of "y", sentinel, sentinel, sentinel)
        )
      )
    }
  }

  it("add-first / steal at depth-3 (children <-- containing children <-- containing child <--): recursion has no depth-specific assumptions") {
    // A THIRD level of nesting is stolen as a unit: `moveToParent` recurses through all three groups,
    // transferring the leaf's owner rather than rebuilding it, so the recursion assumes no fixed
    // depth. Emptying and refilling the middle span at depth-3 checks the nested empty span stays
    // anchored at the moved location.
    val tracker = createEventTracker()
    val leafVar = Var("x")
    val midVar = Var[List[Inserter]](Nil)
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)

    val leaf: Inserter = child <-- leafVar.signal.map(tracker.createSpan(_))
    val inner: Inserter = children <-- midVar.signal // depth-2 group whose items are themselves inserters
    val outer: Inserter = children <-- Var[List[Inserter]](List(inner)).signal // depth-1 group holding `inner`

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    withClue("depth-3 nesting in L1 (each of the 3 groups adds a leading + trailing sentinel):") {
      items1.set(List(outer))
      midVar.set(List(leaf))
      // L1: outer-lead, mid-lead, inner-lead, leaf-lead, <span x>, leaf-trail, inner-trail, mid-trail, outer-trail
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, sentinel, sentinel, span of "x", sentinel, sentinel, sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(
          _.elementCreated("x"),
          _.mounted("x")
        )
        .clear()
    }

    withClue("steal the whole depth-3 span into L2 before removing from L1 – no re-mount at any level:") {
      items2.set(List(outer))
      items1.set(Nil)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, sentinel, sentinel, span of "x", sentinel, sentinel, sentinel, sentinel)
        )
      )
      tracker.assertNoEvents.clear() // transferred, not rebuilt
    }

    withClue("depth-3 leaf still live in L2 (its subscription moved with it):") {
      leafVar.set("y")
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, sentinel, sentinel, span of "y", sentinel, sentinel, sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(
          _.elementCreated("y"),
          _.unmounted("x"),
          _.mounted("y")
        )
        .clear()
    }

    withClue("empty the depth-2 middle span to zero at the moved location, then refill – stays anchored:") {
      midVar.set(Nil)
      // The `inner` group collapses to its bare [leading, trailing] pair, nested inside `outer`.
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, sentinel, sentinel, sentinel, sentinel)
        )
      )
      tracker.assertEvents(_.unmounted("y")).clear()

      midVar.set(List(child <-- leafVar.signal.map(tracker.createSpan(_))))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, sentinel, sentinel, span of "y", sentinel, sentinel, sentinel, sentinel)
        )
      )
      tracker.assertEvents(
        _.elementCreated("y"),
        _.mounted("y")
      )
    }
  }

  // ----------------------------------------------------------------------------------
  // 4b. Steal-BACK from a sibling: same-parent vs cross-parent (both layouts)
  // ----------------------------------------------------------------------------------

  // The stale-re-emit re-steal ("last write wins"), run against BOTH the same-parent and
  // cross-parent layouts via `TwoLists`. L2 steals the item add-first (L1's contentMap goes stale),
  // then L1 re-emits WITH the item and steals it back. For same-parent siblings this exercises
  // `NestedGroup.moveTo`'s same-parent branch: the two lists share the parent ELEMENT — hence the
  // same mount owner — so a raw reposition (without `moveToParent`'s owner transfer) is correct, and
  // the item's live subscription must survive the re-steal. Each choreography asserts the re-steal is
  // seamless (no re-mount / no re-render) and the item stays live at its home afterwards.

  List[Layout](SameParent, CrossParent).foreach { layout =>

    it(s"[${layout.name}] re-emitting a list steals a plain ELEMENT back from a sibling (last write wins)") {
      val tracker = createEventTracker()
      val e = tracker.createSpan("E")
      tracker.clear()
      val f = new TwoLists(layout)
      f.items1.set(List(e))
      mount(f.root)
      tracker.assertEvents(_.mounted("E")).clear()

      withClue("L2 steals E add-first, so L1's contentMap goes stale: ") {
        f.items2.set(List(e))
        tracker.assertNoEvents.clear()
      }
      withClue("L1 re-emits WITH E and steals it back, no re-mount: ") {
        f.items1.set(List(e))
        tracker.assertNoEvents.clear()
        f.expectRoot(list1 = List(span of "E"), list2 = List())
      }
    }

    it(s"[${layout.name}] re-emitting a list steals a nested `children <--` GROUP back from a sibling (subscription stays live)") {
      // The sharp DynamicInserter case: the re-stolen item is a multi-node nested group. The
      // same-parent re-steal runs the raw-reposition branch; the group's inner subscription must
      // stay live, proven by appending content after the re-steal (it lands in L1, no re-mount).
      val tracker = createEventTracker()
      val a = tracker.createSpan("A")
      val b = tracker.createSpan("B")
      tracker.clear()
      val inner = Var[List[Node]](List(a))
      val nested: Inserter = children <-- inner.signal
      val f = new TwoLists(layout)
      f.items1.set(List(nested))
      mount(f.root)
      tracker.assertEvents(_.mounted("A")).clear()

      withClue("L2 steals the whole group add-first, so L1's map goes stale: ") {
        f.items2.set(List(nested))
        tracker.assertNoEvents.clear()
      }
      withClue("L1 re-emits WITH the group and steals it back, no re-mount: ") {
        f.items1.set(List(nested))
        tracker.assertNoEvents.clear()
      }
      withClue("the group's subscription is still live: new content lands in L1: ") {
        inner.set(List(a, b))
        tracker.assertEvents(_.mounted("B")).clear()
        f.expectRoot(
          list1 = List(sentinel, span of "A", span of "B", sentinel),
          list2 = List()
        )
      }
    }

    it(s"[${layout.name}] re-emitting a list steals a single-node `child <--` item back from a sibling (owner not rebuilt)") {
      // A `child <--` list item keeps a (sticky) trailing sentinel, so its span is [lead, node,
      // trail]. The re-steal must NOT re-run its observer (owner preserved), then it must react to a
      // later update at its home.
      val tracker = createEventTracker()
      var observeCount = 0
      val valueVar = Var("x")
      val dyn: Inserter = child <-- valueVar.signal.map { v =>
        observeCount += 1
        tracker.createSpan(v)
      }
      val f = new TwoLists(layout)
      f.items1.set(List(dyn))
      mount(f.root)
      tracker.assertEvents(_.elementCreated("x"), _.mounted("x")).clear()
      observeCount shouldBe 1

      withClue("L2 steals it add-first, so L1's contentMap goes stale: ") {
        f.items2.set(List(dyn))
        tracker.assertNoEvents.clear()
      }
      withClue("L1 re-emits WITH it and steals it back, no re-render (owner preserved): ") {
        f.items1.set(List(dyn))
        tracker.assertNoEvents.clear()
        observeCount shouldBe 1
      }
      withClue("a later update lands at its home in L1 (subscription stayed live): ") {
        valueVar.set("y")
        observeCount shouldBe 2
        tracker.assertEvents(_.elementCreated("y"), _.unmounted("x"), _.mounted("y")).clear()
        f.expectRoot(list1 = List(sentinel, span of "y", sentinel), list2 = List())
      }
    }

    it(s"[${layout.name}] re-emitting a list steals a `text <--` item back from a sibling (subscription transferred, not re-run)") {
      val tracker = createEventTracker()
      val textVar = Var("hi")
      val dynText: Inserter = tracker.text("t", textVar.signal)
      val f = new TwoLists(layout)
      f.items1.set(List(dynText))
      mount(f.root)
      tracker.assertEvents(_.textUpdated("t", "hi")).clear()

      withClue("L2 steals it add-first, so L1's contentMap goes stale: ") {
        f.items2.set(List(dynText))
        tracker.assertNoEvents.clear() // seamless transfer, no re-render
      }
      withClue("L1 re-emits WITH it and steals the live text span back, no re-render: ") {
        f.items1.set(List(dynText))
        tracker.assertNoEvents.clear()
      }
      withClue("a later update lands at its home in L1 (the subscription moved with it): ") {
        textVar.set("bye")
        tracker.assertEvents(_.textUpdated("t", "bye")).clear()
        f.expectRoot(list1 = List(sentinel, ExpectedNode.textNode, sentinel), list2 = List())
      }
    }

    it(s"[${layout.name}] re-emitting a list steals a `children.command <--` item back from a sibling (context follows home)") {
      // Multi-node command group. After the re-steal, later commands must anchor on the group's
      // (moved-back) sentinels in L1 — proving the item's insert context, not just its DOM nodes,
      // followed it home.
      val tracker = createEventTracker()
      val cmdBus = new EventBus[CollectionCommand[Node]]
      val cmd: Inserter = children.command <-- cmdBus.events
      val f = new TwoLists(layout)
      f.items1.set(List(cmd))
      mount(f.root)
      cmdBus.emit(CollectionCommand.Append(tracker.createDiv("a")))
      cmdBus.emit(CollectionCommand.Append(tracker.createDiv("b")))
      tracker.assertEvents(
        _.elementCreated("a"), _.mounted("a"),
        _.elementCreated("b"), _.mounted("b")
      ).clear()

      withClue("L2 steals it add-first, so L1's contentMap goes stale: ") {
        f.items2.set(List(cmd))
        tracker.assertNoEvents.clear()
      }
      withClue("L1 re-emits WITH it and steals the whole command span back, no re-mount: ") {
        f.items1.set(List(cmd))
        tracker.assertNoEvents.clear()
      }
      withClue("a later Append anchors on the moved-back span in L1: ") {
        cmdBus.emit(CollectionCommand.Append(tracker.createDiv("c")))
        tracker.assertEvents(_.elementCreated("c"), _.mounted("c")).clear()
        f.expectRoot(
          list1 = List(sentinel, div of "a", div of "b", div of "c", sentinel),
          list2 = List()
        )
      }
    }
  }

  // ----------------------------------------------------------------------------------
  // 4b-bis. A steal followed by the origin's OWN removal never tears the stolen group down
  // ----------------------------------------------------------------------------------

  // The complement of 4b (which re-emits WITH the item to steal it back): here the loser list
  // re-emits WITHOUT the stolen group, so its reconcile would call `removeFromDynamicList` on it.
  // `DynamicInserter.removeFromDynamicList` must never tear down a group another host now owns.
  // It relies on comparing `leadingSentinel.parentNode == parent.ref`, and the safety splits by
  // layout:
  //  - CROSS-parent steal: the group's leading sentinel moved under the thief's parent ELEMENT, so
  //    the parents differ and the method takes its explicit no-op branch.
  //  - SAME-parent steal: parents still match, so the no-op branch does NOT catch it — but the
  //    method is never even called on the group. The origin list walks its span sentinel-to-sentinel
  //    stepping over each item's whole span, and a same-parent-stolen group is no longer a top-level
  //    node of that span: a sibling thief moved it into the sibling's span, and a child thief nested
  //    it inside that child's span (stepped over via `lastNode`). Either way the walk never lands on
  //    it, so no removal is attempted. Both cases must leave the group live at its new host.

  List[Layout](SameParent, CrossParent).foreach { layout =>

    it(s"[${layout.name}] a sibling steal then the origin's removal leaves the stolen group live (no teardown)") {
      val tracker = createEventTracker()
      val a = tracker.createSpan("A")
      val b = tracker.createSpan("B")
      tracker.clear()
      val inner = Var[List[Node]](List(a))
      val nested: Inserter = children <-- inner.signal
      val f = new TwoLists(layout)
      f.items1.set(List(nested))
      mount(f.root)
      tracker.assertEvents(_.mounted("A")).clear()

      withClue("L2 steals the whole group add-first, so L1's map goes stale: ") {
        f.items2.set(List(nested))
        tracker.assertNoEvents.clear()
      }
      withClue("L1 re-emits Nil: its removal must NOT tear the group down (L2 owns it now): ") {
        f.items1.set(Nil)
        tracker.assertNoEvents.clear()
        f.expectRoot(list1 = List(), list2 = List(sentinel, span of "A", sentinel))
      }
      withClue("the group is still live at its new home L2: later content lands there: ") {
        inner.set(List(a, b))
        tracker.assertEvents(_.mounted("B")).clear()
        f.expectRoot(list1 = List(), list2 = List(sentinel, span of "A", span of "B", sentinel))
      }
    }
  }

  it("a child inserter steal then the origin's removal keeps the group live inside that child (no teardown)") {
    // The same-parent case #2 from `removeFromDynamicList`'s `#Note`: the thief is a nested
    // `children <--` that is ITSELF an item of the origin list, and it absorbs a sibling group G into
    // its own span. G's leading sentinel then sits inside the child's span, still under the origin's
    // parent element. When the origin re-emits without G, its walk steps over the whole child span in
    // one jump, so it never reaches G — and G survives, still owned by the child.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val innerG = Var[List[Node]](List(a))
    val g: Inserter = children <-- innerG.signal
    val innerC = Var[List[Inserter]](Nil)
    val c: Inserter = children <-- innerC.signal
    val items1 = Var[List[Inserter]](List(c, g))

    mount(div(children <-- items1.signal))
    tracker.assertEvents(_.mounted("A")).clear()

    withClue("child C steals G into its own span add-first (no re-mount): ") {
      innerC.set(List(g))
      tracker.assertNoEvents.clear()
      // L1: [ C[ G[A] ] ] — G's span now nested inside C's span, still under L1's parent element.
      expectNode(
        div.of(
          sentinel, // L1 lead
          sentinel, sentinel, span of "A", sentinel, sentinel, // C-lead, G[lead, A, trail], C-trail
          sentinel // L1 trail
        )
      )
    }
    withClue("L1 re-emits WITHOUT G (keeps C): G must survive inside C, no unmount: ") {
      items1.set(List(c))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(sentinel, sentinel, sentinel, span of "A", sentinel, sentinel, sentinel)
      )
    }
    withClue("G is still live inside C: later content lands there: ") {
      innerG.set(List(a, b))
      tracker.assertEvents(_.mounted("B")).clear()
      expectNode(
        div.of(sentinel, sentinel, sentinel, span of "A", span of "B", sentinel, sentinel, sentinel)
      )
    }
  }

  // ----------------------------------------------------------------------------------
  // 4c. A moved span relocates exactly its LIVE DOM span (DOM order + departed nodes left behind)
  // ----------------------------------------------------------------------------------

  // `moveToParent` (the transfer behind a cross-parent steal) must relocate the nodes ACTUALLY in
  // the group's span right now, in the order they sit in the DOM — never the order or membership of
  // the tracking `contentMap`, which is only a lookup index and can lag the DOM two ways:
  //  - ORDER: `children.command <--` Prepend / Insert / Replace build the DOM out of insertion
  //    order, so the map lists nodes in a different order than they appear in the DOM.
  //  - MEMBERSHIP: another host can steal a node out of the span (a sibling list, or the group's
  //    own nested `child <--`), leaving a stale map entry the move must NOT drag back.
  // These use cross-parent layouts on purpose: that is the layout routed through `moveToParent`.
  // (The same-parent reposition path — which already reads the DOM — is covered in section 4b.)

  it("a stolen `children.command <--` span preserves its DOM order (Prepend / Insert are not re-appended)") {
    // The command group tracks nodes in COMMAND order (a, b, c) while the DOM holds them b, a, c.
    // Stealing the group must move the DOM span as it stands, not rebuild it from the tracking map.
    val tracker = createEventTracker()
    val cmdBus = new EventBus[CollectionCommand[Node]]
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val cmd: Inserter = children.command <-- cmdBus.events

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    withClue("build b, a, c out of command order (Append a, Prepend b, Insert c @2):") {
      items1.set(List(cmd))
      cmdBus.emit(CollectionCommand.Append(tracker.createDiv("a")))
      cmdBus.emit(CollectionCommand.Prepend(tracker.createDiv("b")))
      cmdBus.emit(CollectionCommand.Insert(tracker.createDiv("c"), atIndex = 2))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, div of "b", div of "a", div of "c", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(
          _.elementCreated("a"),
          _.mounted("a"),
          _.elementCreated("b"),
          _.mounted("b"),
          _.elementCreated("c"),
          _.mounted("c")
        )
        .clear()
    }

    withClue("steal into L2 (add-first): the span moves in DOM order b, a, c, no re-mount:") {
      items2.set(List(cmd))
      items1.set(Nil) // removal from L1 is a no-op: already stolen
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "b", div of "a", div of "c", sentinel, sentinel)
        )
      )
      tracker.assertNoEvents.clear() // transferred as a unit, not rebuilt
    }

    withClue("post-move commands anchor on the moved span and its preserved order:") {
      cmdBus.emit(CollectionCommand.Insert(tracker.createDiv("d"), atIndex = 1)) // b,a,c -> b,d,a,c
      cmdBus.emit(CollectionCommand.Prepend(tracker.createDiv("e"))) // -> e,b,d,a,c
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "e", div of "b", div of "d", div of "a", div of "c", sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(
          _.elementCreated("d"),
          _.mounted("d"),
          _.elementCreated("e"),
          _.mounted("e")
        )
        .clear()
    }
  }

  it("a stolen `children.command <--` span preserves its DOM order after a Replace") {
    // Replace(old, new) swaps a node in place in the DOM but appends `new` to the END of the
    // tracking map, so map order (a, c, x) diverges from DOM order (a, x, c). The move must keep
    // the DOM position.
    val tracker = createEventTracker()
    val cmdBus = new EventBus[CollectionCommand[Node]]
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val cmd: Inserter = children.command <-- cmdBus.events
    val a = tracker.createDiv("a")
    val b = tracker.createDiv("b")
    val c = tracker.createDiv("c")
    val x = tracker.createDiv("x")
    tracker.clear() // drop the upfront element-create logs

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    withClue("build a, b, c then Replace b -> x, giving DOM a, x, c:") {
      items1.set(List(cmd))
      cmdBus.emit(CollectionCommand.Append(a))
      cmdBus.emit(CollectionCommand.Append(b))
      cmdBus.emit(CollectionCommand.Append(c))
      cmdBus.emit(CollectionCommand.Replace(b, x))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, div of "a", div of "x", div of "c", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(
          _.mounted("a"),
          _.mounted("b"),
          _.mounted("c"),
          _.unmounted("b"), // #Note: Replace unmounts the old node, then mounts the new one
          _.mounted("x")
        )
        .clear()
    }

    withClue("steal into L2: the span moves in DOM order a, x, c, no re-mount:") {
      items2.set(List(cmd))
      items1.set(Nil)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "a", div of "x", div of "c", sentinel, sentinel)
        )
      )
      tracker.assertNoEvents.clear()
    }
  }

  it("moving a nested `children <--` group relocates only the nodes still in its span (sibling-stolen nodes stay put)") {
    // A sibling list steals nodes OUT of the group (last write wins); the group's map keeps stale
    // entries for them. Moving the group must relocate only what remains in its span, in DOM order,
    // and never drag a departed node back from its new host.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    val c = tracker.createSpan("C")
    val d = tracker.createSpan("D")
    tracker.clear()
    val inner = Var[List[Inserter]](List(a, b, c, d))
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val items3 = Var[List[Inserter]](Nil)
    val nested: Inserter = children <-- inner.signal

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal),
        div("L3", children <-- items3.signal)
      )
    )

    withClue("the nested group renders A, B, C, D in L1:") {
      items1.set(List(nested))
      tracker
        .assertEvents(_.mounted("A"), _.mounted("B"), _.mounted("C"), _.mounted("D"))
        .clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "A", span of "B", span of "C", span of "D", sentinel, sentinel),
          div.of("L2", sentinel, sentinel),
          div.of("L3", sentinel, sentinel)
        )
      )
    }

    withClue("L2 steals B (middle) and D (end) out of the group (no re-mount):") {
      items2.set(List(b, d))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "A", span of "C", sentinel, sentinel),
          div.of("L2", sentinel, span of "B", span of "D", sentinel),
          div.of("L3", sentinel, sentinel)
        )
      )
    }

    withClue("L3 steals the group: only A, C travel (in DOM order); B, D stay in L2:") {
      items3.set(List(nested))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, span of "B", span of "D", sentinel),
          div.of("L3", sentinel, sentinel, span of "A", span of "C", sentinel, sentinel)
        )
      )
    }
  }

  it("moving a nested `children <--` group whose entire content was stolen relocates an empty span") {
    // The degenerate membership case: every content node has left the span. The move must relocate
    // just the (empty) span's sentinels, leaving all the departed nodes with their new host.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val inner = Var[List[Inserter]](List(a, b))
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val items3 = Var[List[Inserter]](Nil)
    val nested: Inserter = children <-- inner.signal

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal),
        div("L3", children <-- items3.signal)
      )
    )

    withClue("the nested group renders A, B in L1:") {
      items1.set(List(nested))
      tracker.assertEvents(_.mounted("A"), _.mounted("B")).clear()
    }

    withClue("L2 steals both A and B out of the group, emptying its span (no re-mount):") {
      items2.set(List(a, b))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, sentinel, sentinel),
          div.of("L2", sentinel, span of "A", span of "B", sentinel),
          div.of("L3", sentinel, sentinel)
        )
      )
    }

    withClue("L3 steals the now-empty group: only its sentinels travel; A, B stay in L2:") {
      items3.set(List(nested))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, span of "A", span of "B", sentinel),
          div.of("L3", sentinel, sentinel, sentinel, sentinel)
        )
      )
    }
  }

  it("moving a nested `children <--` group keeps a node its own nested `child <--` absorbed inside that child's span") {
    // A node stolen INTO a sibling item WITHIN the group (the group's own `child.maybe <--` absorbs
    // the next item's span) must travel as part of that child on the group move, not be pulled back
    // out to where the group's list last tracked it. A trailing plain item pins sibling ordering.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val z = tracker.createSpan("Z")
    tracker.clear()
    val innerChild = Var[Option[Span]](None)
    val innerDyn: Inserter = child.maybe <-- innerChild.signal
    val inner = Var[List[Inserter]](List(innerDyn, a, z))
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val nested: Inserter = children <-- inner.signal

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    withClue("the group renders [empty child.maybe, A, Z] in L1:") {
      items1.set(List(nested))
      tracker.assertEvents(_.mounted("A"), _.mounted("Z")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, sentinel, emptyCommentNode, sentinel, span of "A", span of "Z", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("the inner child.maybe absorbs A into its own span (no re-mount):") {
      innerChild.set(Some(a))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, sentinel, span of "A", sentinel, span of "Z", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("L2 steals the group: A stays inside the child.maybe's span, Z keeps its place:") {
      items2.set(List(nested))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, sentinel, span of "A", sentinel, span of "Z", sentinel, sentinel)
        )
      )
    }
  }

  it("last-write-wins survives a partial-span move: a moved group still reclaims a node a sibling stole") {
    // The move relocates only the LIVE span (leaving the stolen node with its thief H) — but it must
    // not sever the inner list's claim on that node. When the inner list re-emits, it steals the node
    // back to the group's NEW host (last write wins), proving the move left tracking intact. This is
    // the mirror of the section 4b re-steal-back tests, but with the group RELOCATED in between.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val inner = Var[List[Inserter]](List(a, b))
    val items1 = Var[List[Inserter]](Nil) // group's original home
    val itemsH = Var[List[Inserter]](Nil) // sibling thief of B
    val items3 = Var[List[Inserter]](Nil) // group's new home
    val nested: Inserter = children <-- inner.signal

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("H", children <-- itemsH.signal),
        div("L3", children <-- items3.signal)
      )
    )

    withClue("the group renders A, B in L1:") {
      items1.set(List(nested))
      tracker.assertEvents(_.mounted("A"), _.mounted("B")).clear()
    }

    withClue("H steals B out of the group (no re-mount); L1's map keeps a stale B entry:") {
      itemsH.set(List(b))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "A", sentinel, sentinel),
          div.of("H", sentinel, span of "B", sentinel),
          div.of("L3", sentinel, sentinel)
        )
      )
    }

    withClue("L3 steals the group: only A travels; B stays with H (no re-mount):") {
      items3.set(List(nested))
      items1.set(Nil)
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("H", sentinel, span of "B", sentinel),
          div.of("L3", sentinel, sentinel, span of "A", sentinel, sentinel)
        )
      )
    }

    withClue("the inner list re-emits [A, B]: B is reclaimed from H to the new host L3 (last write wins), no re-mount:") {
      inner.set(List(a, b))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("H", sentinel, sentinel), // B reclaimed away from H
          div.of("L3", sentinel, sentinel, span of "A", span of "B", sentinel, sentinel)
        )
      )
    }
  }

  // ----------------------------------------------------------------------------------
  // 4d. Re-placing a torn-down group: no live span -> rebuild + re-mount (like a removed element)
  // ----------------------------------------------------------------------------------

  // The counterpart of 4c's "move the live span": when there is NO live span, rebuild it.
  // After another host STEALS a dynamic inserter and then GENUINELY removes it, the group is torn
  // down (`nestedGroupOpt` cleared). But the original list still tracks the inserter in its
  // `contentMap` (it never re-emitted), so its next re-emission of that inserter routes to
  // `addToDynamicList` (with nothing to move). This must NOT fail on the stale tracking — it
  // must place the inserter afresh: re-insert + re-mount, exactly like re-adding a plain element
  // that had been removed. The list's item count already counted this inserter (it was in the
  // previous map), so a rebuild changes no count, while a genuinely new sibling still does.

  it("re-emitting a single-node `child <--` a sibling stole then removed re-adds it, like an element") {
    // Reference: a plain element stolen by L2, removed by L2, then re-emitted by L1 is simply
    // re-inserted and re-mounted. A dynamic inserter must behave the same, even though L1's last
    // emission still tracks it. Hits `updateChildren`'s in-range move branch.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val dyn: Inserter = child <-- Val(a)

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    withClue("initial placement in L1:") {
      items1.set(List(dyn))
      tracker.assertEvents(_.mounted("A")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "A", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("L2 steals the item, then drops it: the content unmounts exactly once:") {
      items2.set(List(dyn))
      items2.set(Nil)
      tracker.assertEvents(_.unmounted("A")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("L1 re-emits the item: it is placed afresh and its content mounts again:") {
      withCollectedAirstreamErrors { errors =>
        items1.set(List(dyn))
        assert(errors.isEmpty, s"re-emitting the item reported: ${errors.mkString("; ")}")
      }
      tracker.assertEvents(_.mounted("A")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "A", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }
  }

  it("re-emitting such a torn-down `child <--` after a retained element re-adds it after it") {
    // Same as above, but the re-emitted item comes after an element that stayed in place, so the
    // list has already run out of tracked DOM content when it reaches the item. Hits
    // `updateChildren`'s OVERFLOW branch instead of the in-range one.
    val tracker = createEventTracker()
    val e = tracker.createSpan("E")
    val a = tracker.createSpan("A")
    tracker.clear()
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val dyn: Inserter = child <-- Val(a)

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    withClue("initial placement in L1:") {
      items1.set(List(e, dyn))
      tracker.assertEvents(_.mounted("E"), _.mounted("A")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, span of "E", sentinel, span of "A", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("L2 steals the item, then drops it:") {
      items2.set(List(dyn))
      items2.set(Nil)
      tracker.assertEvents(_.unmounted("A")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, span of "E", sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("L1 re-emits [E, item]: E stays put, the item is placed afresh after it:") {
      withCollectedAirstreamErrors { errors =>
        items1.set(List(e, dyn))
        assert(errors.isEmpty, s"re-emitting the item reported: ${errors.mkString("; ")}")
      }
      tracker.assertEvents(_.mounted("A")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, span of "E", sentinel, span of "A", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }
  }

  it("re-emitting a torn-down multi-node `children <--` group rebuilds its whole span, then stays live") {
    // The rebuild is type-agnostic: a torn-down MULTI-node group re-emits its current inner value,
    // re-mounting the whole span. And the rebuilt group is genuinely live — a fresh subscription,
    // not a zombie — so the inner list's next emission reconciles into the new host.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    val c = tracker.createSpan("C")
    tracker.clear()
    val inner = Var[List[Inserter]](List(a, b))
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val nested: Inserter = children <-- inner.signal

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    withClue("the nested group renders A, B in L1:") {
      items1.set(List(nested))
      tracker.assertEvents(_.mounted("A"), _.mounted("B")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "A", span of "B", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("L2 steals the group, then drops it: the whole span unmounts:") {
      items2.set(List(nested))
      items2.set(Nil)
      tracker.assertEvents(_.unmounted("A"), _.unmounted("B")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("L1 re-emits the group: it is rebuilt and the whole span re-mounts:") {
      withCollectedAirstreamErrors { errors =>
        items1.set(List(nested))
        assert(errors.isEmpty, s"re-emitting the group reported: ${errors.mkString("; ")}")
      }
      tracker.assertEvents(_.mounted("A"), _.mounted("B")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "A", span of "B", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("the rebuilt group is live: the inner list emits [A, B, C], adding C in the new host:") {
      inner.set(List(a, b, c))
      tracker.assertEvents(_.mounted("C")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "A", span of "B", span of "C", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }
  }

  it("re-placing a torn-down group alongside a brand-new sibling keeps the list count correct") {
    // A single emission that mixes a REBUILD (the torn-down item, already counted -> no count
    // change) with a genuinely NEW item (count += 1). Both must land, in order, with no leftover
    // deletion miscounting the list.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val f = tracker.createSpan("F")
    tracker.clear()
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val dyn: Inserter = child <-- Val(a)

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    withClue("initial placement of the item in L1:") {
      items1.set(List(dyn))
      tracker.assertEvents(_.mounted("A")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "A", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("L2 steals the item, then drops it:") {
      items2.set(List(dyn))
      items2.set(Nil)
      tracker.assertEvents(_.unmounted("A")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("L1 re-emits [item, F]: the item is rebuilt (re-mounts A), F is newly added after it:") {
      withCollectedAirstreamErrors { errors =>
        items1.set(List(dyn, f))
        assert(errors.isEmpty, s"re-emitting reported: ${errors.mkString("; ")}")
      }
      tracker.assertEvents(_.mounted("A"), _.mounted("F")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "A", sentinel, span of "F", sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }
  }

  // ----------------------------------------------------------------------------------
  // 4e. A leaving nested item releases the nodes that the same emission keeps (no re-mount)
  // ----------------------------------------------------------------------------------

  // When a list re-emits WITHOUT a nested dynamic item, but WITH a node (or a nested inserter)
  // that currently lives inside that item, the node is un-nested seamlessly: the leaving item is
  // torn down around it, and the node is re-parented into the list – like moving a plain element
  // out of a wrapper that's being removed, within the same parent. This must hold regardless of
  // where the kept node lands relative to the leaving item, and at any nesting depth.

  List(
    (
      "after", // position
      (a: Div, b: Div) => List(a, b), // nextItems
      List[Rule](sentinel, div of "a", div of "b", sentinel) // expectedDom
    ),
    (
      "before", // position
      (a: Div, b: Div) => List(b, a), // nextItems
      List[Rule](sentinel, div of "b", div of "a", sentinel) // expectedDom
    )
  ).foreach { case (position, nextItems, expectedDom) =>

    it(s"un-nesting: (nested `child <-- b`, a) -> b $position a keeps b mounted, and the nested inserter is dead") {
      val tracker = createEventTracker()
      val a = tracker.createDiv("a")
      val b = tracker.createDiv("b")
      val c = tracker.createDiv("c")
      tracker.clear()

      val nestedVar = Var(b)
      val items = Var[List[Inserter]](List(child <-- nestedVar.signal, a))

      withClue("initial:") {
        mount(div(children <-- items.signal))
        expectNode(div.of(sentinel, sentinel, div of "b", sentinel, div of "a", sentinel))
        tracker
          .assertEvents(
            _.mounted("b"),
            _.mounted("a")
          )
          .clear()
      }

      withClue(s"the list drops the nested item, and places b directly, $position a:") {
        items.set(nextItems(a, b))
        expectNode(div.of(expectedDom: _*))
        tracker.assertNoEvents.clear()
      }

      withClue("the old nested `child <--` was torn down, so it no longer renders anything:") {
        nestedVar.set(c)
        expectNode(div.of(expectedDom: _*))
        tracker.assertNoEvents.clear()
      }
    }
  }

  it("un-nesting at depth 2: (nested `children <--` (c, nested `child <-- b`), a) -> (a, b) keeps b, unmounts c") {
    val tracker = createEventTracker()
    val a = tracker.createDiv("a")
    val b = tracker.createDiv("b")
    val c = tracker.createDiv("c")
    tracker.clear()

    val items = Var[List[Inserter]](List(children <-- Val(List[Inserter](c, child <-- Val(b))), a))

    withClue("initial:") {
      mount(div(children <-- items.signal))
      expectNode(
        div.of(
          sentinel, // outer list
          sentinel, // nested list
          div of "c",
          sentinel, div of "b", sentinel, // nested child
          sentinel, // nested list trailing
          div of "a",
          sentinel // outer list trailing
        )
      )
      tracker
        .assertEvents(
          _.mounted("c"),
          _.mounted("b"),
          _.mounted("a")
        )
        .clear()
    }

    withClue("both nested items are torn down around b, which stays mounted:") {
      items.set(List(a, b))
      expectNode(div.of(sentinel, div of "a", div of "b", sentinel))
      tracker
        .assertEvents(
          _.unmounted("c")
        )
        .clear()
    }
  }

  it("un-nesting a dynamic inserter: (nested `children <--` (c, nested `child <--` I), a) -> (a, I) keeps I live") {
    // The kept thing can be a nested inserter too (matched by its identity, not its content).
    // Its whole span is released from the leaving item, and it keeps rendering in its new place.
    val tracker = createEventTracker()
    val a = tracker.createDiv("a")
    val b = tracker.createDiv("b")
    val c = tracker.createDiv("c")
    val d = tracker.createDiv("d")
    tracker.clear()

    val innerVar = Var(b)
    val inner: Inserter = child <-- innerVar.signal
    val items = Var[List[Inserter]](List(children <-- Val(List[Inserter](c, inner)), a))

    withClue("initial:") {
      mount(div(children <-- items.signal))
      expectNode(
        div.of(
          sentinel, // outer list
          sentinel, // nested list
          div of "c",
          sentinel, div of "b", sentinel, // I
          sentinel, // nested list trailing
          div of "a",
          sentinel // outer list trailing
        )
      )
      tracker
        .assertEvents(
          _.mounted("c"),
          _.mounted("b"),
          _.mounted("a")
        )
        .clear()
    }

    withClue("the nested list is torn down around I, which moves after a without re-mounting:") {
      items.set(List(a, inner))
      expectNode(div.of(sentinel, div of "a", sentinel, div of "b", sentinel, sentinel))
      tracker
        .assertEvents(
          _.unmounted("c")
        )
        .clear()
    }

    withClue("I is still live in its new place:") {
      innerVar.set(d)
      expectNode(div.of(sentinel, div of "a", sentinel, div of "d", sentinel, sentinel))
      tracker
        .assertEvents(
          _.unmounted("b"),
          _.mounted("d")
        )
        .clear()
    }
  }

  it("un-nesting several nodes out of several leaving items, reordered, unmounts only the dropped node") {
    // c lands BEFORE its leaving item (a steal at the cursor), while b and d land AFTER a, so
    // the list removes both leaving items before placing them (the early-removal path).
    val tracker = createEventTracker()
    val a = tracker.createDiv("a")
    val b = tracker.createDiv("b")
    val c = tracker.createDiv("c")
    val d = tracker.createDiv("d")
    val e = tracker.createDiv("e")
    tracker.clear()

    val items = Var[List[Inserter]](List(
      children <-- Val(List(b, c, e)),
      child <-- Val(d),
      a
    ))

    withClue("initial:") {
      mount(div(children <-- items.signal))
      expectNode(
        div.of(
          sentinel, // outer list
          sentinel, div of "b", div of "c", div of "e", sentinel, // nested list
          sentinel, div of "d", sentinel, // nested child
          div of "a",
          sentinel // outer list trailing
        )
      )
      tracker
        .assertEvents(
          _.mounted("b"),
          _.mounted("c"),
          _.mounted("e"),
          _.mounted("d"),
          _.mounted("a")
        )
        .clear()
    }

    withClue("(c, a, d, b): only e, which is dropped, unmounts:") {
      items.set(List(c, a, d, b))
      expectNode(div.of(sentinel, div of "c", div of "a", div of "d", div of "b", sentinel))
      tracker
        .assertEvents(
          _.unmounted("e")
        )
        .clear()
    }
  }

  // Similar to https://github.com/raquo/Laminar/issues/163
  it("CHARACTERIZATION: re-wrapping a nested node into a NEW nested inserter re-mounts it when the old item leaves first") {
    // (nested `child <-- b`, a) -> (a, NEW nested `child <-- b`): the list removes the leaving
    // item before it reaches the new one, and at that point nothing is known about the new
    // inserter's content – it's only rendered once the new item subscribes. So b is unmounted
    // with the leaving item, then mounted again by the new one.
    // #Note: known limitation – see "Known deviations" in notes/Inserters.md.
    val tracker = createEventTracker()
    val a = tracker.createDiv("a")
    val b = tracker.createDiv("b")
    tracker.clear()

    val items = Var[List[Inserter]](List(child <-- Val(b), a))

    withClue("initial:") {
      mount(div(children <-- items.signal))
      expectNode(div.of(sentinel, sentinel, div of "b", sentinel, div of "a", sentinel))
      tracker
        .assertEvents(
          _.mounted("b"),
          _.mounted("a")
        )
        .clear()
    }

    withClue("b is re-mounted:") {
      items.set(List(a, child <-- Val(b)))
      expectNode(div.of(sentinel, div of "a", sentinel, div of "b", sentinel, sentinel))
      tracker
        .assertEvents(
          _.unmounted("b"),
          _.mounted("b")
        )
        .clear()
    }

    withClue("reference: when the new inserter comes FIRST, it takes b before the old item leaves:") {
      items.set(List(child <-- Val(b), a))
      expectNode(div.of(sentinel, sentinel, div of "b", sentinel, div of "a", sentinel))
      tracker.assertNoEvents.clear()
    }
  }

  // ----------------------------------------------------------------------------------
  // 5. Promote / demote: static application <-> list, static <-> static
  // ----------------------------------------------------------------------------------

  it("promote a statically-applied dynamic inserter into a children<-- list, seamlessly (static-first, no re-mount)") {
    // The inserter's FIRST placement is a plain static apply: `div("HOST", nested)`. That creates
    // a LIGHTWEIGHT NestedGroup (single leading sentinel + content, NO trailing sentinel – same
    // minimal DOM as a plain static mount). Moving it into a list must be SEAMLESS: the group
    // grows a trailing sentinel (ensureTrailingSentinel) and `moveToParent` transfers its span +
    // subscription to the list, so HOST is left clean, the content ends up in the list, and NOTHING
    // re-mounts (mountCount/unmountCount are pinned across the promotion). The transfer rides on the
    // group's TransferableSubscription – no re-subscribe, no re-emit, no no-op-diff churn.
    var observeCount = 0
    var mountCount = 0
    var unmountCount = 0
    val innerVar = Var[List[Int]](List(1, 2))
    val items = Var[List[Inserter]](Nil)
    val nested: Inserter = children <-- innerVar.signal.map { ints =>
      observeCount += 1
      ints.map(i =>
        span(
          s"n$i",
          onMountCallback(_ => mountCount += 1),
          onUnmountCallback(_ => unmountCount += 1)
        )
      )
    }

    val host = div("HOST", nested) // <-- first placement is a plain static apply (lightweight group)
    val listEl = div("LIST", children <-- items.signal)

    mount(div(host, listEl))

    withClue("after mount (static-first): HOST holds the content, a lightweight 2 sentinels only:") {
      expectNode(
        div.of(
          div.of("HOST", sentinel, span of "n1", span of "n2", sentinel),
          div.of("LIST", sentinel, sentinel)
        )
      )
      (observeCount, mountCount, unmountCount) shouldBe (1, 2, 0)
    }

    items.set(List(nested)) // now move it into the list

    withClue("after promotion into the list: HOST is clean, content lives in the list as a group, no re-mount:") {
      expectNode(
        div.of(
          div.of("HOST"),
          div.of("LIST", sentinel, sentinel, span of "n1", span of "n2", sentinel, sentinel)
        )
      )
      // Seamless: the source signal never re-emitted (observeCount unchanged) and the spans were
      // transferred, not rebuilt (mount/unmount counts unchanged).
      (observeCount, mountCount, unmountCount) shouldBe (1, 2, 0)
    }

    withClue("still live in the list after promotion: updates flow, HOST stays clean:") {
      innerVar.set(List(3)) // only the group's (live) subscription should react now
      expectNode(
        div.of(
          div.of("HOST"),
          div.of("LIST", sentinel, sentinel, span of "n3", sentinel, sentinel)
        )
      )
      // n1, n2 unmounted, n3 mounted – the group's single subscription is what reacted.
      (observeCount, mountCount, unmountCount) shouldBe (2, 3, 2)
    }
  }

  it("move a statically-applied dynamic inserter between two plain elements, seamlessly (static-to-static)") {
    // A dynamic inserter applied directly to element A (never a list item), then re-applied to
    // element B via `B.amend(inserter)`. Both placements are lightweight (trailing-sentinel-less)
    // groups; the move is a seamless `moveToParent` that relocates the content to B and keeps it
    // live, WITHOUT re-mounting.
    var observeCount = 0
    var mountCount = 0
    var unmountCount = 0
    val valueVar = Var("A")
    val nested: Inserter = child <-- valueVar.signal.map { v =>
      observeCount += 1
      span(
        v,
        onMountCallback(_ => mountCount += 1),
        onUnmountCallback(_ => unmountCount += 1)
      )
    }

    val elA = div("A", nested)
    val elB = div("B")

    mount(div(elA, elB))

    withClue("after mount: content is in A (child <-- is a single sentinel + node):") {
      expectNode(
        div.of(
          div.of("A", sentinel, span of "A"),
          div.of("B")
        )
      )
      (observeCount, mountCount, unmountCount) shouldBe (1, 1, 0)
    }

    withClue("move to B via amend: the whole span (sentinel + node) relocates to B, no re-mount:") {
      elB.amend(nested)
      expectNode(
        div.of(
          div.of("A"),
          div.of("B", sentinel, span of "A")
        )
      )
      (observeCount, mountCount, unmountCount) shouldBe (1, 1, 0)
    }

    withClue("still live in B after the move:") {
      valueVar.set("Z")
      expectNode(
        div.of(
          div.of("A"),
          div.of("B", sentinel, span of "Z")
        )
      )
      // One re-render (A -> Z), so the old node unmounts and the new one mounts. Still just one
      // live subscription doing the work.
      (observeCount, mountCount, unmountCount) shouldBe (2, 2, 1)
    }
  }

  // list -> plain element ("demote"): an inserter that currently lives as a `children <--` list
  // item is applied directly to a plain element via `el.amend(inserter)`. This drives
  // DynamicInserter.apply's move branch out of the list-item state (the group already has a trailing
  // sentinel). The span must relocate to the plain element and stay live, with no re-mount; the old
  // list is left clean.

  it("demote: a `children <--` item applied onto a plain element relocates seamlessly, no re-mount") {
    val tracker = createEventTracker()
    val innerVar = Var[List[String]](List("n1", "n2"))
    val items = Var[List[Inserter]](Nil)
    val nested: Inserter = children <-- innerVar.signal.map(_.map(tracker.createSpan(_)))

    val host = div("HOST")
    val listEl = div("LIST", children <-- items.signal)

    mount(div(host, listEl))

    items.set(List(nested))
    withClue("first placement: the item lives in LIST as a bracketed group:") {
      expectNode(
        div.of(
          div.of("HOST"),
          div.of("LIST", sentinel, sentinel, span of "n1", span of "n2", sentinel, sentinel)
        )
      )
      // The source renders [n1, n2]: both spans are built, then both mount.
      tracker
        .assertEvents(
          _.elementCreated("n1"),
          _.elementCreated("n2"),
          _.mounted("n1"),
          _.mounted("n2")
        )
        .clear()
    }

    withClue("demote onto the plain HOST element via amend: span relocates, LIST emptied, no re-mount:") {
      host.amend(nested)
      items.set(Nil) // removal from LIST is a no-op: the span was already moved to HOST
      expectNode(
        div.of(
          div.of("HOST", sentinel, span of "n1", span of "n2", sentinel),
          div.of("LIST", sentinel, sentinel)
        )
      )
      tracker
        .assertNoEvents // seamless: no re-render, no re-mount
        .clear()
    }

    withClue("still live on HOST after the demote — updates flow to the new location:") {
      innerVar.set(List("n3"))
      expectNode(
        div.of(
          div.of("HOST", sentinel, span of "n3", sentinel),
          div.of("LIST", sentinel, sentinel)
        )
      )
      // The (live) subscription reacts: builds n3, mounts it, and tears down the old content.
      tracker.assertEvents(
        _.elementCreated("n3"),
        _.mounted("n3"),
        _.unmounted("n1"),
        _.unmounted("n2")
      )
    }
  }

  // Single-node demote: a `child <--` (which normally needs NO trailing sentinel) keeps one once
  // it has been a `children <--` list item, because `_forceTrailingSentinel` is sticky (set when it
  // joined the list, never cleared). So demoting it onto a plain element yields a "heavyweight"
  // group [leading, node, trailing], NOT the lightweight [leading, node] shape a never-listed
  // `child <--` has. This pins that deliberate behaviour (the trailing sentinel is retained, not
  // dropped), and that the demote is still seamless (no re-mount).

  it("demote: a single-node `child <--` item retains its (sticky) trailing sentinel on a plain element, no re-mount") {
    val tracker = createEventTracker()
    val valueVar = Var("x")
    val items = Var[List[Inserter]](Nil)
    val dyn: Inserter = child <-- valueVar.signal.map(tracker.createSpan(_))

    val host = div("HOST")
    val listEl = div("LIST", children <-- items.signal)

    mount(div(host, listEl))

    items.set(List(dyn))
    withClue("as a list item, a `child <--` is forced to keep a trailing sentinel: [leading, node, trailing]:") {
      expectNode(
        div.of(
          div.of("HOST"),
          div.of("LIST", sentinel, sentinel, span of "x", sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(
          _.elementCreated("x"),
          _.mounted("x")
        )
        .clear()
    }

    withClue("demote onto HOST: span relocates, no re-mount, and the trailing sentinel is RETAINED (sticky):") {
      host.amend(dyn)
      items.set(Nil)
      // Contrast the never-listed static-to-static case, which is [leading, node] with NO trailing
      // sentinel: here the trailing sentinel survives the demotion because the flag never clears.
      expectNode(
        div.of(
          div.of("HOST", sentinel, span of "x", sentinel),
          div.of("LIST", sentinel, sentinel)
        )
      )
      tracker
        .assertNoEvents // seamless: no re-render, no re-mount
        .clear()
    }

    withClue("still live on HOST after the demote:") {
      valueVar.set("y")
      expectNode(
        div.of(
          div.of("HOST", sentinel, span of "y", sentinel),
          div.of("LIST", sentinel, sentinel)
        )
      )
      tracker.assertEvents(
        _.elementCreated("y"),
        _.unmounted("x"),
        _.mounted("y")
      )
    }
  }

  // A `children <--` list item is stolen out onto a plain element (`moveToParent`) WHILE EMPTY (bare
  // [leading, trailing], zero content). The empty span must relocate to the element, the list must be
  // left clean, and the first population after the move must land on the element between the moved
  // sentinels.

  it("demote: an EMPTY `children <--` list item applied onto a plain element relocates, then populates on the element") {
    val tracker = createEventTracker()
    val innerVar = Var[List[Node]](Nil)
    val items = Var[List[Inserter]](Nil)

    val n1 = tracker.createSpan("n1")
    val n2 = tracker.createSpan("n2")
    tracker.clear()

    val nested: Inserter = children <-- innerVar.signal

    val host = div("HOST")
    val listEl = div("LIST", children <-- items.signal)

    mount(div(host, listEl))

    withClue("place the empty item in LIST: bare leading + trailing sentinels, no content:") {
      items.set(List(nested))
      expectNode(
        div.of(
          div.of("HOST"),
          div.of("LIST", sentinel, sentinel, sentinel, sentinel)
        )
      )
      tracker.assertNoEvents.clear()
    }

    withClue("demote the EMPTY span onto HOST via amend: the bare pair relocates, LIST emptied, no events:") {
      host.amend(nested)
      items.set(Nil) // removal from LIST is a no-op: already moved
      // The sticky trailing sentinel travels too, so HOST holds a [leading, trailing] pair.
      expectNode(
        div.of(
          div.of("HOST", sentinel, sentinel),
          div.of("LIST", sentinel, sentinel)
        )
      )
      tracker.assertNoEvents.clear()
    }

    withClue("populate on HOST after the demote: content lands on HOST between the moved sentinels:") {
      innerVar.set(List(n1, n2))
      expectNode(
        div.of(
          div.of("HOST", sentinel, span of "n1", span of "n2", sentinel),
          div.of("LIST", sentinel, sentinel)
        )
      )
      tracker.assertEvents(
        _.mounted("n1"),
        _.mounted("n2")
      )
    }
  }

  // Stolen-node variants of the promote / demote above. A plainly-applied single-node `child <--`
  // has NO trailing sentinel, so its span end is derived from its tracked node. Once another list
  // steals that node (last-write-wins), the tracked entry is stale — the node lives elsewhere.
  // Promoting or demoting the inserter must then relocate an EMPTY span (bounded at its own leading
  // sentinel): it must NOT fail with a DOM NotFoundError, and must NOT drag the stolen node back.
  // This exercises `InsertContext.lastNodeInDom`, which trusts the tracked node only while it still
  // sits right after the sentinel and otherwise reports an empty span.

  it("promote a plain `child <--` whose node was stolen: an empty span moves into the list, the node stays with its thief") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    val items2 = Var[List[Inserter]](Nil)
    val items3 = Var[List[Inserter]](Nil)
    val dyn: Inserter = child <-- Val(a)

    mount(
      div(
        div("P", dyn),
        div("L2", children <-- items2.signal),
        div("L3", children <-- items3.signal)
      )
    )

    withClue("the plain `child <--` on P renders A (no trailing sentinel):") {
      tracker.assertEvents(_.mounted("A")).clear()
      expectNode(
        div.of(
          div.of("P", sentinel, span of "A"),
          div.of("L2", sentinel, sentinel),
          div.of("L3", sentinel, sentinel)
        )
      )
    }

    withClue("L2 steals A (no re-mount):") {
      items2.set(List(a))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("P", sentinel),
          div.of("L2", sentinel, span of "A", sentinel),
          div.of("L3", sentinel, sentinel)
        )
      )
    }

    withClue("L3 promotes the inserter: its empty span moves, A stays in L2, no re-mount:") {
      items3.set(List(dyn))
      tracker.assertNoEvents.clear()
      // The inserter's own leading sentinel travels with it, so P is left with only its text.
      expectNode(
        div.of(
          div.of("P"),
          div.of("L2", sentinel, span of "A", sentinel),
          div.of("L3", sentinel, sentinel, sentinel, sentinel)
        )
      )
    }
  }

  it("promote such a stolen `child <--`: a following static sibling is left in place") {
    // Bound the empty span at the inserter's own sentinel — placing the new trailing sentinel right
    // after the leading one — without swallowing a static sibling that follows it under P.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val tail = tracker.createSpan("TAIL")
    tracker.clear()
    val items2 = Var[List[Inserter]](Nil)
    val items3 = Var[List[Inserter]](Nil)
    val dyn: Inserter = child <-- Val(a)

    mount(
      div(
        div("P", dyn, tail),
        div("L2", children <-- items2.signal),
        div("L3", children <-- items3.signal)
      )
    )

    withClue("P renders A (no trailing sentinel), then its static TAIL sibling:") {
      tracker.assertEvents(_.mounted("A"), _.mounted("TAIL")).clear()
      expectNode(
        div.of(
          div.of("P", sentinel, span of "A", span of "TAIL"),
          div.of("L2", sentinel, sentinel),
          div.of("L3", sentinel, sentinel)
        )
      )
    }

    withClue("L2 steals A (no re-mount); TAIL is now the sentinel's next sibling:") {
      items2.set(List(a))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("P", sentinel, span of "TAIL"),
          div.of("L2", sentinel, span of "A", sentinel),
          div.of("L3", sentinel, sentinel)
        )
      )
    }

    withClue("L3 promotes the inserter: an empty span moves, A stays in L2, TAIL stays in P:") {
      items3.set(List(dyn))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("P", span of "TAIL"),
          div.of("L2", sentinel, span of "A", sentinel),
          div.of("L3", sentinel, sentinel, sentinel, sentinel)
        )
      )
    }
  }

  it("a promoted stolen `child <--` renders its next emission into the new host") {
    // Promoting leaves the group tracking a stolen node. This must not wedge the inserter: its next
    // emission clears the stale tracking and renders into the group's NEW parent, while the stolen
    // node stays with its thief.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    tracker.clear()
    val items2 = Var[List[Inserter]](Nil)
    val items3 = Var[List[Inserter]](Nil)
    val childVar = Var[Span](a)
    val dyn: Inserter = child <-- childVar.signal

    mount(
      div(
        div("P", dyn),
        div("L2", children <-- items2.signal),
        div("L3", children <-- items3.signal)
      )
    )

    withClue("P renders A:") {
      tracker.assertEvents(_.mounted("A")).clear()
      expectNode(
        div.of(
          div.of("P", sentinel, span of "A"),
          div.of("L2", sentinel, sentinel),
          div.of("L3", sentinel, sentinel)
        )
      )
    }

    withClue("L2 steals A, then L3 promotes the (now empty) inserter:") {
      items2.set(List(a))
      items3.set(List(dyn))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("P"),
          div.of("L2", sentinel, span of "A", sentinel),
          div.of("L3", sentinel, sentinel, sentinel, sentinel)
        )
      )
    }

    withClue("the inserter emits B: it renders inside the promoted span in L3, A stays in L2:") {
      childVar.set(b)
      tracker.assertEvents(_.mounted("B")).clear()
      expectNode(
        div.of(
          div.of("P"),
          div.of("L2", sentinel, span of "A", sentinel),
          div.of("L3", sentinel, sentinel, span of "B", sentinel, sentinel)
        )
      )
    }
  }

  it("move a plain `child <--` whose node was stolen onto another plain element: an empty span relocates") {
    // The demote/relocate path (`element.amend(inserter)` -> moveToParent) must also tolerate the
    // stolen tracked node: it relocates the empty live span, leaving the node with its thief.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    val items2 = Var[List[Inserter]](Nil)
    val dyn: Inserter = child <-- Val(a)
    val q = div("Q")

    mount(
      div(
        div("P", dyn),
        div("L2", children <-- items2.signal),
        q
      )
    )

    withClue("P renders A:") {
      tracker.assertEvents(_.mounted("A")).clear()
      expectNode(
        div.of(
          div.of("P", sentinel, span of "A"),
          div.of("L2", sentinel, sentinel),
          div.of("Q")
        )
      )
    }

    withClue("L2 steals A:") {
      items2.set(List(a))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("P", sentinel),
          div.of("L2", sentinel, span of "A", sentinel),
          div.of("Q")
        )
      )
    }

    withClue("amending Q with the inserter moves its empty span there, A stays in L2:") {
      q.amend(dyn)
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("P"),
          div.of("L2", sentinel, span of "A", sentinel),
          div.of("Q", sentinel)
        )
      )
    }
  }

  // ----------------------------------------------------------------------------------
  // 6. Inserter-TYPE matrix: `children.command <--` and `text <--` as moved items
  // ----------------------------------------------------------------------------------

  // A `children.command <--` item is a multi-node group (needsTrailingSentinel), so as a list
  // item it looks just like a nested `children <--`: [outer-leading, group-leading, ...content...,
  // group-trailing, outer-trailing]. Moving it must carry the whole span (both its own sentinels
  // and its content) to the new host, and subsequent commands must anchor on the MOVED trailing
  // sentinel — Append lands just before it, Prepend just after the group's leading sentinel.

  it("`children.command <--` stolen between two lists: later commands land correctly, no re-mount") {
    val tracker = createEventTracker()
    val cmdBus = new EventBus[CollectionCommand[Node]]
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val cmd: Inserter = children.command <-- cmdBus.events

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    items1.set(List(cmd))
    cmdBus.emit(CollectionCommand.Append(tracker.createDiv("a")))
    cmdBus.emit(CollectionCommand.Append(tracker.createDiv("b")))
    withClue("command group holds a, b in L1:") {
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, div of "a", div of "b", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(
          _.elementCreated("a"),
          _.mounted("a"),
          _.elementCreated("b"),
          _.mounted("b")
        )
        .clear()
    }

    withClue("steal into L2 before removing from L1 (whole command span moves, no re-mount):") {
      items2.set(List(cmd)) // add to L2 while still in L1 -> transfer
      items1.set(Nil) // removal from L1 is a no-op: already stolen
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "a", div of "b", sentinel, sentinel)
        )
      )
      tracker
        .assertNoEvents // the crux: transferred, not rebuilt — no events at all
        .clear()
    }

    withClue("commands after the steal anchor on the MOVED span (Append before trailing, Prepend after leading):") {
      cmdBus.emit(CollectionCommand.Append(tracker.createDiv("c"))) // after b, before the (moved) trailing sentinel
      cmdBus.emit(CollectionCommand.Prepend(tracker.createDiv("d"))) // right after the (moved) group-leading sentinel
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "d", div of "a", div of "b", div of "c", sentinel, sentinel)
        )
      )
      // Only the two new nodes appear; the pre-existing content is not touched.
      tracker
        .assertEvents(
          _.elementCreated("c"),
          _.mounted("c"),
          _.elementCreated("d"),
          _.mounted("d")
        )
        .clear()
    }

    withClue("Insert(atIndex) after the steal counts from the moved span's start (index 2 = between a and b):") {
      cmdBus.emit(CollectionCommand.Insert(tracker.createDiv("e"), atIndex = 2)) // d,a,b,c -> d,a,e,b,c
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "d", div of "a", div of "e", div of "b", div of "c", sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(
          _.elementCreated("e"),
          _.mounted("e")
        )
        .clear()
    }

    withClue("normal removal from the new host L2 tears the whole group down exactly once:") {
      items2.set(Nil)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      tracker.assertEvents(
        _.unmounted("d"),
        _.unmounted("a"),
        _.unmounted("e"),
        _.unmounted("b"),
        _.unmounted("c")
      )
    }
  }

  it("`children.command <--` emptied (RemoveAll) then refilled AFTER a steal: empty span stays anchored at the moved location") {
    // A moved command span emptied to ZERO must keep its own leading + trailing sentinels at the NEW
    // host, so a following Append lands back inside the moved span. Exercises RemoveAll + Append on a
    // command group after `moveToParent`.
    val tracker = createEventTracker()
    val cmdBus = new EventBus[CollectionCommand[Node]]
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val cmd: Inserter = children.command <-- cmdBus.events

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    withClue("command group holds a, b in L1:") {
      items1.set(List(cmd))
      cmdBus.emit(CollectionCommand.Append(tracker.createDiv("a")))
      cmdBus.emit(CollectionCommand.Append(tracker.createDiv("b")))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, div of "a", div of "b", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(
          _.elementCreated("a"),
          _.mounted("a"),
          _.elementCreated("b"),
          _.mounted("b")
        )
        .clear()
    }

    withClue("steal into L2 (whole span moves, no re-mount):") {
      items2.set(List(cmd))
      items1.set(Nil)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "a", div of "b", sentinel, sentinel)
        )
      )
      tracker.assertNoEvents.clear()
    }

    withClue("RemoveAll at the moved location: content unmounts, the group's two sentinels remain as an empty span:") {
      cmdBus.emit(CollectionCommand.RemoveAll)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, sentinel, sentinel)
        )
      )
      // #Note: command RemoveAll tears down in current order (a, b).
      tracker
        .assertEvents(
          _.unmounted("a"),
          _.unmounted("b")
        )
        .clear()
    }

    withClue("Append after emptying still lands inside the moved (now-empty) span, between its sentinels:") {
      cmdBus.emit(CollectionCommand.Append(tracker.createDiv("c")))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "c", sentinel, sentinel)
        )
      )
      tracker.assertEvents(
        _.elementCreated("c"),
        _.mounted("c")
      )
    }
  }

  it("`children.command <--` moved within one list: span moves as a unit, no re-mount") {
    val tracker = createEventTracker()
    val cmdBus = new EventBus[CollectionCommand[Node]]
    val itemsVar = Var[List[Inserter]](Nil)

    val staticX: Inserter = tracker.createSpan("X")
    val cmd: Inserter = children.command <-- cmdBus.events

    mount(div("H", children <-- itemsVar.signal))

    itemsVar.set(List(staticX, cmd))
    cmdBus.emit(CollectionCommand.Append(tracker.createDiv("a")))
    cmdBus.emit(CollectionCommand.Append(tracker.createDiv("b")))
    withClue("X then command group (a, b):") {
      expectNode(div.of("H", sentinel, span of "X", sentinel, div of "a", div of "b", sentinel, sentinel))
      // X was built up-front (element-create) then mounted; a and b built and appended in turn.
      tracker
        .assertEvents(
          _.elementCreated("X"),
          _.mounted("X"),
          _.elementCreated("a"),
          _.mounted("a"),
          _.elementCreated("b"),
          _.mounted("b")
        )
        .clear()
    }

    withClue("reorder: the whole command span moves ahead of X, as a unit, without re-mounting:") {
      itemsVar.set(List(cmd, staticX))
      expectNode(div.of("H", sentinel, sentinel, div of "a", div of "b", sentinel, span of "X", sentinel))
      tracker
        .assertNoEvents // a reorder is a move: no events for the moved span OR the passed-over X
        .clear()
    }

    withClue("commands after the move still land within the moved span (Append before trailing sentinel):") {
      cmdBus.emit(CollectionCommand.Append(tracker.createDiv("c")))
      expectNode(div.of("H", sentinel, sentinel, div of "a", div of "b", div of "c", sentinel, span of "X", sentinel))
      tracker.assertEvents(
        _.elementCreated("c"),
        _.mounted("c")
      )
    }
  }

  // `text <--` is single-node, but as a list item it is forced to keep a trailing sentinel, so its
  // span is [group-leading, text, group-trailing]. Stealing it between lists must transfer the live
  // subscription (owner moved via TransferableSubscription), NOT tear it down and rebuild it — which
  // we prove via the `text-update` events: it must NOT re-render on the steal (owner transferred),
  // then render exactly the NEW value on a genuine later update. Mirrors the `child <--` steal test.

  it("`text <--` stolen between two lists: subscription transferred (not re-run), stays live") {
    val tracker = createEventTracker()
    val textVar = Var("hi")
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val dynText: Inserter = tracker.text("t", textVar.signal)

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    items1.set(List(dynText))
    withClue("text item in L1 renders the current value once:") {
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, "hi", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(_.textUpdated("t", "hi"))
        .clear()
    }

    withClue("steal into L2 before removing from L1 — owner transferred, observer does NOT re-run:") {
      items2.set(List(dynText))
      items1.set(Nil)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, "hi", sentinel, sentinel)
        )
      )
      tracker
        .assertNoEvents // no re-render during the transfer (a rebuild would re-emit "hi")
        .clear()
    }

    withClue("reacts in L2 to a later update — the same live subscription moved with it:") {
      textVar.set("bye")
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, "bye", sentinel, sentinel)
        )
      )
      tracker.assertEvents(_.textUpdated("t", "bye")) // exactly the new value, not a stale re-emit
    }
  }

  // ----------------------------------------------------------------------------------
  // 7. Degenerate: same-transaction double-add
  // ----------------------------------------------------------------------------------

  // Degenerate case: the SAME inserter added to two lists in ONE transaction. An inserter can only
  // render in one place, so this cannot duplicate it. We pin that it converges gracefully — the span
  // ends up in exactly one list, stays live, and nothing throws — rather than corrupting the DOM.

  it("same-transaction double-add of one inserter converges to a single live location") {
    val tracker = createEventTracker()
    val valueVar = Var("A")
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val dyn: Inserter = child <-- valueVar.signal.map(tracker.createSpan(_))

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    withClue("add the same inserter to both lists atomically (no error, lands in exactly one list):") {
      withCollectedAirstreamErrors { errors =>
        Var.set(items1 -> List(dyn), items2 -> List(dyn))
        assert(errors.isEmpty)
      }
      // It converges into L2 (the later-processed list steals it); L1 is left clean.
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "A", sentinel, sentinel)
        )
      )
      // Rendered exactly once (value "A") — not duplicated across the two lists.
      tracker
        .assertEvents(
          _.elementCreated("A"),
          _.mounted("A")
        )
        .clear()
    }

    withClue("the single surviving placement is fully live afterwards:") {
      valueVar.set("B")
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "B", sentinel, sentinel)
        )
      )
      tracker.assertEvents(
        _.elementCreated("B"),
        _.unmounted("A"),
        _.mounted("B")
      )
    }
  }

}
