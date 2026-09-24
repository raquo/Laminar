package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.utils.{EventTracker, UnitSpec}

/** A dynamic item that a `children <--` list steals back from a plain placement under the SAME
  * parent must end up bracketed by its own sentinels, like any other list item, so that its span
  * never extends over the list's neighbouring items.
  */
class NestedGroupReStealSpec extends UnitSpec {

  // -- Fixture: L1 and L2 are two `children <--` lists under ONE parent --

  private class TwoLists {
    val items1: Var[List[Inserter]] = Var(Nil)
    val items2: Var[List[Inserter]] = Var(Nil)
    val parent: Div = div(children <-- items1.signal, children <-- items2.signal)
  }

  /** Gets `dyn` into L1 by re-stealing it from a plain placement under L1's own parent.
    *
    * L2 steals `dyn` and drops it, tearing its group down while L1 still tracks it. Then
    * `parent.amend(dyn)` rebuilds the group plainly (without a trailing sentinel), and L1
    * steals it back by re-emitting it, which takes the same-parent branch of
    * `DynamicInserter.moveWithinDynamicList`.
    *
    * `dyn` must render `c` on activation.
    */
  private def reStealIntoL1(lists: TwoLists, dyn: Inserter, tracker: EventTracker): Unit = {
    withClue("detour: L1 places dyn, L2 steals and drops it, the parent re-applies it:") {
      lists.items1.set(List(dyn))
      lists.items2.set(List(dyn))
      lists.items2.set(Nil)
      lists.parent.amend(dyn)
      tracker.assertEvents(
        _.mounted("c"), // L1 places dyn
        _.unmounted("c"), // L2 tears dyn's group down
        _.mounted("c") // the parent rebuilds dyn's group
      ).clear()
    }

    withClue("L1 re-emits dyn, stealing it back without re-mounting its content:") {
      lists.items1.set(List(dyn))
      tracker.assertNoEvents.clear()
    }
  }

  it("a group re-stolen from a plain placement under the same parent is bracketed in its new list") {
    val tracker = createEventTracker()
    val c = tracker.createSpan("c")
    tracker.clear()
    val lists = new TwoLists
    val dyn: Inserter = child <-- Val(c)

    mount(lists.parent)
    reStealIntoL1(lists, dyn, tracker)

    withClue("L1 now holds dyn's span, closed by its own trailing sentinel:") {
      expectNode(
        div.of(
          sentinel, sentinel, span of "c", sentinel, sentinel, // L1: [dyn: [c]]
          sentinel, sentinel // L2: []
        )
      )
    }
  }

  it("REFERENCE: a list's item placed right after a group stays with the list when the group re-renders") {
    val tracker = createEventTracker()
    val c = tracker.createSpan("c")
    val c2 = tracker.createSpan("c2")
    tracker.clear()
    val lists = new TwoLists
    val childVar = Var[HtmlElement](c)
    val dyn: Inserter = child <-- childVar.signal

    mount(lists.parent)

    withClue("L1 places dyn directly:") {
      lists.items1.set(List(dyn))
      tracker.assertEvents(_.mounted("c")).clear()
    }

    withClue("L1 takes c as its own item right after dyn (last write wins):") {
      lists.items1.set(List(dyn, c))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          sentinel, sentinel, sentinel, span of "c", sentinel, // L1: [dyn: [], c]
          sentinel, sentinel // L2: []
        )
      )
    }

    withClue("dyn renders c2; L1's c stays:") {
      childVar.set(c2)
      tracker.assertEvents(_.mounted("c2")).clear()
      expectNode(
        div.of(
          sentinel, sentinel, span of "c2", sentinel, span of "c", sentinel, // L1: [dyn: [c2], c]
          sentinel, sentinel // L2: []
        )
      )
    }
  }

  it("a list's item placed right after a re-stolen group stays with the list when the group re-renders") {
    val tracker = createEventTracker()
    val c = tracker.createSpan("c")
    val c2 = tracker.createSpan("c2")
    tracker.clear()
    val lists = new TwoLists
    val childVar = Var[HtmlElement](c)
    val dyn: Inserter = child <-- childVar.signal

    mount(lists.parent)
    reStealIntoL1(lists, dyn, tracker)

    withClue("L1 takes c as its own item right after dyn (last write wins):") {
      lists.items1.set(List(dyn, c))
      tracker.assertNoEvents.clear()
    }

    withClue("dyn renders c2; L1's c stays, as in the REFERENCE:") {
      childVar.set(c2)
      tracker.assertEvents(_.mounted("c2")).clear()
      expectNode(
        div.of(
          sentinel, sentinel, span of "c2", sentinel, span of "c", sentinel, // L1: [dyn: [c2], c]
          sentinel, sentinel // L2: []
        )
      )
    }
  }

  it("REFERENCE: moving a group to another list leaves behind a node that its list took from it") {
    val tracker = createEventTracker()
    val c = tracker.createSpan("c")
    tracker.clear()
    val lists = new TwoLists
    val dyn: Inserter = child <-- Val(c)

    mount(lists.parent)

    withClue("L1 places dyn directly, then takes c from it as its own item:") {
      lists.items1.set(List(dyn))
      tracker.assertEvents(_.mounted("c")).clear()
      lists.items1.set(List(dyn, c))
      tracker.assertNoEvents.clear()
    }

    withClue("L2 takes dyn (now empty); c stays in L1:") {
      lists.items2.set(List(dyn))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          sentinel, span of "c", sentinel, // L1: [c]
          sentinel, sentinel, sentinel, sentinel // L2: [dyn: []]
        )
      )
    }
  }

  it("moving a re-stolen group to another list leaves behind a node that its list took from it") {
    val tracker = createEventTracker()
    val c = tracker.createSpan("c")
    tracker.clear()
    val lists = new TwoLists
    val dyn: Inserter = child <-- Val(c)

    mount(lists.parent)
    reStealIntoL1(lists, dyn, tracker)

    withClue("L1 takes c from dyn as its own item:") {
      lists.items1.set(List(dyn, c))
      tracker.assertNoEvents.clear()
    }

    withClue("L2 takes dyn (now empty); c stays in L1, as in the REFERENCE:") {
      lists.items2.set(List(dyn))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          sentinel, span of "c", sentinel, // L1: [c]
          sentinel, sentinel, sentinel, sentinel // L2: [dyn: []]
        )
      )
    }
  }
}
