package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.{CollectionCommand, Inserter}
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

/** Resilience to EXTERNAL DOM mutation — changes by third-party scripts, extensions, or interop
  * that bypass Laminar (made here via [[external]], so Laminar's bookkeeping goes stale).
  *
  * Scope: that Laminar then RECOVERS (produces correct DOM on the next emission, without
  * re-mounting survivors) and REPORTS (an unrecognized node inside a trailing-sentinel span is
  * flagged via `AirstreamError` and left in place), with lifecycle pinned precisely — including
  * the bounded leak whereby an externally-removed node is torn down only on parent unmount. These
  * report paths were previously uncovered. See notes/Testing.md for assertion conventions.
  */
class InserterExternalMutationSpec extends UnitSpec {

  /** A raw element created outside Laminar, to stand in for third-party DOM content. */
  private def foreignEl(text: String): dom.Element = {
    val el = dom.document.createElement("div")
    el.textContent = text
    el
  }

  // `child <--` : the previous child can be externally removed; the next emission must fall
  // back to inserting (rather than replacing a node that is no longer where we left it), and
  // must not disturb the lifecycle of the surviving content.

  it("`child <--` recovers when its child is externally removed") {
    val tracker = createEventTracker()
    val bus = new EventBus[HtmlElement]

    val a = tracker.createSpan("a")
    val b = tracker.createSpan("b")

    val el = div("H", child <-- bus.events)
    mount(el)
    expectNode(div of ("H", sentinel))

    bus.emit(a)
    withClue("initial emission mounts `a` (both spans were built up front):") {
      expectNode(div of ("H", sentinel, span of "a"))
      tracker
        .assertEvents(
          _.elementCreated("a"),
          _.elementCreated("b"),
          _.mounted("a")
        )
        .clear()
    }

    withClue("External code yanks the child out of the DOM behind Laminar's back:") {
      external.removeChild(a)
      expectNode(div of ("H", sentinel))
      // Laminar has not been told, so it does NOT unmount `a` (nothing to react to).
      tracker
        .assertEvents() // no lifecycle events: Laminar is unaware
        .clear()
    }

    withClue("Next emission notices the child is gone and inserts (does not try to replace):") {
      bus.emit(b)
      expectNode(div of ("H", sentinel, span of "b"))
      // Only `b` mounts; the externally-removed `a` is neither unmounted (no longer in our span
      // to tear down) nor re-run.
      tracker
        .assertEvents(_.mounted("b"))
        .clear()
    }

    withClue("On parent unmount, the surviving child and the orphaned `a` are both torn down:") {
      unmount()
      tracker.assertEvents(
        _.unmounted("a"),
        _.unmounted("b")
      )
    }
  }

  // `children <--` : an element externally removed from the middle of the list must not break
  // the next reconciliation, and the surviving siblings must NOT be re-mounted by it.
  // This exercises the `isContentEnd` count-correction in `updateChildren` (the stale contentMap
  // over-counts until the walk hits the trailing sentinel).

  it("`children <--` reconciles after an external removal, without re-mounting the survivors") {
    val tracker = createEventTracker()
    val bus = new EventBus[List[HtmlElement]]

    val a = tracker.createSpan("a")
    val b = tracker.createSpan("b")
    val c = tracker.createSpan("c")
    val d = tracker.createSpan("d")

    tracker.clear()

    mount(mainTag(children <-- bus.events))
    expectNode(mainTag of sentinel)

    bus.emit(List(a, b, c))
    withClue("initial list mounts a, b, c:") {
      expectNode(mainTag of (sentinel, span of "a", span of "b", span of "c", sentinel))
      tracker
        .assertEvents(
          _.mounted("a"),
          _.mounted("b"),
          _.mounted("c")
        )
        .clear()
    }

    withClue("External code removes the middle element; Laminar's contentMap is now stale:") {
      external.removeChild(b)
      expectNode(mainTag of (sentinel, span of "a", span of "c", sentinel))
      tracker
        .assertEvents() // Laminar wasn't told: `b` is not unmounted
        .clear()
    }

    withClue("A new list that drops the removed item reconciles cleanly, survivors untouched:") {
      bus.emit(List(a, c))
      expectNode(mainTag of (sentinel, span of "a", span of "c", sentinel))
      // The crux: `a` and `c` are left exactly in place – no re-mount, no re-run; `b` stays orphaned.
      tracker
        .assertEvents()
        .clear()
    }

    withClue("The list is still fully live afterwards – a reorder is a move, not a re-mount:") {
      bus.emit(List(c, a))
      expectNode(mainTag of (sentinel, span of "c", span of "a", sentinel))
      tracker
        .assertEvents() // reorder = move: no lifecycle events
        .clear()
    }

    withClue("... and grows with a brand-new element, still not touching the survivors:") {
      bus.emit(List(c, d, a))
      expectNode(mainTag of (sentinel, span of "c", span of "d", span of "a", sentinel))
      // Only `d` mounts; `c` and `a` are moved, not re-run.
      tracker
        .assertEvents(_.mounted("d"))
        .clear()
    }

    withClue("On parent unmount, all live items and the orphaned `b` are torn down exactly once:") {
      unmount()
      // #Note: teardown walks the contentMap in its original insertion order (a, b, c, d), not
      // the current DOM order — the orphaned `b` is torn down in its old slot along with the rest.
      tracker.assertEvents(
        _.unmounted("a"),
        _.unmounted("b"),
        _.unmounted("c"),
        _.unmounted("d")
      )
    }
  }

  // `children.command <--` : tolerates an external removal of one of its tracked nodes. The
  // trailing sentinel keeps the span's boundary intact, so later commands still land correctly.
  // Crucially we pin the lifecycle: RemoveAll unmounts only the nodes still IN the span; the
  // externally-removed one is not unmounted by RemoveAll (it left our span), only by the
  // eventual parent unmount.

  it("`children.command <--` tolerates an external removal: boundary intact, lifecycle precise") {
    val tracker = createEventTracker()
    val cmdBus = new EventBus[CollectionCommand[Node]]

    val a = tracker.createDiv("a")
    val b = tracker.createDiv("b")
    val c = tracker.createDiv("c")
    tracker.clear()

    val el = div("Hello", children.command <-- cmdBus.events, div("World"))
    mount(el)

    cmdBus.emit(CollectionCommand.Append(a))
    cmdBus.emit(CollectionCommand.Append(b))
    withClue("initial: Append a then b:") {
      expectNode(div of ("Hello", sentinel, div of "a", div of "b", sentinel, div of "World"))
      tracker
        .assertEvents(
          _.mounted("a"),
          _.mounted("b")
        )
        .clear()
    }

    withClue("External code removes a tracked node (Laminar not told):") {
      external.removeChild(a)
      expectNode(div of ("Hello", sentinel, div of "b", sentinel, div of "World"))
      tracker
        .assertEvents() // `a` still considered mounted: no event
        .clear()
    }

    withClue("Append still lands before the trailing sentinel (boundary did not drift):") {
      cmdBus.emit(CollectionCommand.Append(c))
      expectNode(div of ("Hello", sentinel, div of "b", div of "c", sentinel, div of "World"))
      tracker
        .assertEvents(_.mounted("c"))
        .clear()
    }

    withClue("RemoveAll: tolerated (no error); unmounts the in-span nodes, NOT the departed one:") {
      withCollectedAirstreamErrors { errors =>
        cmdBus.emit(CollectionCommand.RemoveAll)
        assert(errors.isEmpty)
      }
      expectNode(div of ("Hello", sentinel, sentinel, div of "World"))
      // `b` and `c` were in the span -> unmounted; `a` left the span externally -> NOT unmounted here.
      tracker
        .assertEvents(
          _.unmounted("b"),
          _.unmounted("c")
        )
        .clear()
    }

    withClue("The orphaned `a` is finally torn down when its parent unmounts (bounded leak):") {
      unmount()
      tracker.assertEvents(_.unmounted("a"))
    }
  }

  // The intruder-report path: a node externally INSERTED inside a trailing-sentinel span is an
  // unauthorized addition. On teardown Laminar reports it (AirstreamError) and steps over it,
  // leaving it in place, while still unmounting all of its own tracked content exactly once.

  it("`children.command <--` reports an externally-inserted intruder node on RemoveAll") {
    val tracker = createEventTracker()
    val cmdBus = new EventBus[CollectionCommand[Node]]

    val a = tracker.createDiv("a")
    val b = tracker.createDiv("b")
    tracker.clear()

    val el = div("Hello", children.command <-- cmdBus.events, div("World"))
    mount(el)

    cmdBus.emit(CollectionCommand.Append(a))
    cmdBus.emit(CollectionCommand.Append(b))
    withClue("initial: Append a then b:") {
      expectNode(div of ("Hello", sentinel, div of "a", div of "b", sentinel, div of "World"))
      tracker
        .assertEvents(
          _.mounted("a"),
          _.mounted("b")
        )
        .clear()
    }

    withClue("External code inserts an untracked node between our two content nodes:") {
      external.insertBefore(foreignEl("intruder"), referenceChild = b)
      expectNode(div of ("Hello", sentinel, div of "a", div of "intruder", div of "b", sentinel, div of "World"))
      tracker
        .assertEvents() // a pure external DOM insertion: no lifecycle events
        .clear()
    }

    withClue("RemoveAll removes and unmounts our content, reports the intruder, leaves it in place:") {
      withCollectedAirstreamErrors { errors =>
        cmdBus.emit(CollectionCommand.RemoveAll)
        assert(errors.size == 1)
        assert(errors.head.getMessage.contains("not tracked by Laminar"))
      }
      expectNode(div of ("Hello", sentinel, div of "intruder", sentinel, div of "World"))
      tracker.assertEvents(
        _.unmounted("a"),
        _.unmounted("b")
      )
    }
  }

  // Same intruder-report path, reached through a TAKEOVER of a `children <--` span (rather than
  // a command), to show it is a general teardown property. Because the takeover rides the
  // element's own unmount / remount, the old children mount twice and unmount twice in total.

  it("takeover of a `children <--` span reports an externally-inserted intruder while tearing it down") {
    val tracker = createEventTracker()
    val childrenBus = new EventBus[List[Node]]
    val childBus = new EventBus[String]

    val a = tracker.createDiv("a")
    val b = tracker.createDiv("b")
    tracker.clear()

    var dynamicInserter: Inserter = children <-- childrenBus.events
    val childInserter: Inserter = child <-- childBus.events.map(tracker.createDiv(_))

    val el = div("Hello ", onMountInsert(_ => dynamicInserter), " world")
    mount(el)

    childrenBus.emit(List(a, b))
    withClue("initial: children <-- renders a, b:") {
      expectNode(div of ("Hello ", sentinel, div of "a", div of "b", sentinel, " world"))
      tracker
        .assertEvents(
          _.mounted("a"),
          _.mounted("b")
        )
        .clear()
    }

    withClue("External code inserts an untracked node inside the `children <--` span:") {
      external.insertBefore(foreignEl("intruder"), referenceChild = b)
      expectNode(div of ("Hello ", sentinel, div of "a", div of "intruder", div of "b", sentinel, " world"))
      tracker
        .assertEvents() // a pure external DOM insertion: no lifecycle events
        .clear()
    }

    withClue("element unmount: a and b unmount with it (their DOM nodes are preserved for remount):") {
      unmount()
      tracker
        .assertEvents(
          _.unmounted("a"),
          _.unmounted("b")
        )
        .clear()
    }

    dynamicInserter = childInserter
    mount(el)
    withClue("remount restores and re-mounts a and b (before the takeover):") {
      tracker
        .assertEvents(
          _.mounted("a"),
          _.mounted("b")
        )
        .clear()
    }

    withClue("`child <--` takes over: old children are torn down, intruder is reported and kept:") {
      withCollectedAirstreamErrors { errors =>
        childBus.emit("k")
        assert(errors.size == 1)
        assert(errors.head.getMessage.contains("not tracked by Laminar"))
      }
      expectNode(div of ("Hello ", sentinel, div of "k", div of "intruder", " world"))
      tracker.assertEvents(
        _.elementCreated("k"),
        _.unmounted("a"),
        _.unmounted("b"),
        _.mounted("k")
      )
    }
  }
}
