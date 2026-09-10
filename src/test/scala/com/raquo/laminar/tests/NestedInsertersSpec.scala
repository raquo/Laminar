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
    // nested is empty -> only its two bracketing sentinels are present
    expectNode(div.of("H", sentinel, span of "A", sentinel, sentinel, sentinel, sentinel))

    withClue("Inner list fills in (between the nested item's sentinels):") {
      innerVar.set(List(1, 2))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n1", span of "n2", sentinel, sentinel, sentinel))
    }

    withClue("Reorder outer: the whole nested span moves as a unit:") {
      itemsVar.set(List(nested, staticA))
      expectNode(div.of("H", sentinel, sentinel, span of "n1", span of "n2", sentinel, sentinel, span of "A", sentinel))
    }

    withClue("Inner update after the move still works (trailing sentinel stays at span end):") {
      innerVar.set(List(3))
      expectNode(div.of("H", sentinel, sentinel, span of "n3", sentinel, sentinel, span of "A", sentinel))
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
    expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n1", span of "n2", sentinel, sentinel, sentinel))

    withClue("Move the span backward (to the front):") {
      itemsVar.set(List(nested, staticA))
      expectNode(div.of("H", sentinel, sentinel, span of "n1", span of "n2", sentinel, sentinel, span of "A", sentinel))
    }

    withClue("Move it forward again (back behind the static):") {
      itemsVar.set(List(staticA, nested))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n1", span of "n2", sentinel, sentinel, sentinel))
    }

    withClue("Grow the span to three content nodes, then move it backward:") {
      innerVar.set(List(1, 2, 3))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n1", span of "n2", span of "n3", sentinel, sentinel, sentinel))
      itemsVar.set(List(nested, staticA))
      expectNode(div.of("H", sentinel, sentinel, span of "n1", span of "n2", span of "n3", sentinel, sentinel, span of "A", sentinel))
    }

    withClue("Shrink the span to a single content node while moved, then move forward:") {
      innerVar.set(List(7))
      expectNode(div.of("H", sentinel, sentinel, span of "n7", sentinel, sentinel, span of "A", sentinel))
      itemsVar.set(List(staticA, nested))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n7", sentinel, sentinel, sentinel))
    }

    withClue("Inner list is still live after all the moves:") {
      innerVar.set(List(8, 9))
      expectNode(div.of("H", sentinel, span of "A", sentinel, span of "n8", span of "n9", sentinel, sentinel, sentinel))
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
