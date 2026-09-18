package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.{DynamicInserter, InsertContext, Inserter, InserterType}
import com.raquo.laminar.nodes.CommentNode
import com.raquo.laminar.utils.UnitSpec

import scala.scalajs.js

/** Pins the invariant behind the `contentMap.size > 1` guard in
  * `InsertContext.setNextInserterType`:
  *
  *   Whenever `trailingSentinelNode` is empty, `contentMap` holds at most one node.
  *
  * The guard throws if we ever switch TO a trailing-sentinel inserter type (`children <--`
  * / `children.command <--`) while the context has no trailing sentinel yet AND already
  * holds more than one content node – because we wouldn't know where that pre-existing
  * multi-node content ends, so we couldn't place the trailing sentinel correctly.
  *
  * The invariant holds structurally for every public code path:
  *  - Only the children-like inserters ever grow `contentMap` beyond one entry, and they
  *    always maintain a trailing sentinel while doing so.
  *  - The trailing sentinel is only ever removed inside `clearPreviousInserterContent`
  *    (when switching to `child <--` / `text <--`), which clears `contentMap` to <= 1
  *    entry BEFORE removing the sentinel.
  *  - `switchToChildren` is the only `setNextInserterType(<trailing type>)` call that does
  *    NOT clear first – but by then, if `contentMap` has > 1 entry, a trailing sentinel is
  *    already present (from the previous children-like inserter), so the guard is not hit.
  *
  * So the guard is unreachable through the public API; the first test below demonstrates
  * the most plausible near-miss (a static `Seq[Node]` inserter, then `children <--`, in a
  * shared `onMountInsert` context) staying safe, and the second forces the forbidden state
  * white-box to prove the guard actually fires.
  *
  * Two further, unrelated invariants are pinned at the end: `DynamicInserter.removeFromDynamicList`
  * and `DynamicInserter.lastNode` both require a prior `addToDynamicList`, failing loudly
  * otherwise. Every real call site reads them only on a group that is currently placed (just
  * added, or looked up via a live DOM node whose presence implies its sentinels, hence its
  * group, exist), so the guards are unreachable through the public API.
  */
class InserterInvariantSpec extends UnitSpec {

  // -- The scenario one might expect to break the invariant: a `Seq[Node]` static inserter
  //    (→ SlottableChildrenInserter) leaves TWO content nodes in the shared onMountInsert
  //    context, then `children <--` takes over. It stays safe because SlottableChildrenInserter
  //    renders through `ChildrenInserter.switchToChildren`, which establishes the trailing
  //    sentinel – so by the time `children <--` runs, the sentinel is already there.
  //    (If SlottableChildrenInserter ever stopped bracketing its content with a trailing
  //    sentinel, the `bus.emit` step below – diffing against the 2-node contentMap with no
  //    trailing sentinel – would trip the guard. This test is that regression tripwire.) --

  it("static Seq[Node] then `children <--` in a shared onMountInsert context does not trip the guard") {
    val useChildren = Var(false)
    val bus = new EventBus[List[HtmlElement]]

    val a = span("a")
    val b = span("b")

    val seqInserter: Inserter = List[Node](a, b) // -> SlottableChildrenInserter (2 content nodes)
    val childrenInserter: Inserter = children <-- bus.events

    val el = div(
      "start ",
      onMountInsert { _ =>
        if (useChildren.now()) childrenInserter else seqInserter
      },
      " end"
    )

    // First mount: the static 2-node Seq. SlottableChildrenInserter brackets it with a
    // trailing sentinel via switchToChildren.
    mount(el)
    expectNode(div of ("start ", sentinel, span of "a", span of "b", sentinel, " end"))

    // Remount with `children <--`. The Seq's content (and its trailing sentinel) is preserved
    // across unmount/remount; `children <--` hasn't emitted yet, so nothing changes.
    unmount()
    useChildren.set(true)
    mount(el)
    expectNode(el.ref, div of ("start ", sentinel, span of "a", span of "b", sentinel, " end"))

    // `children <--` emits: switchToChildren finds the trailing sentinel already present
    // (needsTrailingSentinel + trailing nonEmpty => guard not entered) and diffs in place.
    bus.emit(List(span("x"), span("y")))
    expectNode(div of ("start ", sentinel, span of "x", span of "y", sentinel, " end"))
  }

  // -- White-box: there is no public path into the forbidden state, so we build it by hand –
  //    a context with two content nodes and no trailing sentinel – and confirm the guard
  //    throws when a trailing-sentinel inserter type takes over. --

  it("`setNextInserterType` fails loudly on >1 content nodes without a trailing sentinel") {
    val el = div()
    mount(el)

    val sentinelNode = new CommentNode("")
    sentinelNode(el) // append to el
    val ctx = new InsertContext(
      sentinelNode = sentinelNode,
      initialParentNode = el,
      initialSlotName = ()
    )

    val a = span("a")
    val b = span("b")
    // Forbidden state: two content entries, and a fresh context has no trailing sentinel.
    ctx.contentMap.set(a.ref, a)
    ctx.contentMap.set(b.ref, b)

    val nextType: js.UndefOr[InserterType] = InserterType.ChildrenType
    val thrown = intercept[Exception] {
      ctx.setNextInserterType(nextType)
    }
    assert(thrown.getMessage.contains("content nodes without trailing sentinel"))
  }

  // -- Invariant guard: `removeFromDynamicList` is documented as requiring a prior
  //    `addToDynamicList`. Every real call site upholds this (a DynamicInserter only reaches
  //    it as a `children <--` item, added via `addToDynamicList` before it can enter a
  //    contentMap). If that ever broke, silently no-op-ing would leak the whole subtree AND
  //    its owner, so the method fails loudly instead. This white-box test pins that. --

  it("`DynamicInserter.removeFromDynamicList` without a prior `addToDynamicList` fails loudly") {
    val bus = new EventBus[String]
    val inserter = (child <-- bus.events.map(s => span(s))).asInstanceOf[DynamicInserter]
    val parent = div()

    // No `addToDynamicList` was ever called on `inserter`, so it has no NestedGroup.
    val thrown = intercept[Exception] {
      inserter.removeFromDynamicList(parent)
    }
    assert(thrown.getMessage.contains("nested group not found"))
  }

  // -- Invariant guard: `lastNode` likewise requires a prior `addToDynamicList`. Every real
  //    call site reads it only on a placed group (just added, or found via a live DOM node),
  //    so it never sees a group-less inserter. If that broke, we'd read a span end off an
  //    inserter that has none, so the method fails loudly instead. White-box tripwire. --

  it("`DynamicInserter.lastNode` without a prior `addToDynamicList` fails loudly") {
    val bus = new EventBus[String]
    val inserter = (child <-- bus.events.map(s => span(s))).asInstanceOf[DynamicInserter]

    // No `addToDynamicList` was ever called on `inserter`, so it has no NestedGroup.
    val thrown = intercept[Exception] {
      inserter.lastNode
    }
    assert(thrown.getMessage.contains("nested group not found"))
  }

}
