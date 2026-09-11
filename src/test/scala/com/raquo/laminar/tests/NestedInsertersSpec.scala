package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.utils.UnitSpec

/** Tests for issue #157: `children <--` accepting a list of Inserters,
  * so dynamic inserters (`child <--`, `children <--`) can be nested directly.
  */
class NestedInsertersSpec extends UnitSpec {

  it("static + dynamic items: add, reorder, remove") {
    val itemsVar = Var[List[Inserter]](Nil)

    // Stable inserter instances (so identity matching works across emissions).
    val staticA: Inserter = span("A")
    val childBus = new EventBus[String]
    val dynB: Inserter = child <-- childBus.events.map(v => span(v))

    mount(div("Hello", children <-- itemsVar.signal))
    expectNode(div.of("Hello", sentinel, sentinel))

    withClue("Add a single static element (no sentinel for it):") {
      itemsVar.set(List(staticA))
      expectNode(div.of("Hello", sentinel, span of "A", sentinel))
    }

    withClue("Add a dynamic child<-- item (gets its own leading + trailing sentinel):") {
      itemsVar.set(List(staticA, dynB))
      // dynB has no value yet -> only its two bracketing sentinels are present
      expectNode(div.of("Hello", sentinel, span of "A", sentinel, sentinel, sentinel))
    }

    withClue("Dynamic item emits content (between its two sentinels):") {
      childBus.writer.onNext("B1")
      expectNode(div.of("Hello", sentinel, span of "A", sentinel, span of "B1", sentinel, sentinel))
    }

    withClue("Reorder: dynamic before static (its whole span moves):") {
      itemsVar.set(List(dynB, staticA))
      expectNode(div.of("Hello", sentinel, sentinel, span of "B1", sentinel, span of "A", sentinel))
    }

    withClue("Dynamic item emits new content while reordered:") {
      childBus.writer.onNext("B2")
      expectNode(div.of("Hello", sentinel, sentinel, span of "B2", sentinel, span of "A", sentinel))
    }

    withClue("Remove the static item:") {
      itemsVar.set(List(dynB))
      expectNode(div.of("Hello", sentinel, sentinel, span of "B2", sentinel, sentinel))
    }

    withClue("Remove all items:") {
      itemsVar.set(Nil)
      expectNode(div.of("Hello", sentinel, sentinel))
    }
  }

  it("child <-- returned from split (the headline use case)") {
    val foosBus = new EventBus[List[Int]]

    val el = div(
      "Hello",
      children <-- foosBus.events.toSignal(Nil).split(identity) { (id, _, idSignal) =>
        child <-- idSignal.map(v => span(s"item-$v"))
      }
    )

    mount(el)
    expectNode(div.of("Hello", sentinel, sentinel))

    withClue("Initial list:") {
      foosBus.writer.onNext(List(1, 2, 3))
      expectNode(div.of(
        "Hello", sentinel,
        sentinel, span of "item-1", sentinel,
        sentinel, span of "item-2", sentinel,
        sentinel, span of "item-3", sentinel,
        sentinel
      ))
    }

    withClue("Reorder:") {
      foosBus.writer.onNext(List(3, 1, 2))
      expectNode(div.of(
        "Hello", sentinel,
        sentinel, span of "item-3", sentinel,
        sentinel, span of "item-1", sentinel,
        sentinel, span of "item-2", sentinel,
        sentinel
      ))
    }

    withClue("Remove middle:") {
      foosBus.writer.onNext(List(3, 2))
      expectNode(div.of(
        "Hello", sentinel,
        sentinel, span of "item-3", sentinel,
        sentinel, span of "item-2", sentinel,
        sentinel
      ))
    }
  }

  it("per-item lifecycle: pause on unmount, resume on remount, teardown on removal") {
    var observeCount = 0
    val valueVar = Var(0)
    val itemsVar = Var[List[Inserter]](Nil)

    val dynItem: Inserter = child <-- valueVar.signal.map { v =>
      observeCount += 1
      span(s"v=$v")
    }
    itemsVar.set(List(dynItem))

    val el = div("Hello", children <-- itemsVar.signal)

    mount(el)
    expectNode(div.of("Hello", sentinel, sentinel, span of "v=0", sentinel, sentinel))
    observeCount shouldBe 1

    withClue("Emit while mounted -> observer runs:") {
      valueVar.set(5)
      expectNode(div.of("Hello", sentinel, sentinel, span of "v=5", sentinel, sentinel))
      observeCount shouldBe 2
    }

    withClue("Unmount -> item is paused:") {
      unmount()
      valueVar.set(9) // Var retains value; observer is paused so must not run
      observeCount shouldBe 2
    }

    withClue("Remount -> resumes with latest value:") {
      mount(el)
      expectNode(div.of("Hello", sentinel, sentinel, span of "v=9", sentinel, sentinel))
      observeCount shouldBe 3
    }

    withClue("Remove the item -> its owner is torn down:") {
      itemsVar.set(Nil)
      expectNode(div.of("Hello", sentinel, sentinel))
      valueVar.set(11) // removed item must not react anymore
      observeCount shouldBe 3
    }
  }

  it("re-adding the SAME dynamic inserter instance after removal works") {
    // Since a dynamic inserter is used as its own list-item handle, its per-item
    // state lives on the instance. Removing then re-adding the same instance must
    // re-initialize that state (fresh sentinels + a fresh, live owner).
    val valueVar = Var("A")
    val itemsVar = Var[List[Inserter]](Nil)
    val dyn: Inserter = child <-- valueVar.signal.map(v => span(v))

    mount(div("H", children <-- itemsVar.signal))
    expectNode(div.of("H", sentinel, sentinel))

    withClue("Add the instance:") {
      itemsVar.set(List(dyn))
      expectNode(div.of("H", sentinel, sentinel, span of "A", sentinel, sentinel))
    }

    withClue("Remove it:") {
      itemsVar.set(Nil)
      expectNode(div.of("H", sentinel, sentinel))
    }

    withClue("Re-add the SAME instance -> state re-initializes:") {
      itemsVar.set(List(dyn))
      expectNode(div.of("H", sentinel, sentinel, span of "A", sentinel, sentinel))
    }

    withClue("Re-added instance is live again (its owner was rebuilt):") {
      valueVar.set("B")
      expectNode(div.of("H", sentinel, sentinel, span of "B", sentinel, sentinel))
    }
  }

  it("text <-- as an item (keeps a static leading sentinel anchor)") {
    // As a `children <--` list item, `text <--` keeps its leading sentinel comment as a
    // stable anchor and inserts the text node as a content node between its two bracketing
    // sentinels. This gives every dynamic list item a static first node.
    val textBus = new EventBus[String]
    val itemsVar = Var[List[Inserter]](Nil)

    val staticA: Inserter = span("A")
    val dynText: Inserter = text <-- textBus.events

    mount(div("H", children <-- itemsVar.signal))
    expectNode(div.of("H", sentinel, sentinel))

    withClue("Add static + text item (text empty -> two bracketing sentinels):") {
      itemsVar.set(List(staticA, dynText))
      expectNode(div.of("H", sentinel, span of "A", sentinel, sentinel, sentinel))
    }

    withClue("Text emits -> text node inserted after leading sentinel, trailing stays:") {
      textBus.writer.onNext("hello")
      expectNode(div.of("H", sentinel, span of "A", sentinel, "hello", sentinel, sentinel))
    }

    withClue("Text updates in place -> structure unchanged:") {
      textBus.writer.onNext("world")
      expectNode(div.of("H", sentinel, span of "A", sentinel, "world", sentinel, sentinel))
    }

    withClue("Reorder: text item's whole span (both sentinels + text node) moves:") {
      itemsVar.set(List(dynText, staticA))
      expectNode(div.of("H", sentinel, sentinel, "world", sentinel, span of "A", sentinel))
    }

    withClue("Remove the text item -> its nodes are gone:") {
      itemsVar.set(List(staticA))
      expectNode(div.of("H", sentinel, span of "A", sentinel))
    }
  }

  it("nested children <-- as an item (multi-node span move)") {
    val innerVar = Var[List[Int]](Nil)
    val itemsVar = Var[List[Inserter]](Nil)

    val staticA: Inserter = span("A")
    val nested: Inserter = children <-- innerVar.signal.map(_.map(i => span(s"n$i")))
    itemsVar.set(List(staticA, nested))

    mount(div("H", children <-- itemsVar.signal))
    // nested is empty -> only its leading + trailing sentinel are present
    expectNode(div.of("H", sentinel, span of "A", sentinel, sentinel, sentinel))

    withClue("Inner list fills in (between the nested item's sentinels):") {
      innerVar.set(List(1, 2))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n1", span of "n2", sentinel, sentinel))
    }

    withClue("Reorder outer: the whole nested span moves as a unit:") {
      itemsVar.set(List(nested, staticA))
      expectNode(div.of("H", sentinel, sentinel, span of "n1", span of "n2", sentinel, span of "A", sentinel))
    }

    withClue("Inner update after the move still works (trailing sentinel stays at span end):") {
      innerVar.set(List(3))
      expectNode(div.of("H", sentinel, sentinel, span of "n3", sentinel, span of "A", sentinel))
    }
  }

  it("nested `children <--` item empties to zero content while nested (keeps its sentinels), refills, and does the same after a move") {
    // A nested `children <--` item is bracketed by its own leading + trailing sentinels. Emptying
    // its inner list to ZERO content (existing tests only ever shrink to 1) must leave those two
    // sentinels in place – the span stays findable and refillable – and it must behave identically
    // after the whole span has been MOVED to a new slot (the empty span is anchored at the moved
    // location, and grows there, not back in its original slot).
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

    withClue("nested item starts empty: A, then just the item's own leading + trailing sentinels:") {
      itemsVar.set(List(staticA, nested))
      mount(div("H", children <-- itemsVar.signal))
      expectNode(div.of("H", sentinel, span of "A", sentinel, sentinel, sentinel))
      tracker.assertEvents(_.mounted("A")).clear()
    }

    withClue("fill the nested span (content lands between its two sentinels):") {
      innerVar.set(List(n1, n2))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n1", span of "n2", sentinel, sentinel))
      tracker
        .assertEvents(
          _.mounted("n1"),
          _.mounted("n2")
        )
        .clear()
    }

    withClue("empty it to ZERO while nested: content unmounts, the two sentinels remain as an empty span:") {
      innerVar.set(Nil)
      // Back to the bare [leading, trailing] pair – the span didn't collapse or lose its anchors.
      expectNode(div.of("H", sentinel, span of "A", sentinel, sentinel, sentinel))
      // #Note: `children <--` tears down in contentMap insertion order (n1, n2).
      tracker
        .assertEvents(
          _.unmounted("n1"),
          _.unmounted("n2")
        )
        .clear()
    }

    withClue("refill after emptying: the same span accepts new content:") {
      innerVar.set(List(n3))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n3", sentinel, sentinel))
      tracker.assertEvents(_.mounted("n3")).clear()
    }

    withClue("move the (non-empty) nested span ahead of A – a unit move, no re-mount:") {
      itemsVar.set(List(nested, staticA))
      expectNode(div.of("H", sentinel, sentinel, span of "n3", sentinel, span of "A", sentinel))
      tracker.assertNoEvents.clear()
    }

    withClue("empty to ZERO again, now at the MOVED slot: the sentinels stay put where the span was moved to:") {
      innerVar.set(Nil)
      expectNode(div.of("H", sentinel, sentinel, sentinel, span of "A", sentinel))
      tracker.assertEvents(_.unmounted("n3")).clear()
    }

    withClue("grow from empty while nested AND moved: content fills the moved span, not the original slot:") {
      innerVar.set(List(n4, n5))
      expectNode(div.of("H", sentinel, sentinel, span of "n4", span of "n5", sentinel, span of "A", sentinel))
      tracker
        .assertEvents(
          _.mounted("n4"),
          _.mounted("n5")
        )
    }
  }

  it("nested `children <--` item emptied to zero BEFORE being stolen: the empty span relocates, then populates at the new location") {
    // Distinct ordering from the fill-then-move test above: here the span is ALREADY empty (a bare
    // [leading, trailing] pair, zero content) at the moment it's stolen into another list. So
    // `moveToParent` must relocate a ZERO-length span, and the FIRST population after the move must
    // land at the new host between the moved sentinels — not back in the original list.
    val tracker = createEventTracker()
    val innerVar = Var[List[Node]](Nil)
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)

    val n1 = tracker.createDiv("n1")
    val n2 = tracker.createDiv("n2")
    tracker.clear()

    val nested: Inserter = children <-- innerVar.signal

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    withClue("place the (empty) nested item in L1: just its bare leading + trailing sentinels, no content:") {
      items1.set(List(nested))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      tracker.assertNoEvents.clear() // nothing rendered yet
    }

    withClue("steal the EMPTY span into L2 before removing from L1: the bare pair relocates, still no content:") {
      items2.set(List(nested))
      items1.set(Nil)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, sentinel, sentinel)
        )
      )
      tracker.assertNoEvents.clear()
    }

    withClue("populate from the new location: content lands in L2 between the moved sentinels, not back in L1:") {
      innerVar.set(List(n1, n2))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "n1", div of "n2", sentinel, sentinel)
        )
      )
      tracker.assertEvents(
        _.mounted("n1"),
        _.mounted("n2")
      )
    }
  }

  it("nested `children <--` item reordered within its list WHILE EMPTY: the empty span moves as a unit, then populates at its new position") {
    // The within-list ordering counterpart to the steal test above: an ALREADY-empty span (bare
    // [leading, trailing]) is moved WITHIN one list via `moveWithinDynamicList` (not `moveToParent`).
    // The zero-length span must relocate past its neighbour, and the first population must land at
    // the span's NEW position, not its old one.
    val tracker = createEventTracker()
    val innerVar = Var[List[Node]](Nil)
    val itemsVar = Var[List[Inserter]](Nil)

    val staticA = tracker.createSpan("A")
    val n1 = tracker.createSpan("n1")
    val n2 = tracker.createSpan("n2")
    tracker.clear()

    val nested: Inserter = children <-- innerVar.signal

    withClue("A first, then the empty nested item (bare leading + trailing sentinels):") {
      itemsVar.set(List(staticA, nested))
      mount(div("H", children <-- itemsVar.signal))
      expectNode(div.of("H", sentinel, span of "A", sentinel, sentinel, sentinel))
      tracker.assertEvents(_.mounted("A")).clear()
    }

    withClue("reorder the EMPTY span ahead of A: the bare pair moves as a unit, no events:") {
      itemsVar.set(List(nested, staticA))
      expectNode(div.of("H", sentinel, sentinel, sentinel, span of "A", sentinel))
      tracker.assertNoEvents.clear()
    }

    withClue("populate at the new (front) position: content lands before A, between the moved sentinels:") {
      innerVar.set(List(n1, n2))
      expectNode(div.of("H", sentinel, sentinel, span of "n1", span of "n2", sentinel, span of "A", sentinel))
      tracker.assertEvents(
        _.mounted("n1"),
        _.mounted("n2")
      )
    }
  }

  it("static seq item (StaticChildrenInserter): empty group + multi-node span") {
    val itemsVar = Var[List[Inserter]](Nil)

    // Stable inserter instances (so identity matching works across emissions).
    val staticA: Inserter = span("A")
    // A static group of several nodes, rendered directly, with no bracketing sentinels.
    val groupXY: Inserter = List(span("X"), span("Y"))
    // Empty static group: no content nodes, so it renders a single placeholder comment
    // (see HookableChildrenInserter.nodesToRender) to anchor the item.
    val emptyGroup: Inserter = List.empty[HtmlElement]

    mount(div("H", children <-- itemsVar.signal))
    expectNode(div.of("H", sentinel, sentinel))

    withClue("Empty static group renders as a single placeholder sentinel:") {
      itemsVar.set(List(staticA, emptyGroup))
      expectNode(div.of("H", sentinel, span of "A", sentinel, sentinel))
    }

    withClue("Multi-node group renders its nodes directly, with no bracketing sentinels:") {
      itemsVar.set(List(staticA, emptyGroup, groupXY))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "X", span of "Y", sentinel))
    }

    withClue("Reorder: empty group and multi-node group move as whole spans:") {
      itemsVar.set(List(groupXY, emptyGroup, staticA))
      expectNode(div.of("H", sentinel, span of "X", span of "Y", sentinel, span of "A", sentinel))
    }

    withClue("Remove the multi-node group (its nodes go):") {
      itemsVar.set(List(emptyGroup, staticA))
      expectNode(div.of("H", sentinel, sentinel, span of "A", sentinel))
    }

    withClue("Remove all items:") {
      itemsVar.set(Nil)
      expectNode(div.of("H", sentinel, sentinel))
    }
  }

  it("reordering never re-mounts items: static and dynamic inserters move without re-running") {
    // A move (moveWithinDynamicList) must relocate an item's DOM span WITHOUT tearing it down
    // and re-adding it: the logical parent is unchanged, so hooks/subscriptions must not re-run.
    // We verify this for a dynamic item (observer count stays put, item stays live) while its
    // static neighbours are shuffled around it, and while it is itself moved forward and backward.
    var observeCount = 0
    val dynVar = Var("d0")
    val itemsVar = Var[List[Inserter]](Nil)

    val staticA: Inserter = span("A")
    val staticB: Inserter = span("B")
    val dyn: Inserter = child <-- dynVar.signal.map { v =>
      observeCount += 1
      span(v)
    }

    itemsVar.set(List(staticA, dyn, staticB))
    mount(div("H", children <-- itemsVar.signal))
    expectNode(div.of("H", sentinel, span of "A", sentinel, span of "d0", sentinel, span of "B", sentinel))
    observeCount shouldBe 1

    withClue("Move the dynamic item to the front (backward move) – observer must NOT re-run:") {
      itemsVar.set(List(dyn, staticA, staticB))
      expectNode(div.of("H", sentinel, sentinel, span of "d0", sentinel, span of "A", span of "B", sentinel))
      observeCount shouldBe 1
    }

    withClue("Move the dynamic item to the back (forward move, past both statics) – still no re-run:") {
      itemsVar.set(List(staticA, staticB, dyn))
      expectNode(div.of("H", sentinel, span of "A", span of "B", sentinel, span of "d0", sentinel, sentinel))
      observeCount shouldBe 1
    }

    withClue("Dynamic item is still live after being moved twice (updates in place):") {
      dynVar.set("d1")
      expectNode(div.of("H", sentinel, span of "A", span of "B", sentinel, span of "d1", sentinel, sentinel))
      observeCount shouldBe 2
    }

    withClue("Swap the two static neighbours (pure static-inserter moves):") {
      itemsVar.set(List(staticB, staticA, dyn))
      expectNode(div.of("H", sentinel, span of "B", span of "A", sentinel, span of "d1", sentinel, sentinel))
      observeCount shouldBe 2
    }
  }

  it("multi-node dynamic span moves forward and backward, of varying length") {
    // Exercises DynamicInserter.moveWithinDynamicList directly: the whole nested span (leading
    // sentinel .. content nodes .. trailing sentinel) is relocated as a unit. We move it both
    // backward and forward, round-trip it, and grow/shrink the span so the internal walk covers
    // spans of different lengths.
    val innerVar = Var[List[Int]](List(1, 2))
    val itemsVar = Var[List[Inserter]](Nil)

    val staticA: Inserter = span("A")
    val nested: Inserter = children <-- innerVar.signal.map(_.map(i => span(s"n$i")))
    itemsVar.set(List(staticA, nested))

    mount(div("H", children <-- itemsVar.signal))
    expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n1", span of "n2", sentinel, sentinel))

    withClue("Move the span backward (to the front):") {
      itemsVar.set(List(nested, staticA))
      expectNode(div.of("H", sentinel, sentinel, span of "n1", span of "n2", sentinel, span of "A", sentinel))
    }

    withClue("Move it forward again (back behind the static):") {
      itemsVar.set(List(staticA, nested))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n1", span of "n2", sentinel, sentinel))
    }

    withClue("Grow the span to three content nodes, then move it backward:") {
      innerVar.set(List(1, 2, 3))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n1", span of "n2", span of "n3", sentinel, sentinel))
      itemsVar.set(List(nested, staticA))
      expectNode(div.of("H", sentinel, sentinel, span of "n1", span of "n2", span of "n3", sentinel, span of "A", sentinel))
    }

    withClue("Shrink the span to a single content node while moved, then move forward:") {
      innerVar.set(List(7))
      expectNode(div.of("H", sentinel, sentinel, span of "n7", sentinel, span of "A", sentinel))
      itemsVar.set(List(staticA, nested))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n7", sentinel, sentinel))
    }

    withClue("Inner list is still live after all the moves:") {
      innerVar.set(List(8, 9))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n8", span of "n9", sentinel, sentinel))
    }
  }

  it("static seq item backed by a MUTABLE collection: removing from `children <--` is resilient to source collection mutation") {
    import scala.collection.mutable

    val itemsVar = Var[List[Inserter]](Nil)
    val staticA: Inserter = span("A")

    // An inserter whose backing collection is mutable and shared with our test code
    // (nodeRenderable.asNodeSeq keeps ChildNode-s in their original collection).
    val buffer = mutable.ArrayBuffer[HtmlElement](
      span("X"),
      span("Y")
    )
    val bufferInserter: Inserter = buffer

    mount(div("H", children <-- itemsVar.signal))
    expectNode(
      div.of(
        "H",
        sentinel, // `children <--` sentinel
        sentinel
      )
    )

    withClue("Add the mutable-backed inserter (X, Y between its two sentinels):") {
      itemsVar.set(List(staticA, bufferInserter))
      expectNode(
        div.of(
          "H",
          sentinel, // `children <--` sentinel
          span of "A",
          span of "X",
          span of "Y",
          sentinel,
        )
      )
    }

    withClue("Mutating the backing collection alone does not change the DOM:") {
      buffer.remove(1) // drop Y from the backing; the DOM still shows X, Y
      expectNode(
        div.of(
          "H",
          sentinel,
          span of "A",
          span of "X",
          span of "Y",
          sentinel,
        )
      )
    }

    withClue("Removing the group must remove ALL originally-inserted nodes (no orphaned Y):") {
      itemsVar.set(List(staticA))
      expectNode(
        div.of(
          "H",
          sentinel,
          span of "A",
          sentinel
        )
      )
    }

    withClue("Remove all items:") {
      itemsVar.set(Nil)
      expectNode(
        div.of(
          "H",
          sentinel,
          sentinel
        )
      )
    }
  }

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
    probe(addFirst = false) shouldBe (2, 1)
    // Add-first is a true transfer: the element is stolen into L2 with NO re-mount.
    probe(addFirst = true) shouldBe (1, 0)
  }

  it("CHARACTERIZATION (remove-first): same dynamic inserter moved between two lists re-mounts, like an element") {
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
    // Depth-2 already proves `moveToParent` recurses; this pins that a THIRD level of nesting works
    // the same – the leaf's owner is transferred, not rebuilt – so nothing in the recursion assumes
    // a fixed depth. We also empty and refill the middle span at depth-3 to check the nested empty
    // span stays anchored at the moved location.
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
}
