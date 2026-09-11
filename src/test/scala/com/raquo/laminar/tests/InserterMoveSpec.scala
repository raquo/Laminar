package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.{CollectionCommand, Inserter}
import com.raquo.laminar.utils.UnitSpec

/** THE move / transfer suite for inserters: one home for every "relocate an item, then assert the
  * lifecycle consequence" test. It walks the move taxonomy — FROM x TO x direction x item-type x
  * depth — with each case asserting whether the move is SEAMLESS (no re-mount; the DOM span is
  * relocated and subscriptions transferred) or DELIBERATELY re-mounts (documented as such).
  *
  * Sections, in order:
  *   1. Classic single-node `child <--` moves (pre-#157 path: ChildInserter.switchToChild).
  *   2. Classic `children <--` plain-element moves (pre-#157 reconcile: updateChildren).
  *   3. A dynamic inserter reordered WITHIN one `children <--` list (moveWithinDynamicList).
  *   4. A dynamic inserter moved BETWEEN two `children <--` lists (add-first steal / remove-first /
  *      the #163 two-bindings characterization), including nested and depth-2/3 spans.
  *   5. Promote / demote across static application and a list (static <-> list, static <-> static).
  *   6. The inserter-TYPE matrix: `children.command <--` and `text <--` as moved items.
  *   7. The degenerate same-transaction double-add.
  *
  * The #157 guarantee under test throughout: a move relocates an item's DOM span and transfers its
  * subscriptions WITHOUT re-mounting its content. See notes/Testing.md for assertion conventions.
  */
class InserterMoveSpec extends UnitSpec {

  // ----------------------------------------------------------------------------------
  // 1. Classic single-node `child <--` moves (ChildInserter.switchToChild)
  // ----------------------------------------------------------------------------------

  it("can move child from one receiver to another") {

    val spanA = span("a")
    val spanB = span("b")
    val spanC = span("c")
    val spanD = span("d")
    val spanE = span("e")

    val bus1 = new EventBus[HtmlElement]
    val bus2 = new EventBus[HtmlElement]

    val el = div(
      child <-- bus1,
      child <-- bus2,
    )

    mount(el)

    // --

    expectNode(
      div of (
        sentinel,
        sentinel
      )
    )

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

    // -- Unmount and re-mount

    unmount()

    mount(el)

    expectNode(
      div of (
        sentinel,
        span of "a",
        sentinel,
        span of "b",
      )
    )

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

  // ----------------------------------------------------------------------------------
  // 2. Classic `children <--` plain-element moves (ChildrenInserter.updateChildren)
  // ----------------------------------------------------------------------------------

  it("can move children from one dynamic list to another") {

    val spanA = span("a")
    val spanB = span("b")
    val spanC = span("c")
    val spanD = span("d")
    val spanE = span("e")
    val spanF = span("f")

    val bus1 = new EventBus[List[HtmlElement]]
    val bus2 = new EventBus[List[HtmlElement]]

    val el = div(
      children <-- bus1,
      span("--"),
      children <-- bus2,
    )

    mount(el)

    // --

    expectNode(
      div of (
        sentinel,
        span of "--",
        sentinel
      )
    )

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
  }

  it("moving elements within / between classic `children <--` lists never re-mounts them") {
    // The classic reconcile (ChildrenInserter.updateChildren) routes reorders and steals
    // through moveWithinDynamicList, so relocating a plain element must not tear its DOM
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

  // ----------------------------------------------------------------------------------
  // 3. Dynamic inserter reordered WITHIN one `children <--` list (moveWithinDynamicList)
  // ----------------------------------------------------------------------------------

  it("reordering never re-mounts items: static and dynamic inserters move without re-running") {
    // A move (moveWithinDynamicList) must relocate an item's DOM span WITHOUT tearing it down
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

    withClue("swap the two static neighbours (pure static-inserter moves, base-class moveWithinDynamicList) – no re-mount:") {
      itemsVar.set(List(staticB, staticA, dyn))
      expectNode(div.of("H", sentinel, span of "B", span of "A", sentinel, span of "d1", sentinel, sentinel))
      observeCount shouldBe 2
      tracker.assertNoEvents
    }
  }

  it("multi-node dynamic span moves forward and backward, of varying length (no re-mount)") {
    // Exercises DynamicInserter.moveWithinDynamicList directly: the whole nested span (leading
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
    // move to L2 as a unit, and the inner list must remain live (able to update in place) after.
    val innerVar = Var[List[Int]](List(1, 2))
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val nested: Inserter = children <-- innerVar.signal.map(_.map(i => span(s"n$i")))

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    // Sentinel layout for a nested `children <--` item inside an outer `children <--` list:
    //   [outer-leading, group-leading, ...content..., trailing, outer-trailing]
    withClue("in L1:") {
      items1.set(List(nested))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "n1", span of "n2", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("steal into L2 (whole multi-node span moves) before removing from L1:") {
      items2.set(List(nested))
      items1.set(Nil)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "n1", span of "n2", sentinel, sentinel)
        )
      )
    }

    withClue("inner list still live in L2 (grow), directing emissions to the new parent:") {
      innerVar.set(List(1, 2, 3))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "n1", span of "n2", span of "n3", sentinel, sentinel)
        )
      )
    }

    withClue("inner list still live in L2 (shrink):") {
      innerVar.set(List(7))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "n7", sentinel, sentinel)
        )
      )
    }

    withClue("normal removal from its new host L2 tears it down:") {
      items2.set(Nil)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
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
