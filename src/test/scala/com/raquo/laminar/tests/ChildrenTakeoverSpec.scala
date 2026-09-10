package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.{DynamicInserter, Inserter}
import com.raquo.laminar.utils.UnitSpec

import scala.collection.mutable

/** When another inserter takes over an `onMountInsert` context previously managed by
  * `children <--`, the takeover clears the old list through
  * `InsertContext.clearPreviousInserterContent` -> `removeContentMapNodesFromDom`, which
  * calls `Inserter.removeFromDynamicList` on every entry still in the context's `contentMap`.
  *
  * If those list items are themselves DYNAMIC inserters (a nested `child <--` /
  * `children <--`), that call routes to `DynamicInserter.removeFromDynamicList`, which tears
  * down the item's `NestedGroup` (its owner + sentinels), recursively. This is the one path
  * where `removeContentMapNodesFromDom` does more than a plain `removeChild`, and it relies
  * on the invariant that any dynamic inserter in a `contentMap` was placed there by
  * `ChildrenInserter.updateChildren`, which always calls `addToDynamicList` first.
  *
  * These tests exercise that path (asserting the nested content actually UNMOUNTS via
  * mount/unmount callbacks, not merely detaches), and the last one pins the invariant
  * directly: calling `removeFromDynamicList` without a prior `addToDynamicList` now fails
  * loudly rather than silently leaking the subtree.
  */
class ChildrenTakeoverSpec extends UnitSpec {

  // -- `onMountInsert` context: takeover happens across an unmount / remount. The nested
  //    group lives on the parent element's lifecycle, so it re-mounts on remount and is then
  //    torn down by the takeover, leaving it net-unmounted (mounts == unmounts, mounts >= 2). --

  it("onMountInsert: `children <--` (with a nested `child <--` item) -> `child <--` unmounts the list's content") {
    val mounts = mutable.Map[String, Int]().withDefaultValue(0)
    val unmounts = mutable.Map[String, Int]().withDefaultValue(0)
    def trackedDiv(id: String): Div = div(
      id,
      onMountCallback(_ => mounts(id) += 1),
      onUnmountCallback(_ => unmounts(id) += 1)
    )

    val innerBus = new EventBus[String]
    val takeoverBus = new EventBus[String]

    // The list's single item is itself a dynamic inserter, so it lands in the outer
    // context's contentMap as a DynamicInserter (with a NestedGroup), not a plain node.
    val nestedChildItem: Inserter = child <-- innerBus.events.map(trackedDiv)
    var dynamicInserter: Inserter = children <-- Var[List[Inserter]](List(nestedChildItem)).signal
    val takeoverInserter: Inserter = child <-- takeoverBus.events.map(trackedDiv)

    val el = div("Hello ", onMountInsert(_ => dynamicInserter), " world")
    mount(el)
    innerBus.writer.onNext("n1")

    // outer children<-- leading sentinel, the nested item's leading + trailing sentinels
    // bracketing n1, then the outer children<-- trailing sentinel.
    expectNode(div of ("Hello ", sentinel, sentinel, div of "n1", sentinel, sentinel, " world"))
    assert(mounts("n1") == 1 && unmounts("n1") == 0)

    unmount()
    dynamicInserter = takeoverInserter
    mount(el)
    // The takeover teardown runs when `child <--` first emits (nothing to clear before then).
    takeoverBus.writer.onNext("k1")

    // The whole nested list (item's sentinels + n1) is gone; only the takeover child remains.
    expectNode(div of ("Hello ", sentinel, div of "k1", " world"))
    assert(mounts("k1") == 1)
    // n1 rode the element's unmount / remount, then the takeover unmounted it: net-unmounted.
    assert(mounts("n1") == unmounts("n1"))
    assert(mounts("n1") >= 2)
  }

  it("onMountInsert: `children <--` (with a nested `children <--` item) -> `text <--` unmounts the list's content recursively") {
    val mounts = mutable.Map[String, Int]().withDefaultValue(0)
    val unmounts = mutable.Map[String, Int]().withDefaultValue(0)
    def trackedDiv(id: String): Div = div(
      id,
      onMountCallback(_ => mounts(id) += 1),
      onUnmountCallback(_ => unmounts(id) += 1)
    )

    val innerBus = new EventBus[List[Node]]
    val textBus = new EventBus[String]

    // Nested `children <--`: taking this down recurses (outer removeFromDynamicList ->
    // NestedGroup.removeFromParent -> nested removeContentMapNodesFromDom over g1 / g2).
    val nestedChildrenItem: Inserter = children <-- innerBus.events
    var dynamicInserter: Inserter = children <-- Var[List[Inserter]](List(nestedChildrenItem)).signal
    val takeoverInserter: Inserter = text <-- textBus.events

    val el = div("Hello ", onMountInsert(_ => dynamicInserter), " world")
    mount(el)
    innerBus.writer.onNext(List(trackedDiv("g1"), trackedDiv("g2")))

    expectNode(div of ("Hello ", sentinel, sentinel, div of "g1", div of "g2", sentinel, sentinel, " world"))
    assert(mounts("g1") == 1 && mounts("g2") == 1)

    unmount()
    dynamicInserter = takeoverInserter
    mount(el)
    textBus.writer.onNext("hi")

    expectNode(div of ("Hello ", sentinel, "hi", " world"))
    assert(mounts("g1") == unmounts("g1") && mounts("g1") >= 2)
    assert(mounts("g2") == unmounts("g2") && mounts("g2") >= 2)
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
}
