package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.utils.UnitSpec

import scala.annotation.nowarn

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

    val el = ({
      div(
        "Hello",
        children <-- foosBus.events.toSignal(Nil).split(identity) { (id, _, idSignal) =>
          child <-- idSignal.map(v => span(s"item-$v"))
        }
      )
    }: @nowarn("msg=method split in class SplittableSeqObservable is deprecated"))

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
    // its inner list to ZERO content must leave those two sentinels in place – the span stays
    // findable and refillable – and must behave identically once the whole span has been MOVED to a
    // new slot: the empty span is anchored at the moved location, and grows there.
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
    // The span is ALREADY empty (a bare [leading, trailing] pair, zero content) at the moment it's
    // stolen into another list, so `moveToParent` must relocate a ZERO-length span, and the first
    // population after the move must land at the new host between the moved sentinels.
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
    // An ALREADY-empty span (bare [leading, trailing]) is moved WITHIN one list via
    // `moveWithinDynamicList`. The zero-length span must relocate past its neighbour, and the first
    // population must land at the span's new position.
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

}
