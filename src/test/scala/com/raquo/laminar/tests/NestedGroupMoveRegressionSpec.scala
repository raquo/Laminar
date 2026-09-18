package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.utils.UnitSpec

/** A nested dynamic inserter's tracked content can lag behind the DOM after another host tore its
  * group down or stole its node. These tests pin what re-placing such an inserter must do: place it
  * afresh (like a plain element) rather than failing on the stale tracking left by its last
  * emission.
  *
  * The companion case — a group MOVE that must relocate exactly its live DOM span (in DOM order,
  * leaving stolen nodes with their new host) — lives in [[InserterMoveSpec]] section 4c.
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

  // -- Promoting a plain `child <--` whose node was stolen --

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
