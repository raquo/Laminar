package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.{CollectionCommand, Inserter}
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

/** Resilience to EXTERNAL DOM mutation.
  *
  * `InsertContext` is explicitly designed to "detect (and recover from) external changes to the
  * DOM" – i.e. changes made by third-party scripts, browser extensions, or hand-written interop
  * that bypass Laminar entirely. These tests make such changes with [[external]] (raw browser
  * API, so Laminar is NOT notified and its bookkeeping goes stale) and assert that Laminar then:
  *
  *  - '''recovers''': `child <--` / `children <--` keep producing the correct DOM on the next
  *    emission instead of throwing or corrupting their span, and without re-mounting the content
  *    that legitimately survived, and
  *  - '''reports''': a span with a trailing sentinel (`children <--` / `children.command <--`)
  *    treats an unrecognized node found inside it as an intruder – it is reported via
  *    `AirstreamError` and left in place (an external insertion into our span is not ours to
  *    silently remove), while our own tracked content is still torn down correctly.
  *
  * A note on lifecycle, which these tests pin precisely: once a node is externally ripped out of
  * the DOM, Laminar has no event to react to, so it CANNOT unmount it on the spot – the node stays
  * logically owned by its Laminar parent and is only torn down when that parent unmounts. That is
  * a bounded leak, inherent to bypassing Laminar, not an unbounded one; the tests assert both the
  * "not unmounted yet" state and the eventual cleanup on parent unmount, so the behaviour is
  * explicit rather than assumed.
  *
  * These paths had no direct coverage before (the intruder-report branch of
  * `removeContentMapNodesFromDom` was asserted nowhere), despite being a documented feature and
  * carrying an in-code `#TODO[Test]` in `ChildrenReceiverSpec`.
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
    val tracker = newLifecycleTracker()
    val bus = new EventBus[HtmlElement]

    val a = tracker.span("a")
    val b = tracker.span("b")

    val el = div("H", child <-- bus.events)
    mount(el)
    expectNode(div of ("H", sentinel))

    bus.emit(a)
    expectNode(div of ("H", sentinel, span of "a"))
    tracker.assertMountedOnce("a")

    withClue("External code yanks the child out of the DOM behind Laminar's back:") {
      external.removeChild(a)
      expectNode(div of ("H", sentinel))
      // Laminar has not been told, so it still considers `a` mounted (nothing to react to).
      tracker.assertMountedOnce("a")
    }

    withClue("Next emission notices the child is gone and inserts (does not try to replace):") {
      bus.emit(b)
      expectNode(div of ("H", sentinel, span of "b"))
      tracker.assertMountedOnce("b")
      // The recovery does NOT unmount the externally-removed `a` (it is no longer in our span
      // to tear down); `a` was never re-run either.
      tracker.assertMountedOnce("a")
    }

    withClue("On parent unmount, the surviving child and the orphaned `a` are both torn down:") {
      unmount()
      tracker.assertMountedThenUnmounted("a")
      tracker.assertMountedThenUnmounted("b")
    }
  }

  // `children <--` : an element externally removed from the middle of the list must not break
  // the next reconciliation, and the surviving siblings must NOT be re-mounted by it.
  // This exercises the `isContentEnd` count-correction in `updateChildren` (the stale contentMap
  // over-counts until the walk hits the trailing sentinel).

  it("`children <--` reconciles after an external removal, without re-mounting the survivors") {
    val tracker = newLifecycleTracker()
    val bus = new EventBus[List[HtmlElement]]

    val a = tracker.span("a")
    val b = tracker.span("b")
    val c = tracker.span("c")
    val d = tracker.span("d")

    mount(mainTag(children <-- bus.events))
    expectNode(mainTag of sentinel)

    bus.emit(List(a, b, c))
    expectNode(mainTag of (sentinel, span of "a", span of "b", span of "c", sentinel))
    tracker.assertMountedOnce("a")
    tracker.assertMountedOnce("b")
    tracker.assertMountedOnce("c")

    withClue("External code removes the middle element; Laminar's contentMap is now stale:") {
      external.removeChild(b)
      expectNode(mainTag of (sentinel, span of "a", span of "c", sentinel))
      tracker.assertMountedOnce("b") // not unmounted – Laminar wasn't told
    }

    withClue("A new list that drops the removed item reconciles cleanly, survivors untouched:") {
      bus.emit(List(a, c))
      expectNode(mainTag of (sentinel, span of "a", span of "c", sentinel))
      // The crux: `a` and `c` are left exactly in place – no re-mount, no re-run.
      tracker.assertMountedOnce("a")
      tracker.assertMountedOnce("c")
      tracker.assertMountedOnce("b") // still just orphaned, not unmounted
    }

    withClue("The list is still fully live afterwards – a reorder is a move, not a re-mount:") {
      bus.emit(List(c, a))
      expectNode(mainTag of (sentinel, span of "c", span of "a", sentinel))
      tracker.assertMountedOnce("a")
      tracker.assertMountedOnce("c")
    }

    withClue("... and grows with a brand-new element, still not touching the survivors:") {
      bus.emit(List(c, d, a))
      expectNode(mainTag of (sentinel, span of "c", span of "d", span of "a", sentinel))
      tracker.assertMountedOnce("a")
      tracker.assertMountedOnce("c")
      tracker.assertMountedOnce("d")
    }

    withClue("On parent unmount, all live items and the orphaned `b` are torn down exactly once:") {
      unmount()
      tracker.assertMountedThenUnmounted("a")
      tracker.assertMountedThenUnmounted("b") // finally unmounted
      tracker.assertMountedThenUnmounted("c")
      tracker.assertMountedThenUnmounted("d")
    }
  }

  // `children.command <--` : tolerates an external removal of one of its tracked nodes. The
  // trailing sentinel keeps the span's boundary intact, so later commands still land correctly.
  // Crucially we pin the lifecycle: RemoveAll unmounts only the nodes still IN the span; the
  // externally-removed one is not unmounted by RemoveAll (it left our span), only by the
  // eventual parent unmount.

  it("`children.command <--` tolerates an external removal: boundary intact, lifecycle precise") {
    val tracker = newLifecycleTracker()
    val cmdBus = new EventBus[CollectionCommand[Node]]

    val a = tracker.div("a")
    val b = tracker.div("b")
    val c = tracker.div("c")

    val el = div("Hello", children.command <-- cmdBus.events, div("World"))
    mount(el)

    cmdBus.emit(CollectionCommand.Append(a))
    cmdBus.emit(CollectionCommand.Append(b))
    expectNode(div of ("Hello", sentinel, div of "a", div of "b", sentinel, div of "World"))
    tracker.assertMountedOnce("a")
    tracker.assertMountedOnce("b")

    withClue("External code removes a tracked node (Laminar not told):") {
      external.removeChild(a)
      expectNode(div of ("Hello", sentinel, div of "b", sentinel, div of "World"))
      tracker.assertMountedOnce("a") // still considered mounted
    }

    withClue("Append still lands before the trailing sentinel (boundary did not drift):") {
      cmdBus.emit(CollectionCommand.Append(c))
      expectNode(div of ("Hello", sentinel, div of "b", div of "c", sentinel, div of "World"))
      tracker.assertMountedOnce("c")
    }

    withClue("RemoveAll: tolerated (no error); unmounts the in-span nodes, NOT the departed one:") {
      withCollectedAirstreamErrors { errors =>
        cmdBus.emit(CollectionCommand.RemoveAll)
        assert(errors.isEmpty)
      }
      expectNode(div of ("Hello", sentinel, sentinel, div of "World"))
      tracker.assertMountedThenUnmounted("b") // was in the span -> torn down
      tracker.assertMountedThenUnmounted("c") // was in the span -> torn down
      tracker.assertMountedOnce("a") // left the span externally -> NOT unmounted by RemoveAll
    }

    withClue("The orphaned `a` is finally torn down when its parent unmounts (bounded leak):") {
      unmount()
      tracker.assertMountedThenUnmounted("a")
    }
  }

  // The intruder-report path: a node externally INSERTED inside a trailing-sentinel span is an
  // unauthorized addition. On teardown Laminar reports it (AirstreamError) and steps over it,
  // leaving it in place, while still unmounting all of its own tracked content exactly once.

  it("`children.command <--` reports an externally-inserted intruder node on RemoveAll") {
    val tracker = newLifecycleTracker()
    val cmdBus = new EventBus[CollectionCommand[Node]]

    val a = tracker.div("a")
    val b = tracker.div("b")

    val el = div("Hello", children.command <-- cmdBus.events, div("World"))
    mount(el)

    cmdBus.emit(CollectionCommand.Append(a))
    cmdBus.emit(CollectionCommand.Append(b))
    tracker.assertMountedOnce("a")
    tracker.assertMountedOnce("b")

    withClue("External code inserts an untracked node between our two content nodes:") {
      external.insertBefore(foreignEl("intruder"), referenceChild = b)
      expectNode(div of ("Hello", sentinel, div of "a", div of "intruder", div of "b", sentinel, div of "World"))
    }

    withClue("RemoveAll removes and unmounts our content, reports the intruder, leaves it in place:") {
      withCollectedAirstreamErrors { errors =>
        cmdBus.emit(CollectionCommand.RemoveAll)
        assert(errors.size == 1)
        assert(errors.head.getMessage.contains("not tracked by Laminar"))
      }
      expectNode(div of ("Hello", sentinel, div of "intruder", sentinel, div of "World"))
      tracker.assertMountedThenUnmounted("a")
      tracker.assertMountedThenUnmounted("b")
    }
  }

  // Same intruder-report path, reached through a TAKEOVER of a `children <--` span (rather than
  // a command), to show it is a general teardown property. Because the takeover rides the
  // element's own unmount / remount, the old children mount twice and unmount twice in total.

  it("takeover of a `children <--` span reports an externally-inserted intruder while tearing it down") {
    val tracker = newLifecycleTracker()
    val childrenBus = new EventBus[List[Node]]
    val childBus = new EventBus[String]

    val a = tracker.div("a")
    val b = tracker.div("b")

    var dynamicInserter: Inserter = children <-- childrenBus.events
    val childInserter: Inserter = child <-- childBus.events.map(tracker.div(_))

    val el = div("Hello ", onMountInsert(_ => dynamicInserter), " world")
    mount(el)

    childrenBus.emit(List(a, b))
    expectNode(div of ("Hello ", sentinel, div of "a", div of "b", sentinel, " world"))
    tracker.assertMountedOnce("a")
    tracker.assertMountedOnce("b")

    withClue("External code inserts an untracked node inside the `children <--` span:") {
      external.insertBefore(foreignEl("intruder"), referenceChild = b)
      expectNode(div of ("Hello ", sentinel, div of "a", div of "intruder", div of "b", sentinel, " world"))
    }

    // Takeover rides an unmount / remount: `a` and `b` unmount with the element, then re-mount
    // with it (their DOM nodes are preserved), before the takeover finally tears them down.
    unmount()
    tracker.assertMountedThenUnmounted("a")
    tracker.assertMountedThenUnmounted("b")

    dynamicInserter = childInserter
    mount(el)

    withClue("`child <--` takes over: old children are torn down, intruder is reported and kept:") {
      withCollectedAirstreamErrors { errors =>
        childBus.emit("k")
        assert(errors.size == 1)
        assert(errors.head.getMessage.contains("not tracked by Laminar"))
      }
      expectNode(div of ("Hello ", sentinel, div of "k", div of "intruder", " world"))
      tracker.assertMountedOnce("k")
      // Mounted once with the initial mount, unmounted on the element's unmount, re-mounted on
      // remount, then unmounted by the takeover: exactly two of each.
      tracker.assertCounts("a", mounts = 2, unmounts = 2)
      tracker.assertCounts("b", mounts = 2, unmounts = 2)
    }
  }
}
