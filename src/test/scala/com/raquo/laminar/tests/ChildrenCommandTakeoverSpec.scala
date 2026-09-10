package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.{CollectionCommand, Inserter}
import com.raquo.laminar.utils.UnitSpec

import scala.collection.mutable

/** Switching an inserter slot between `children.command <--` and other inserter types must
  * REMOVE and UNMOUNT the command's content (issue #157 follow-up).
  *
  * `children.command` tracks its content nodes in `InsertContext.contentMap` (keyed by ref,
  * like a plain `child <--` node) and brackets them with a `trailingSentinelNode`. A
  * taking-over inserter of a different type finds and unmounts the content via the same
  * map-based teardown (`clearPreviousInserterContent` -> `removeContentMapNodesFromDom`) it
  * uses for any content, and `setNextInserterType` then drops or reuses the trailing sentinel
  * depending on whether the new inserter type needs one. Historically the command's nodes
  * were untracked and thus invisible to that teardown, so they leaked in the DOM.
  *
  * These tests assert the DOM result AND – wherever the nodes carry mount/unmount callbacks –
  * that the removed nodes are actually unmounted by Laminar, not merely detached from the DOM.
  */
class ChildrenCommandTakeoverSpec extends UnitSpec {

  // -- `children <--` context: the parent stays mounted throughout, so unmount callbacks
  //    fire exactly when content is torn down – an unambiguous check. --

  it("children <--: removing a `children.command` item unmounts its whole span") {
    val lifecycle = mutable.Buffer[String]()
    def trackedDiv(id: String): Div = div(
      id,
      onMountCallback(_ => lifecycle += s"mount:$id"),
      onUnmountCallback(_ => lifecycle += s"unmount:$id")
    )

    val cmdBus = new EventBus[CollectionCommand[Node]]
    val commandItem: Inserter = children.command <-- cmdBus.events
    val itemsVar = Var[List[Inserter]](List(commandItem))

    mount(div("Hello", children <-- itemsVar.signal))

    // outer children<-- leading sentinel, then the command item's leading + trailing sentinels,
    // then the outer children<-- trailing sentinel.
    expectNode(div.of("Hello", sentinel, sentinel, sentinel, sentinel))

    cmdBus.writer.onNext(CollectionCommand.Append(trackedDiv("c1")))
    cmdBus.writer.onNext(CollectionCommand.Append(trackedDiv("c2")))

    assert(lifecycle.toList == List("mount:c1", "mount:c2"))
    expectNode(div.of("Hello", sentinel, sentinel, div of "c1", div of "c2", sentinel, sentinel))

    lifecycle.clear()

    // Remove the command item: its content AND all its sentinels are gone, c1/c2 unmount.
    itemsVar.set(Nil)

    assert(lifecycle.toList == List("unmount:c1", "unmount:c2"))
    expectNode(div.of("Hello", sentinel, sentinel))
  }

  it("children <--: switching a slot from `children.command` to `child <--` unmounts the command content") {
    val lifecycle = mutable.Buffer[String]()
    def trackedDiv(id: String): Div = div(
      id,
      onMountCallback(_ => lifecycle += s"mount:$id"),
      onUnmountCallback(_ => lifecycle += s"unmount:$id")
    )

    val cmdBus = new EventBus[CollectionCommand[Node]]
    val childBus = new EventBus[String]
    val commandItem: Inserter = children.command <-- cmdBus.events
    val childItem: Inserter = child <-- childBus.events.map(trackedDiv)

    val itemsVar = Var[List[Inserter]](List(commandItem))
    mount(div("Hello", children <-- itemsVar.signal))

    cmdBus.writer.onNext(CollectionCommand.Append(trackedDiv("c1")))
    cmdBus.writer.onNext(CollectionCommand.Append(trackedDiv("c2")))
    assert(lifecycle.toList == List("mount:c1", "mount:c2"))
    lifecycle.clear()

    // Replace the command item with a `child <--` item, then let the child emit.
    itemsVar.set(List(childItem))
    childBus.writer.onNext("k1")

    assert(lifecycle.toSet == Set("unmount:c1", "unmount:c2", "mount:k1"))
    expectNode(div.of("Hello", sentinel, sentinel, div of "k1", sentinel, sentinel))
  }

  // -- `onMountInsert` context: takeover happens across an unmount / remount. The old
  //    command nodes remount on the remount, then the takeover unmounts them again, so a
  //    correct teardown leaves them net-unmounted (mounts == unmounts, with mounts >= 2). --

  it("onMountInsert: `children.command` -> `child <--` unmounts the command's nodes") {
    val mounts = mutable.Map[String, Int]().withDefaultValue(0)
    val unmounts = mutable.Map[String, Int]().withDefaultValue(0)
    def trackedDiv(id: String): Div = div(
      id,
      onMountCallback(_ => mounts(id) += 1),
      onUnmountCallback(_ => unmounts(id) += 1)
    )

    val cmdBus = new EventBus[CollectionCommand[Node]]
    val xChildIx = new EventBus[Int]

    var dynamicInserter: Inserter = children.command <-- cmdBus.events
    val xChildInserter: Inserter = child <-- xChildIx.events.map(n => span("x" + n))

    val el = div("Hello ", onMountInsert(_ => dynamicInserter), " world")
    mount(el)

    cmdBus.writer.onNext(CollectionCommand.Append(trackedDiv("c1")))
    cmdBus.writer.onNext(CollectionCommand.Append(trackedDiv("c2")))
    expectNode(div of ("Hello ", sentinel, div of "c1", div of "c2", sentinel, " world"))

    unmount()
    dynamicInserter = xChildInserter
    mount(el)
    xChildIx.emit(1)

    expectNode(div of ("Hello ", sentinel, span of "x1", " world"))
    assert(mounts("c1") == unmounts("c1"))
    assert(mounts("c2") == unmounts("c2"))
    assert(mounts("c1") >= 2) // remount happened, so the final unmount is the takeover's
    assert(mounts("c2") >= 2)
  }

  it("onMountInsert: `children.command` -> `children <--` unmounts the command's nodes") {
    val mounts = mutable.Map[String, Int]().withDefaultValue(0)
    val unmounts = mutable.Map[String, Int]().withDefaultValue(0)
    def trackedDiv(id: String): Div = div(
      id,
      onMountCallback(_ => mounts(id) += 1),
      onUnmountCallback(_ => unmounts(id) += 1)
    )

    val cmdBus = new EventBus[CollectionCommand[Node]]
    val childrenBus = new EventBus[List[Node]]

    var dynamicInserter: Inserter = children.command <-- cmdBus.events
    val childrenInserter: Inserter = children <-- childrenBus.events

    val el = div("Hello ", onMountInsert(_ => dynamicInserter), " world")
    mount(el)

    cmdBus.writer.onNext(CollectionCommand.Append(trackedDiv("c1")))
    cmdBus.writer.onNext(CollectionCommand.Append(trackedDiv("c2")))
    expectNode(div of ("Hello ", sentinel, div of "c1", div of "c2", sentinel, " world"))

    unmount()
    dynamicInserter = childrenInserter
    mount(el)
    childrenBus.writer.onNext(List(span("a"), span("b")))

    expectNode(div of ("Hello ", sentinel, span of "a", span of "b", sentinel, " world"))
    assert(mounts("c1") == unmounts("c1"))
    assert(mounts("c2") == unmounts("c2"))
    assert(mounts("c1") >= 2)
  }

  it("onMountInsert: `children.command` -> `text <--` unmounts the command's nodes") {
    val mounts = mutable.Map[String, Int]().withDefaultValue(0)
    val unmounts = mutable.Map[String, Int]().withDefaultValue(0)
    def trackedDiv(id: String): Div = div(
      id,
      onMountCallback(_ => mounts(id) += 1),
      onUnmountCallback(_ => unmounts(id) += 1)
    )

    val cmdBus = new EventBus[CollectionCommand[Node]]
    val textBus = new EventBus[String]

    var dynamicInserter: Inserter = children.command <-- cmdBus.events
    val textInserter: Inserter = text <-- textBus.events

    val el = div("Hello ", onMountInsert(_ => dynamicInserter), " world")
    mount(el)

    cmdBus.writer.onNext(CollectionCommand.Append(trackedDiv("c1")))
    expectNode(div of ("Hello ", sentinel, div of "c1", sentinel, " world"))

    unmount()
    dynamicInserter = textInserter
    mount(el)
    textBus.writer.onNext("hi")

    expectNode(div of ("Hello ", sentinel, "hi", " world"))
    assert(mounts("c1") == unmounts("c1"))
    assert(mounts("c1") >= 2)
  }

  // -- The reverse direction: `children.command` taking over another inserter's content.
  //    (That content IS in extraNodesMap, so it was already handled – these guard it.) --

  it("onMountInsert: `child <--` -> `children.command` removes and unmounts the previous child") {
    val mounts = mutable.Map[String, Int]().withDefaultValue(0)
    val unmounts = mutable.Map[String, Int]().withDefaultValue(0)
    def trackedDiv(id: String): Div = div(
      id,
      onMountCallback(_ => mounts(id) += 1),
      onUnmountCallback(_ => unmounts(id) += 1)
    )

    val childBus = new EventBus[String]
    val cmdBus = new EventBus[CollectionCommand[Node]]

    var dynamicInserter: Inserter = child <-- childBus.events.map(trackedDiv)
    val commandInserter: Inserter = children.command <-- cmdBus.events

    val el = div("Hello ", onMountInsert(_ => dynamicInserter), " world")
    mount(el)
    childBus.writer.onNext("p1")
    expectNode(div of ("Hello ", sentinel, div of "p1", " world"))

    unmount()
    dynamicInserter = commandInserter
    mount(el)
    cmdBus.writer.onNext(CollectionCommand.Append(trackedDiv("c1")))

    expectNode(div of ("Hello ", sentinel, div of "c1", sentinel, " world"))
    // The previous child must be gone from the DOM and net-unmounted.
    assert(mounts("p1") == unmounts("p1"))
  }

  it("onMountInsert: `children <--` -> `children.command` removes and unmounts the previous children") {
    // Distinct from the `child <--` case above: `children <--` is a MULTI-node span that
    // already owns a trailing sentinel. So the takeover exercises `clearPreviousInserterContent`
    // tearing down several tracked nodes at once, and `setNextInserterType` REUSING the existing
    // trailing sentinel (both inserter types need one) rather than inserting a fresh one.
    val mounts = mutable.Map[String, Int]().withDefaultValue(0)
    val unmounts = mutable.Map[String, Int]().withDefaultValue(0)
    def trackedDiv(id: String): Div = div(
      id,
      onMountCallback(_ => mounts(id) += 1),
      onUnmountCallback(_ => unmounts(id) += 1)
    )

    val childrenBus = new EventBus[List[Node]]
    val cmdBus = new EventBus[CollectionCommand[Node]]

    var dynamicInserter: Inserter = children <-- childrenBus.events
    val commandInserter: Inserter = children.command <-- cmdBus.events

    val el = div("Hello ", onMountInsert(_ => dynamicInserter), " world")
    mount(el)
    childrenBus.writer.onNext(List(trackedDiv("p1"), trackedDiv("p2")))
    expectNode(div of ("Hello ", sentinel, div of "p1", div of "p2", sentinel, " world"))

    unmount()
    dynamicInserter = commandInserter
    mount(el)
    cmdBus.writer.onNext(CollectionCommand.Append(trackedDiv("c1")))

    // Both previous children are gone; the (reused) trailing sentinel still brackets the span,
    // and the command's Append lands right before it.
    expectNode(div of ("Hello ", sentinel, div of "c1", sentinel, " world"))
    assert(mounts("p1") == unmounts("p1"))
    assert(mounts("p2") == unmounts("p2"))
  }

  it("onMountInsert: `text <--` -> `children.command` removes the previous text node") {
    // Mirrors the `child <--` case (both are `ChildType` – a single node, no trailing sentinel),
    // completing the reverse matrix against the forward `command -> {child, children, text}` tests.
    // A `text <--` node has no mount/unmount callbacks, so we assert the DOM result only.
    val cmdBus = new EventBus[CollectionCommand[Node]]
    val textBus = new EventBus[String]

    var dynamicInserter: Inserter = text <-- textBus.events
    val commandInserter: Inserter = children.command <-- cmdBus.events

    val el = div("Hello ", onMountInsert(_ => dynamicInserter), " world")
    mount(el)
    textBus.writer.onNext("hi")
    expectNode(div of ("Hello ", sentinel, "hi", " world"))

    unmount()
    dynamicInserter = commandInserter
    mount(el)
    cmdBus.writer.onNext(CollectionCommand.Append(div("c1")))

    // The text node is cleared, a trailing sentinel is added for the command span, and the
    // command's Append lands before it.
    expectNode(div of ("Hello ", sentinel, div of "c1", sentinel, " world"))
  }

  // -- `children.command` -> `children.command`: a DIFFERENT command inserter taking over
  //    the same `onMountInsert` context. Unlike the reconciling inserters (child/children/
  //    text, which clear or diff on every emission), `children.command` sets up its span
  //    ONCE, and its stream replays no past commands, so a taking-over command inserter
  //    can't meaningfully rebuild the span from scratch. We therefore keep the existing
  //    span (a trailing sentinel is present) and its content, and let the new inserter's
  //    commands build on top of it. --

  it("onMountInsert: `children.command` -> `children.command` (different inserter) keeps the previous command's content") {
    val mounts = mutable.Map[String, Int]().withDefaultValue(0)
    val unmounts = mutable.Map[String, Int]().withDefaultValue(0)
    def trackedDiv(id: String): Div = div(
      id,
      onMountCallback(_ => mounts(id) += 1),
      onUnmountCallback(_ => unmounts(id) += 1)
    )

    val cmdBusA = new EventBus[CollectionCommand[Node]]
    val cmdBusB = new EventBus[CollectionCommand[Node]]

    var dynamicInserter: Inserter = children.command <-- cmdBusA.events
    val commandB: Inserter = children.command <-- cmdBusB.events

    val el = div("Hello ", onMountInsert(_ => dynamicInserter), " world")
    mount(el)

    cmdBusA.writer.onNext(CollectionCommand.Append(trackedDiv("a1")))
    cmdBusA.writer.onNext(CollectionCommand.Append(trackedDiv("a2")))
    expectNode(div of ("Hello ", sentinel, div of "a1", div of "a2", sentinel, " world"))

    unmount()
    dynamicInserter = commandB
    mount(el)
    // Command B takes over at mount, but the span (trailing sentinel) already exists, so B
    // reuses it and A's content as-is; B's Append lands at the end of that preserved span.
    cmdBusB.writer.onNext(CollectionCommand.Append(trackedDiv("b1")))

    expectNode(div of ("Hello ", sentinel, div of "a1", div of "a2", div of "b1", sentinel, " world"))
    // A's nodes are preserved and still mounted: they unmounted once with the element and
    // re-mounted with it (they were never removed from the DOM), so they are net-mounted.
    assert(mounts("a1") == 2 && unmounts("a1") == 1)
    assert(mounts("a2") == 2 && unmounts("a2") == 1)
  }

  it("onMountInsert: `children.command` remount with the SAME inserter preserves its content") {
    // The guard that detects a DIFFERENT command inserter taking over must NOT trip for the
    // same inserter re-mounting: `onMountInsert` preserves the DOM across unmount / remount.
    val cmdBus = new EventBus[CollectionCommand[Node]]
    val commandInserter: Inserter = children.command <-- cmdBus.events

    val el = div("Hello ", onMountInsert(_ => commandInserter), " world")
    mount(el)

    cmdBus.writer.onNext(CollectionCommand.Append(div("c1")))
    cmdBus.writer.onNext(CollectionCommand.Append(div("c2")))
    expectNode(div of ("Hello ", sentinel, div of "c1", div of "c2", sentinel, " world"))

    unmount()
    mount(el)

    // Same inserter, same span: content is preserved (not cleared, not duplicated).
    expectNode(div of ("Hello ", sentinel, div of "c1", div of "c2", sentinel, " world"))

    // And it keeps working: a further Append lands at the end of the preserved span.
    cmdBus.writer.onNext(CollectionCommand.Append(div("c3")))
    expectNode(div of ("Hello ", sentinel, div of "c1", div of "c2", div of "c3", sentinel, " world"))
  }
}
