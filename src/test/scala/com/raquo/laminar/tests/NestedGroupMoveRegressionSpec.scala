package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.{CollectionCommand, Inserter}
import com.raquo.laminar.utils.UnitSpec

/** A nested dynamic inserter's tracked content can lag behind the DOM: another host may have
  * torn its group down, stolen a node out of its span, or its content may sit in the DOM in a
  * different order than it was tracked. These tests pin what a list re-emission and a group move
  * must do in those states: place afresh when the group is gone, and relocate exactly the span
  * currently in the DOM, in DOM order, leaving stolen nodes with their new host.
  */
class NestedGroupMoveRegressionSpec extends UnitSpec {

  // -- Re-emitting an item whose group another list already tore down --

  it("re-emitting a nested `child <--` that another list stole and then removed re-adds it, like an element") {
    // Reference: a plain element stolen by L2, removed by L2, then re-emitted by L1 is simply
    // re-inserted and re-mounted. The same must hold for a dynamic inserter, even though L1's
    // last emission still tracks it.
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

  it("re-emitting such a torn-down nested `child <--` after a retained element also re-adds it") {
    // Same as above, but the re-emitted item comes after an element that stayed in place, so the
    // list has already run out of tracked DOM content when it reaches the item.
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

  // -- A moved span keeps its DOM order --

  it("a moved `children.command <--` span keeps its DOM order (Prepend / Insert content is not re-appended)") {
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

    withClue("build b, a, c with Append + Prepend + Insert:") {
      items1.set(List(cmd))
      cmdBus.emit(CollectionCommand.Append(tracker.createDiv("a")))
      cmdBus.emit(CollectionCommand.Prepend(tracker.createDiv("b")))
      cmdBus.emit(CollectionCommand.Insert(tracker.createDiv("c"), atIndex = 2))
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
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, div of "b", div of "a", div of "c", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("steal into L2: the span moves as a unit, in DOM order, without re-mounting:") {
      items2.set(List(cmd))
      items1.set(Nil)
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "b", div of "a", div of "c", sentinel, sentinel)
        )
      )
    }

    withClue("a later Insert counts from the moved span's preserved order:") {
      cmdBus.emit(CollectionCommand.Insert(tracker.createDiv("d"), atIndex = 1))
      tracker.assertEvents(_.elementCreated("d"), _.mounted("d")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "b", div of "d", div of "a", div of "c", sentinel, sentinel)
        )
      )
    }
  }

  // -- A moved span relocates only what is currently inside it --

  it("moving a nested `children <--` relocates only the nodes still in its span: a node stolen by another list stays stolen") {
    // Reference: moving an element does not pull back a child that another list stole out of it.
    // A nested group is a span that moves like an element, so the same applies to its content.
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

    withClue("the nested list renders A, B inside L1:") {
      items1.set(List(nested))
      tracker.assertEvents(_.mounted("A"), _.mounted("B")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "A", span of "B", sentinel, sentinel),
          div.of("L2", sentinel, sentinel),
          div.of("L3", sentinel, sentinel)
        )
      )
    }

    withClue("L2 steals B out of the nested list (last write wins, no re-mount):") {
      items2.set(List(b))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, span of "A", sentinel, sentinel),
          div.of("L2", sentinel, span of "B", sentinel),
          div.of("L3", sentinel, sentinel)
        )
      )
    }

    withClue("L3 steals the nested list: only A travels with it, B stays in L2:") {
      items3.set(List(nested))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, span of "B", sentinel),
          div.of("L3", sentinel, sentinel, span of "A", sentinel, sentinel)
        )
      )
    }
  }

  it("moving a nested `children <--` keeps a node that its own nested `child <--` stole inside that child's span") {
    // The stolen node sits inside a sibling item's span within the same group. The group move
    // must not pull it back out to where the group's list last placed it.
    val tracker = createEventTracker()
    val a = tracker.createSpan("A")
    tracker.clear()
    val innerChild = Var[Option[Span]](None)
    val innerDyn: Inserter = child.maybe <-- innerChild.signal
    val inner = Var[List[Inserter]](List(innerDyn, a))
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val nested: Inserter = children <-- inner.signal

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    withClue("the nested list renders [empty child.maybe, A] in L1:") {
      items1.set(List(nested))
      tracker.assertEvents(_.mounted("A")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, sentinel, emptyCommentNode, sentinel, span of "A", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("the inner child.maybe steals A into its own span (no re-mount):") {
      innerChild.set(Some(a))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, sentinel, span of "A", sentinel, sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("L2 steals the nested list: A stays inside the inner child.maybe's span:") {
      items2.set(List(nested))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, sentinel, span of "A", sentinel, sentinel, sentinel)
        )
      )
    }
  }

  it("promoting a plain `child <--` whose node another list stole moves an empty span, leaving the node with its new host") {
    // A plainly applied `child <--` has no trailing sentinel, so its span end is derived from its
    // tracked node. Once another list steals that node, promoting the inserter into a
    // `children <--` list must neither fail nor steal the node back.
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

    withClue("L3 promotes the inserter into a list: its empty span moves, A stays in L2:") {
      withCollectedAirstreamErrors { errors =>
        items3.set(List(dyn))
        assert(errors.isEmpty, s"promoting the inserter reported: ${errors.mkString("; ")}")
      }
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
}
