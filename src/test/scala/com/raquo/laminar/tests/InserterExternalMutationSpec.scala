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
        .assertNoEvents // no lifecycle events: Laminar is unaware
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
        .assertNoEvents // Laminar wasn't told: `b` is not unmounted
        .clear()
    }

    withClue("A new list that drops the removed item reconciles cleanly, survivors untouched:") {
      bus.emit(List(a, c))
      expectNode(mainTag of (sentinel, span of "a", span of "c", sentinel))
      // The crux: `a` and `c` are left exactly in place – no re-mount, no re-run; `b` stays orphaned.
      tracker
        .assertNoEvents
        .clear()
    }

    withClue("The list is still fully live afterwards – a reorder is a move, not a re-mount:") {
      bus.emit(List(c, a))
      expectNode(mainTag of (sentinel, span of "c", span of "a", sentinel))
      tracker
        .assertNoEvents // reorder = move: no lifecycle events
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

  // `children <--` : a node externally INSERTED inside the span must not break the diff. Both
  // removal loops in `updateChildren` walk the DOM by sibling; an untracked node sitting at the
  // cursor is reported (AirstreamError) and stepped over (left in place) rather than aborting the
  // reconcile — so items after the intruder are still removed, and later emissions still land.

  it("`children <--` reconciles around an externally-inserted intruder, reporting it once") {
    val tracker = createEventTracker()
    val bus = new EventBus[List[HtmlElement]]

    val a = tracker.createSpan("a")
    val b = tracker.createSpan("b")
    val c = tracker.createSpan("c")
    tracker.clear()

    mount(mainTag(children <-- bus.events))
    expectNode(mainTag of sentinel)

    bus.emit(List(a, b))
    withClue("initial list mounts a, b:") {
      expectNode(mainTag of (sentinel, span of "a", span of "b", sentinel))
      tracker
        .assertEvents(
          _.mounted("a"),
          _.mounted("b")
        )
        .clear()
    }

    withClue("External code inserts an untracked node between our two content nodes:") {
      external.insertBefore(foreignEl("intruder"), referenceChild = b)
      expectNode(mainTag of (sentinel, span of "a", div of "intruder", span of "b", sentinel))
      tracker
        .assertNoEvents // a pure external DOM insertion: no lifecycle events
        .clear()
    }

    withClue("Dropping `b` still unmounts it, reports the intruder once, and leaves it in place:") {
      // The crux: the intruder sits at the cursor when the diff reaches `b`; without recovery the
      // reconcile would abort here and `b` would never leave. `a` is untouched (no re-mount).
      withCollectedAirstreamErrors { errors =>
        bus.emit(List(a))
        assert(errors.size == 1)
        assert(errors.head.getMessage.contains("not tracked by Laminar"))
      }
      expectNode(mainTag of (sentinel, span of "a", div of "intruder", sentinel))
      tracker
        .assertEvents(_.unmounted("b"))
        .clear()
    }

    withClue("The list is still live: a later emission reconciles around the retained intruder:") {
      bus.emit(List(a, c))
      // `c` mounts at the cursor (right after `a`, before the intruder); `a` is not re-mounted.
      expectNode(mainTag of (sentinel, span of "a", span of "c", div of "intruder", sentinel))
      tracker
        .assertEvents(_.mounted("c"))
        .clear()
    }

    withClue("On parent unmount, the tracked items tear down once; the intruder is left alone:") {
      unmount()
      tracker.assertEvents(
        _.unmounted("a"),
        _.unmounted("c")
      )
    }
  }

  // `children <--` : intruder position matters. At the HEAD of the span (right after the leading
  // sentinel) the diff walks onto the intruder as it reconciles the first item, so it is REPORTED
  // and stepped over on every emission whose first item is diffed via the removal path — and it
  // stays put. The reconcile itself stays fully correct (shrink and grow) around it.

  it("`children <--` reports a HEAD intruder on each reconcile, and reconciles correctly around it") {
    val tracker = createEventTracker()
    val bus = new EventBus[List[HtmlElement]]

    val a = tracker.createSpan("a")
    val b = tracker.createSpan("b")
    val c = tracker.createSpan("c")
    tracker.clear()

    mount(mainTag(children <-- bus.events))

    bus.emit(List(a, b))
    withClue("initial list mounts a, b:") {
      expectNode(mainTag of (sentinel, span of "a", span of "b", sentinel))
      tracker
        .assertEvents(
          _.mounted("a"),
          _.mounted("b")
        )
        .clear()
    }

    withClue("External code inserts an untracked node at the head, right after the sentinel:") {
      external.insertBefore(foreignEl("intruder"), referenceChild = a)
      expectNode(mainTag of (sentinel, div of "intruder", span of "a", span of "b", sentinel))
      tracker
        .assertNoEvents // a pure external DOM insertion: no lifecycle events
        .clear()
    }

    withClue("Dropping `b` reconciles past the head intruder: reports it once, unmounts `b`:") {
      withCollectedAirstreamErrors { errors =>
        bus.emit(List(a))
        assert(errors.size == 1)
        assert(errors.head.getMessage.contains("not tracked by Laminar"))
      }
      expectNode(mainTag of (sentinel, div of "intruder", span of "a", sentinel))
      tracker
        .assertEvents(_.unmounted("b"))
        .clear()
    }

    withClue("Growing the list reports the head intruder AGAIN (re-encountered), and mounts `c`:") {
      // The head intruder sits at the cursor every time `a` is diffed, so each such reconcile
      // reports it anew. `c` still lands correctly (right after `a`), `a` is not re-mounted.
      withCollectedAirstreamErrors { errors =>
        bus.emit(List(a, c))
        assert(errors.size == 1)
        assert(errors.head.getMessage.contains("not tracked by Laminar"))
      }
      expectNode(mainTag of (sentinel, div of "intruder", span of "a", span of "c", sentinel))
      tracker
        .assertEvents(_.mounted("c"))
        .clear()
    }

    withClue("On parent unmount, the tracked items tear down once; the intruder is left alone:") {
      unmount()
      tracker.assertEvents(
        _.unmounted("a"),
        _.unmounted("c")
      )
    }
  }

  // `children <--` : an intruder at the TAIL of the span (right before the trailing sentinel) is
  // the benign case – the count-terminated removal loops stop exactly when the cursor reaches it,
  // so it is NEVER visited and NEVER reported, no matter how the list shrinks or grows. It simply
  // rides along at the tail. This is the property that keeps a trailing external node from
  // spamming errors on every emission.

  it("`children <--` leaves a TAIL intruder untouched and unreported across reconciles") {
    val tracker = createEventTracker()
    val bus = new EventBus[List[HtmlElement]]

    val a = tracker.createSpan("a")
    val b = tracker.createSpan("b")
    val c = tracker.createSpan("c")
    tracker.clear()

    mount(mainTag(children <-- bus.events))

    bus.emit(List(a, b))
    withClue("initial list mounts a, b:") {
      expectNode(mainTag of (sentinel, span of "a", span of "b", sentinel))
      tracker
        .assertEvents(
          _.mounted("a"),
          _.mounted("b")
        )
        .clear()
    }

    withClue("External code inserts an untracked node at the tail, before the trailing sentinel:") {
      external.insertAfter(foreignEl("intruder"), referenceChild = b)
      expectNode(mainTag of (sentinel, span of "a", span of "b", div of "intruder", sentinel))
      tracker
        .assertNoEvents // a pure external DOM insertion: no lifecycle events
        .clear()
    }

    withClue("Dropping `b` unmounts it and stops at the tail intruder WITHOUT reporting it:") {
      withCollectedAirstreamErrors { errors =>
        bus.emit(List(a))
        assert(errors.isEmpty) // tail intruder is never visited by the count-terminated loop
      }
      expectNode(mainTag of (sentinel, span of "a", div of "intruder", sentinel))
      tracker
        .assertEvents(_.unmounted("b"))
        .clear()
    }

    withClue("Growing the list inserts `c` before the tail intruder, still no report:") {
      bus.emit(List(a, c))
      expectNode(mainTag of (sentinel, span of "a", span of "c", div of "intruder", sentinel))
      tracker
        .assertEvents(_.mounted("c"))
        .clear()
    }

    withClue("Clearing the list unmounts all tracked items, still without reporting the intruder:") {
      withCollectedAirstreamErrors { errors =>
        bus.emit(Nil)
        assert(errors.isEmpty)
      }
      expectNode(mainTag of (sentinel, div of "intruder", sentinel))
      tracker
        .assertEvents(
          _.unmounted("a"),
          _.unmounted("c")
        )
        .clear()
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
        .assertNoEvents // `a` still considered mounted: no event
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
        .assertNoEvents // a pure external DOM insertion: no lifecycle events
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
        .assertNoEvents // a pure external DOM insertion: no lifecycle events
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

  // `child <--` : the mirror of the removal case, and the reason single-node inserters CAN'T
  // report intruders the way `children <--` does. Their span has no trailing sentinel, so the
  // first untracked node is indistinguishable from a legitimate following sibling (the `span("Z")`
  // here, from the next modifier). So an externally-inserted node is neither reported nor removed:
  // the inserter sees its old child is no longer right after the sentinel, gives up on it (a
  // bounded leak, torn down only on parent unmount), and renders the new child at the sentinel —
  // in front of both the intruder and the orphaned old child. The same path backs `child.maybe`,
  // `text <--`, and `text.maybe` (all route through `ChildInserter.switchToChild`).

  it("CHARACTERIZATION `child <--` neither reports nor is broken by an external insertion (no trailing sentinel)") {
    val tracker = createEventTracker()
    val bus = new EventBus[HtmlElement]

    val a = tracker.createSpan("a")
    val b = tracker.createSpan("b")
    val c = tracker.createSpan("c")
    tracker.clear()

    // `span("Z")` is a legitimate sibling right after the inserter's content, with no marker
    // separating it from that content – exactly what makes intruder detection impossible here.
    val el = div("H", child <-- bus.events, span("Z"))
    mount(el)

    bus.emit(a)
    withClue("initial emission mounts `a`:") {
      expectNode(div of ("H", sentinel, span of "a", span of "Z"))
      tracker
        .assertEvents(_.mounted("a"))
        .clear()
    }

    withClue("External code inserts an untracked node between the sentinel and `a`:") {
      external.insertBefore(foreignEl("intruder"), referenceChild = a)
      expectNode(div of ("H", sentinel, div of "intruder", span of "a", span of "Z"))
      tracker
        .assertNoEvents // a pure external DOM insertion: no lifecycle events
        .clear()
    }

    withClue("Next emission: no error (can't tell intruder from `Z`), `b` renders at the sentinel:") {
      withCollectedAirstreamErrors { errors =>
        bus.emit(b)
        assert(errors.isEmpty) // no trailing sentinel -> nothing is flagged as an intruder
      }
      // `b` goes in right after the sentinel; the displaced `a` is orphaned in place (not
      // unmounted yet), so both linger in the DOM ahead of `Z`.
      expectNode(div of ("H", sentinel, span of "b", div of "intruder", span of "a", span of "Z"))
      tracker
        .assertEvents(_.mounted("b")) // `a` is NOT unmounted here – it leaks until parent unmount
        .clear()
    }

    withClue("A further emission still works: `b` is where we left it, so `c` replaces it cleanly:") {
      bus.emit(c)
      expectNode(div of ("H", sentinel, span of "c", div of "intruder", span of "a", span of "Z"))
      tracker
        .assertEvents(
          _.unmounted("b"),
          _.mounted("c")
        )
        .clear()
    }

    withClue("On parent unmount, the current child and the orphaned `a` are both torn down:") {
      unmount()
      tracker.assertEvents(
        _.unmounted("a"),
        _.unmounted("c")
      )
    }
  }

  // `children.command <--` : also a trailing-sentinel span, but it applies commands one by one
  // rather than diffing, so there is no lookup that could abort (RemoveAll / ReplaceAll go through
  // the same report-and-step-over path as `children <--`, covered above). The commands react to an
  // external insertion differently by design:
  //  - Append / Prepend are sentinel-anchored (before the trailing sentinel / after the leading
  //    one), so they are completely immune – they land correctly no matter what sits between.
  //  - Insert(atIndex) is POSITIONAL: it reads the span size from the DOM (deliberately, since the
  //    DOM is the source of truth for order), so an intruder counts as an occupied slot and shifts
  //    the index. This is inherent to a low-level positional API under external mutation; it does
  //    not error or corrupt, it just indexes over the DOM span as it actually is.

  it("`children.command <--` is immune to an intruder for Append/Prepend; Insert indexes over the DOM span") {
    val tracker = createEventTracker()
    val cmdBus = new EventBus[CollectionCommand[Node]]

    val a = tracker.createDiv("a")
    val b = tracker.createDiv("b")
    val x = tracker.createDiv("x")
    val y = tracker.createDiv("y")
    val p = tracker.createDiv("p")
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

    withClue("External code inserts an untracked node at the head, right after the sentinel:") {
      external.insertBefore(foreignEl("intruder"), referenceChild = a)
      expectNode(div of ("Hello", sentinel, div of "intruder", div of "a", div of "b", sentinel, div of "World"))
      tracker
        .assertNoEvents // a pure external DOM insertion: no lifecycle events
        .clear()
    }

    withClue("Insert(atIndex = 1) indexes over the DOM span, where the intruder holds slot 0:") {
      // So `x` lands after the intruder (DOM slot 1), i.e. before `a` – not between `a` and `b`.
      // No error: the intruder only inflates the span, it never pushes the index out of range.
      withCollectedAirstreamErrors { errors =>
        cmdBus.emit(CollectionCommand.Insert(x, atIndex = 1))
        assert(errors.isEmpty)
      }
      expectNode(div of ("Hello", sentinel, div of "intruder", div of "x", div of "a", div of "b", sentinel, div of "World"))
      tracker
        .assertEvents(_.mounted("x"))
        .clear()
    }

    withClue("Append is sentinel-anchored – `y` lands before the trailing sentinel regardless:") {
      cmdBus.emit(CollectionCommand.Append(y))
      expectNode(div of ("Hello", sentinel, div of "intruder", div of "x", div of "a", div of "b", div of "y", sentinel, div of "World"))
      tracker
        .assertEvents(_.mounted("y"))
        .clear()
    }

    withClue("Prepend is sentinel-anchored – `p` lands right after the leading sentinel, ahead of the intruder:") {
      cmdBus.emit(CollectionCommand.Prepend(p))
      expectNode(div of ("Hello", sentinel, div of "p", div of "intruder", div of "x", div of "a", div of "b", div of "y", sentinel, div of "World"))
      tracker
        .assertEvents(_.mounted("p"))
        .clear()
    }
  }
}
