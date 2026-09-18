package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.utils.UnitSpec

/** A nested dynamic inserter's tracked content can lag behind the DOM after another host tore its
  * group down. These tests pin what re-emitting such an inserter must do: place it afresh (like a
  * plain element) rather than failing on the stale tracking left by its last emission.
  *
  * Companion cases live in [[InserterMoveSpec]]: a group MOVE that must relocate exactly its live
  * DOM span (section 4c), and promoting / demoting a single-node `child <--` whose node another
  * host stole (section 5).
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
}
