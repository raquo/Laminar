package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.utils.{EventTracker, UnitSpec}

/** A moved dynamic inserter must behave like a moved element on its host's next activation: the
  * inserter re-renders first, and content it no longer wants is dropped WITHOUT ever mounting.
  * These tests pin that a move never makes stale content mount (running its mount hooks) only to
  * be unmounted moments later by the inserter's own re-render of a value that changed while it
  * was inactive.
  */
class NestedGroupActivationOrderSpec extends UnitSpec {

  // -- Fixture: a `child <--` that builds a FRESH tracked span per rendered value (the common
  //    `signal.map(render)` idiom). A value that changes while the inserter is inactive is rendered
  //    on the next activation, replacing the previous span. Each span is tagged with the render
  //    ordinal so the two generations are distinguishable in the log.

  private class FreshRenderer(tracker: EventTracker) {
    val valueVar = Var("v")
    private var renderCount = 0
    val inserter: Inserter = child <-- valueVar.signal.map { v =>
      renderCount += 1
      tracker.createSpan(s"$v$renderCount")
    }
  }

  it("REFERENCE: a group that was never moved re-renders BEFORE its stale content can mount on remount") {
    // Baseline for the tests below: a freshly placed group's inserter re-runs first on remount, so
    // the span rendered before the unmount is discarded without a mount, and only the new one mounts.
    val tracker = createEventTracker()
    val r = new FreshRenderer(tracker)
    val items = Var[List[Inserter]](Nil)
    val root = div(div("LIST", children <-- items.signal))

    mount(root)

    withClue("first placement renders and mounts the first span:") {
      items.set(List(r.inserter))
      tracker.assertEvents(_.elementCreated("v1"), _.mounted("v1")).clear()
      expectNode(div.of(div.of("LIST", sentinel, sentinel, span of "v1", sentinel, sentinel)))
    }

    withClue("unmount, then the value changes while inactive (not observed yet):") {
      unmount()
      tracker.assertEvents(_.unmounted("v1")).clear()
      r.valueVar.set("w")
      tracker.assertNoEvents.clear()
    }

    withClue("remount: the inserter re-renders first, so the stale span is dropped without mounting:") {
      mount(root)
      tracker.assertEvents(_.elementCreated("w2"), _.mounted("w2")).clear()
      expectNode(div.of(div.of("LIST", sentinel, sentinel, span of "w2", sentinel, sentinel)))
    }
  }

  it("REFERENCE: a plain ELEMENT moved onto an UNMOUNTED element re-renders before its stale content can mount") {
    // The plain-element analogy the moved-group tests below must match: the element's own owner
    // keeps its inserter ahead of its content, so mounting it re-renders first and the span
    // rendered before the move is discarded without mounting.
    val tracker = createEventTracker()
    val r = new FreshRenderer(tracker)
    val items = Var[List[Inserter]](Nil)
    val inner = div("INNER", r.inserter)
    val host = div("HOST") // stays detached until the last step
    val root = div(div("LIST", children <-- items.signal))

    mount(root)

    withClue("first placement in the mounted LIST:") {
      items.set(List(inner))
      tracker.assertEvents(_.elementCreated("v1"), _.mounted("v1")).clear()
    }

    withClue("move onto the detached HOST: the content unmounts exactly once:") {
      host.amend(inner)
      items.set(Nil)
      tracker.assertEvents(_.unmounted("v1")).clear()
    }

    withClue("the value changes while HOST is detached (not observed yet):") {
      r.valueVar.set("w")
      tracker.assertNoEvents.clear()
    }

    withClue("mounting HOST: only the re-rendered span mounts:") {
      root.amend(host)
      tracker.assertEvents(_.elementCreated("w2"), _.mounted("w2")).clear()
      expectNode(
        div.of(
          div.of("LIST", sentinel, sentinel),
          div.of("HOST", div.of("INNER", sentinel, span of "w2"))
        )
      )
    }
  }

  it("a group moved onto an UNMOUNTED element does not mount its stale content when that element mounts") {
    // Reference: `unmountedEl.amend(div(child <-- signal.map(render)))`, then mounting `unmountedEl`
    // after the value changed, re-renders and mounts ONLY the new span. A moved group must do the same.
    val tracker = createEventTracker()
    val r = new FreshRenderer(tracker)
    val items = Var[List[Inserter]](Nil)
    val host = div("HOST") // stays detached until the last step
    val root = div(div("LIST", children <-- items.signal))

    mount(root)

    withClue("first placement in the mounted LIST:") {
      items.set(List(r.inserter))
      tracker.assertEvents(_.elementCreated("v1"), _.mounted("v1")).clear()
    }

    withClue("demote onto the detached HOST: the span relocates and its content unmounts exactly once:") {
      host.amend(r.inserter)
      items.set(Nil) // no-op removal: the span already left LIST
      tracker.assertEvents(_.unmounted("v1")).clear()
      expectNode(div.of(div.of("LIST", sentinel, sentinel)))
      expectNode(host.ref, div.of("HOST", sentinel, span of "v1", sentinel))
    }

    withClue("the value changes while HOST is detached (not observed yet):") {
      r.valueVar.set("w")
      tracker.assertNoEvents.clear()
    }

    withClue("mounting HOST: the inserter re-renders first; the stale span v1 must NOT mount, only w2 does:") {
      root.amend(host)
      tracker.assertEvents(_.elementCreated("w2"), _.mounted("w2")).clear()
      expectNode(
        div.of(
          div.of("LIST", sentinel, sentinel),
          div.of("HOST", sentinel, span of "w2", sentinel)
        )
      )
    }

    withClue("still live on HOST:") {
      r.valueVar.set("x")
      tracker.assertEvents(_.elementCreated("x3"), _.mounted("x3"), _.unmounted("w2")).clear()
      expectNode(
        div.of(
          div.of("LIST", sentinel, sentinel),
          div.of("HOST", sentinel, span of "x3", sentinel)
        )
      )
    }
  }

  it("a group with content stolen from an UNMOUNTED host into a mounted list does not mount its stale content") {
    // Reference: an element with rendered-then-unmounted content whose value changed meanwhile,
    // moved into a mounted list, re-renders on the way in and mounts ONLY the new span.
    val tracker = createEventTracker()
    val r = new FreshRenderer(tracker)
    val items2 = Var[List[Inserter]](Nil)
    val root1 = div(div("P", r.inserter))
    val root2 = div(div("L2", children <-- items2.signal))

    withClue("render in P, then unmount P: the span unmounts exactly once:") {
      mount(root1)
      tracker.assertEvents(_.elementCreated("v1"), _.mounted("v1")).clear()
      unmount()
      tracker.assertEvents(_.unmounted("v1")).clear()
      expectNode(root1.ref, div.of(div.of("P", sentinel, span of "v1")))
    }

    withClue("the value changes while P is unmounted (not observed yet):") {
      r.valueVar.set("w")
      tracker.assertNoEvents.clear()
    }

    withClue("L2 (mounted) steals the group out of the unmounted P: only the re-rendered span mounts:") {
      mount(root2)
      tracker.clear()
      items2.set(List(r.inserter))
      tracker.assertEvents(_.elementCreated("w2"), _.mounted("w2")).clear()
      expectNode(div.of(div.of("L2", sentinel, sentinel, span of "w2", sentinel, sentinel)))
      expectNode(root1.ref, div.of(div.of("P")))
    }
  }

  it("a group stolen between two MOUNTED lists does not mount its stale content on the new host's later remount") {
    // The seamless add-first steal itself is silent (no re-mount). But the new host must ALSO keep
    // behaving like the reference on every later unmount / remount cycle: re-render first, never
    // mount the stale span.
    val tracker = createEventTracker()
    val r = new FreshRenderer(tracker)
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val root = div(
      div("L1", children <-- items1.signal),
      div("L2", children <-- items2.signal)
    )

    mount(root)

    withClue("first placement in L1:") {
      items1.set(List(r.inserter))
      tracker.assertEvents(_.elementCreated("v1"), _.mounted("v1")).clear()
    }

    withClue("add-first steal into L2 is seamless:") {
      items2.set(List(r.inserter))
      items1.set(Nil)
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "v1", sentinel, sentinel)
        )
      )
    }

    withClue("unmount, then the value changes while inactive (not observed yet):") {
      unmount()
      tracker.assertEvents(_.unmounted("v1")).clear()
      r.valueVar.set("w")
      tracker.assertNoEvents.clear()
    }

    withClue("remount: as in the REFERENCE, the stale span must NOT mount before the inserter re-renders:") {
      mount(root)
      tracker.assertEvents(_.elementCreated("w2"), _.mounted("w2")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "w2", sentinel, sentinel)
        )
      )
    }
  }

  // -- Fixture: stale content whose mount has a side effect on an UNRELATED live list --

  // While mounted, the group's content `c` claims `e` via its own `child.maybe <--` binding.
  // While the group is inactive, `c` leaves the group's source and starts claiming `e`, which
  // meanwhile lives in an unrelated live list. If stale `c` ever mounts, its binding steals `e`
  // from that list, and when the group then drops `c`, `e` leaves the page with it for good.

  private class StaleClaimer(tracker: EventTracker) {
    val e: Div = tracker.createDiv("e")
    val claimE: Var[Option[HtmlElement]] = Var(None)
    val c: Div = tracker.createDiv("c", child.maybe <-- claimE.signal)
    val groupItems: Var[List[HtmlElement]] = Var(List(c))
    val group: Inserter = children <-- groupItems.signal
    tracker.clear()

    /** Call while the group is inactive: drop `c` from the group, and make `c` claim `e`. */
    def makeContentStale(): Unit = {
      Var.set(
        groupItems -> Nil,
        claimE -> Some(e)
      )
    }
  }

  it("REFERENCE: a never-moved group's stale content does not mount, so it can't steal from an unrelated list") {
    // Baseline for the tests below: `e` stays in L3, untouched, when the group's host remounts.
    val tracker = createEventTracker()
    val s = new StaleClaimer(tracker)
    val showL1 = Var(true)
    val items3 = Var[List[HtmlElement]](Nil)
    val l1 = div("L1", s.group)
    val l3 = div("L3", children <-- items3.signal)
    val root = div(child.maybe <-- showL1.signal.map(if (_) Some(l1) else None), l3)

    withClue("mount: the group renders and mounts `c`:") {
      mount(root)
      tracker.assertEvents(_.mounted("c")).clear()
    }

    withClue("hide L1, make `c` stale while inactive, and put `e` in the unrelated live L3:") {
      showL1.set(false)
      tracker.assertEvents(_.unmounted("c")).clear()
      s.makeContentStale()
      tracker.assertNoEvents.clear()
      items3.set(List(s.e))
      tracker.assertEvents(_.mounted("e")).clear()
    }

    withClue("show L1 again: the group re-renders first, stale `c` never mounts, `e` stays in L3:") {
      showL1.set(true)
      tracker.assertNoEvents.clear()
      expectNode(l1.ref, div.of("L1", sentinel, sentinel))
      expectNode(l3.ref, div.of("L3", sentinel, div of "e", sentinel))
    }
  }

  it("a group stolen from an UNMOUNTED host into a mounted list does not let stale content steal from an unrelated list") {
    val tracker = createEventTracker()
    val s = new StaleClaimer(tracker)
    val showL1 = Var(true)
    val items1 = Var[List[Inserter]](List(s.group))
    val items2 = Var[List[Inserter]](Nil)
    val items3 = Var[List[HtmlElement]](Nil)
    val l1 = div("L1", children <-- items1.signal)
    val l2 = div("L2", children <-- items2.signal)
    val l3 = div("L3", children <-- items3.signal)
    val root = div(child.maybe <-- showL1.signal.map(if (_) Some(l1) else None), l2, l3)

    withClue("mount: the group renders and mounts `c` in L1:") {
      mount(root)
      tracker.assertEvents(_.mounted("c")).clear()
    }

    withClue("hide L1, make `c` stale while inactive, and put `e` in the unrelated live L3:") {
      showL1.set(false)
      tracker.assertEvents(_.unmounted("c")).clear()
      s.makeContentStale()
      tracker.assertNoEvents.clear()
      items3.set(List(s.e))
      tracker.assertEvents(_.mounted("e")).clear()
    }

    withClue("mounted L2 steals the group out of hidden L1: stale `c` must not mount and steal `e`:") {
      items2.set(List(s.group))
      tracker.assertNoEvents.clear()
      expectNode(l2.ref, div.of("L2", sentinel, sentinel, sentinel, sentinel))
      expectNode(l3.ref, div.of("L3", sentinel, div of "e", sentinel))
    }
  }

  it("a group moved from an UNMOUNTED host onto a mounted element does not let stale content steal from an unrelated list") {
    val tracker = createEventTracker()
    val s = new StaleClaimer(tracker)
    val showL1 = Var(true)
    val items1 = Var[List[Inserter]](List(s.group))
    val items3 = Var[List[HtmlElement]](Nil)
    val l1 = div("L1", children <-- items1.signal)
    val host = div("HOST")
    val l3 = div("L3", children <-- items3.signal)
    val root = div(child.maybe <-- showL1.signal.map(if (_) Some(l1) else None), host, l3)

    withClue("mount: the group renders and mounts `c` in L1:") {
      mount(root)
      tracker.assertEvents(_.mounted("c")).clear()
    }

    withClue("hide L1, make `c` stale while inactive, and put `e` in the unrelated live L3:") {
      showL1.set(false)
      tracker.assertEvents(_.unmounted("c")).clear()
      s.makeContentStale()
      tracker.assertNoEvents.clear()
      items3.set(List(s.e))
      tracker.assertEvents(_.mounted("e")).clear()
    }

    withClue("move the group onto mounted HOST: stale `c` must not mount and steal `e`:") {
      host.amend(s.group)
      tracker.assertNoEvents.clear()
      expectNode(host.ref, div.of("HOST", sentinel, sentinel))
      expectNode(l3.ref, div.of("L3", sentinel, div of "e", sentinel))
    }
  }

  it("a group moved onto an UNMOUNTED element does not let stale content steal from an unrelated list when that element mounts") {
    val tracker = createEventTracker()
    val s = new StaleClaimer(tracker)
    val showL1 = Var(true)
    val items1 = Var[List[Inserter]](List(s.group))
    val items3 = Var[List[HtmlElement]](Nil)
    val l1 = div("L1", children <-- items1.signal)
    val host = div("HOST") // stays detached until the last step
    val l3 = div("L3", children <-- items3.signal)
    val root = div(child.maybe <-- showL1.signal.map(if (_) Some(l1) else None), l3)

    withClue("mount: the group renders and mounts `c` in L1:") {
      mount(root)
      tracker.assertEvents(_.mounted("c")).clear()
    }

    withClue("hide L1, make `c` stale while inactive, and put `e` in the unrelated live L3:") {
      showL1.set(false)
      tracker.assertEvents(_.unmounted("c")).clear()
      s.makeContentStale()
      tracker.assertNoEvents.clear()
      items3.set(List(s.e))
      tracker.assertEvents(_.mounted("e")).clear()
    }

    withClue("move the group onto the detached HOST (inactive to inactive):") {
      host.amend(s.group)
      tracker.assertNoEvents.clear()
    }

    withClue("mount HOST: stale `c` must not mount and steal `e`:") {
      root.amend(host)
      tracker.assertNoEvents.clear()
      expectNode(host.ref, div.of("HOST", sentinel, sentinel))
      expectNode(l3.ref, div.of("L3", sentinel, div of "e", sentinel))
    }
  }

  // -- Depth 2: the stale content is itself a nested group, not an element --

  // A fix must cover every nesting depth: moving the outer group also moves (and re-registers)
  // the inner group, so the inner group must not run ahead of the outer group's re-render either.

  it("REFERENCE depth-2: a never-moved group's stale NESTED group does not activate, so it can't steal from an unrelated list") {
    val tracker = createEventTracker()
    val e = tracker.createDiv("e")
    tracker.clear()
    val claimE = Var[Option[HtmlElement]](None)
    val inner: Inserter = child.maybe <-- claimE.signal // claims `e` while active
    val outerItems = Var[List[Inserter]](List(inner))
    val outer: Inserter = children <-- outerItems.signal
    val showL1 = Var(true)
    val items3 = Var[List[HtmlElement]](Nil)
    val l1 = div("L1", outer)
    val l3 = div("L3", children <-- items3.signal)
    val root = div(child.maybe <-- showL1.signal.map(if (_) Some(l1) else None), l3)

    withClue("mount, hide L1, make `inner` stale while inactive, and put `e` in the unrelated live L3:") {
      mount(root)
      showL1.set(false)
      Var.set(
        outerItems -> Nil,
        claimE -> Some(e)
      )
      tracker.assertNoEvents.clear()
      items3.set(List(e))
      tracker.assertEvents(_.mounted("e")).clear()
    }

    withClue("show L1 again: the outer group re-renders first, stale `inner` never activates:") {
      showL1.set(true)
      tracker.assertNoEvents.clear()
      expectNode(l1.ref, div.of("L1", sentinel, sentinel))
      expectNode(l3.ref, div.of("L3", sentinel, div of "e", sentinel))
    }
  }

  it("depth-2: a group stolen from an UNMOUNTED host into a mounted list does not let a stale NESTED group steal from an unrelated list") {
    val tracker = createEventTracker()
    val e = tracker.createDiv("e")
    tracker.clear()
    val claimE = Var[Option[HtmlElement]](None)
    val inner: Inserter = child.maybe <-- claimE.signal // claims `e` while active
    val outerItems = Var[List[Inserter]](List(inner))
    val outer: Inserter = children <-- outerItems.signal
    val showL1 = Var(true)
    val items1 = Var[List[Inserter]](List(outer))
    val items2 = Var[List[Inserter]](Nil)
    val items3 = Var[List[HtmlElement]](Nil)
    val l1 = div("L1", children <-- items1.signal)
    val l2 = div("L2", children <-- items2.signal)
    val l3 = div("L3", children <-- items3.signal)
    val root = div(child.maybe <-- showL1.signal.map(if (_) Some(l1) else None), l2, l3)

    withClue("mount, hide L1, make `inner` stale while inactive, and put `e` in the unrelated live L3:") {
      mount(root)
      showL1.set(false)
      Var.set(
        outerItems -> Nil,
        claimE -> Some(e)
      )
      tracker.assertNoEvents.clear()
      items3.set(List(e))
      tracker.assertEvents(_.mounted("e")).clear()
    }

    withClue("mounted L2 steals the outer group out of hidden L1: stale `inner` must not activate and steal `e`:") {
      items2.set(List(outer))
      tracker.assertNoEvents.clear()
      expectNode(l2.ref, div.of("L2", sentinel, sentinel, sentinel, sentinel))
      expectNode(l3.ref, div.of("L3", sentinel, div of "e", sentinel))
    }
  }
}
