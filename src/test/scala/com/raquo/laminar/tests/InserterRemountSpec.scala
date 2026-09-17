package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ChildNode
import com.raquo.laminar.utils.UnitSpec

/** Regression + characterization suite for how an inserter reconciles its content against the
  * PERSISTENT [[com.raquo.laminar.inserters.InsertContext]] across an unmount / remount.
  *
  * The context (and its DOM content) survives unmounting, but an inserter's `insertFn` – and
  * thus any state it keeps in a closure – runs afresh on every mount. So an inserter must decide
  * what to clear / keep from the context, not from its closure. `text.maybe <--`'s "no value"
  * branch used to consult only its closure, so a value-then-None transition straddling a remount
  * (or content left in a shared `onMountInsert` context) leaked stale nodes. These tests pin that
  * fix and guard the same guarantee for the neighbouring inserter types.
  */
class InserterRemountSpec extends UnitSpec {

  describe("text.maybe reconciles its content across unmount/remount") {

    it("clears optional text changed to None while unmounted") {
      val valueVar = Var(Option("old"))
      val host = div("before", text.maybe <-- valueVar.signal, "after")
      mount(host)
      expectNode(div.of("before", sentinel, "old", "after"))

      // -- The value drops to None while unmounted; the remount must not resurrect "old". --
      unmount()
      valueVar.set(None)
      mount(host)
      expectNode(div.of("before", sentinel, "after"))

      // -- And the inserter still works normally afterwards. --
      valueVar.set(Some("new"))
      expectNode(div.of("before", sentinel, "new", "after"))
    }

    it("clears the last-rendered value when it changes to None while unmounted") {
      val valueVar = Var(Option("a"))
      val host = div("before", text.maybe <-- valueVar.signal, "after")
      mount(host)
      valueVar.set(Some("b"))
      expectNode(div.of("before", sentinel, "b", "after"))

      // -- Two values were rendered before unmounting; "b" must not survive the None remount. --
      unmount()
      valueVar.set(None)
      mount(host)
      expectNode(div.of("before", sentinel, "after"))
    }

    it("renders a value that appeared while unmounted (started as None)") {
      val valueVar = Var(Option.empty[String])
      val host = div("before", text.maybe <-- valueVar.signal, "after")
      mount(host)
      expectNode(div.of("before", sentinel, "after"))

      // -- A value appears while unmounted; the remount must render it. --
      unmount()
      valueVar.set(Some("late"))
      mount(host)
      expectNode(div.of("before", sentinel, "late", "after"))
    }

    it("swaps to a different value that appeared while unmounted") {
      val valueVar = Var(Option("old"))
      val host = div("before", text.maybe <-- valueVar.signal, "after")
      mount(host)
      expectNode(div.of("before", sentinel, "old", "after"))

      // -- A different value replaces the old one across the remount (no stale "old" left). --
      unmount()
      valueVar.set(Some("new"))
      mount(host)
      expectNode(div.of("before", sentinel, "new", "after"))
    }

    it("stays empty across a remount when the value is None throughout") {
      val valueVar = Var(Option.empty[String])
      val host = div("before", text.maybe <-- valueVar.signal, "after")
      mount(host)
      expectNode(div.of("before", sentinel, "after"))

      unmount()
      mount(host)
      expectNode(div.of("before", sentinel, "after"))
    }

    it("clears only its own content, leaving a sibling text.maybe untouched") {
      val v1 = Var(Option("a"))
      val v2 = Var(Option("b"))
      val host = div(
        "x",
        text.maybe <-- v1.signal,
        "|",
        text.maybe <-- v2.signal,
        "y"
      )
      mount(host)
      expectNode(div.of("x", sentinel, "a", "|", sentinel, "b", "y"))

      // -- Only the first inserter's value drops; the second inserter's "b" must remain. --
      unmount()
      v1.set(None)
      mount(host)
      expectNode(div.of("x", sentinel, "|", sentinel, "b", "y"))

      // -- Now drop the second one too, across another remount. --
      unmount()
      v2.set(None)
      mount(host)
      expectNode(div.of("x", sentinel, "|", sentinel, "y"))
    }
  }

  describe("text.maybe inside onMountInsert reconciles foreign content across remount") {

    it("clears previous onMountInsert content when optional text starts with None") {
      val tracker = createEventTracker()
      var useText = false
      val host = div(
        "before",
        onMountInsert { _ =>
          if (useText) {
            text.maybe <-- Val(Option.empty[String])
          } else {
            tracker.createSpan("old")
          }
        },
        "after"
      )
      mount(host)
      expectNode(div.of("before", sentinel, span.of("old"), "after"))
      tracker.assertEvents(
        _.elementCreated("old"),
        _.mounted("old")
      ).clear()

      // -- On remount the shared context holds the old span; text.maybe(None) must clear it. --
      unmount()
      useText = true
      mount(host)
      expectNode(div.of("before", sentinel, "after"))
      tracker.assertEvents(
        _.unmounted("old")
      ).clear()
    }

    it("replaces previous onMountInsert content when optional text starts with a value") {
      val tracker = createEventTracker()
      var useText = false
      val host = div(
        "before",
        onMountInsert { _ =>
          if (useText) {
            text.maybe <-- Val(Option("txt"))
          } else {
            tracker.createSpan("old")
          }
        },
        "after"
      )
      mount(host)
      expectNode(div.of("before", sentinel, span.of("old"), "after"))
      tracker.clear()

      // -- The remount's optional text has a value, which must replace the stale span. --
      unmount()
      useText = true
      mount(host)
      expectNode(div.of("before", sentinel, "txt", "after"))
      tracker.assertEvents(
        _.unmounted("old")
      ).clear()
    }

    it("clears previous optional text when the next mount's optional text is None") {
      var value: Option[String] = Some("first")
      val host = div(
        "before",
        onMountInsert { _ =>
          text.maybe <-- Val(value)
        },
        "after"
      )
      mount(host)
      expectNode(div.of("before", sentinel, "first", "after"))

      // -- A fresh text.maybe(None) inserter shares the context; it must clear the old text. --
      unmount()
      value = None
      mount(host)
      expectNode(div.of("before", sentinel, "after"))
    }
  }

  describe("neighbouring inserters reconcile against the persistent context across remount") {

    it("children <-- clears to an empty list that emptied while unmounted") {
      val tracker = createEventTracker()
      // Pre-create the items and feed them through a strict Var signal (no `.map`), so the empty
      // list deterministically re-emits on remount – a lazy `.map` layer would memoize instead.
      val itemA = tracker.createSpan("a")
      val itemB = tracker.createSpan("b")
      val itemsVar = Var[List[HtmlElement]](List(itemA, itemB))
      val host = div("before", children <-- itemsVar.signal, "after")
      tracker.clear()

      mount(host)
      expectNode(div.of("before", sentinel, span.of("a"), span.of("b"), sentinel, "after"))
      tracker.assertEvents(
        _.mounted("a"),
        _.mounted("b")
      ).clear()

      // -- The list empties while unmounted; the remount must clear the old items from the DOM. --
      unmount()
      itemsVar.set(Nil)
      mount(host)
      expectNode(div.of("before", sentinel, sentinel, "after"))
      // The items unmount as the host unmounts; the empty remount then removes them from the DOM.
      tracker.assertEvents(
        _.unmounted("a"),
        _.unmounted("b")
      ).clear()
    }

    it("children <-- signal.map re-runs on remount and clears the emptied list") {
      // Same guarantee as the strict-signal test above, but through a lazy `.map` layer that
      // also CREATES the items. The mapped signal re-runs with the current (empty) value on
      // remount – it does not memoize the stale list – so the old items are cleared.
      val tracker = createEventTracker()
      val itemsVar = Var(List("a", "b"))
      val host = div(
        "before",
        children <-- itemsVar.signal.map(_.map(id => tracker.createSpan(id))),
        "after"
      )
      mount(host)
      expectNode(div.of("before", sentinel, span.of("a"), span.of("b"), sentinel, "after"))
      tracker.assertEvents(
        _.elementCreated("a"),
        _.elementCreated("b"),
        _.mounted("a"),
        _.mounted("b")
      ).clear()

      // -- The list empties while unmounted; the mapped signal re-runs on remount and clears. --
      unmount()
      itemsVar.set(Nil)
      mount(host)
      expectNode(div.of("before", sentinel, sentinel, "after"))
      tracker.assertEvents(
        _.unmounted("a"),
        _.unmounted("b")
      ).clear()
    }

    it("child <-- swaps to a new value that appeared while unmounted") {
      val tracker = createEventTracker()
      val nameVar = Var("old")
      val host = div(
        "before",
        child <-- nameVar.signal.map(id => tracker.createSpan(id)),
        "after"
      )
      mount(host)
      expectNode(div.of("before", sentinel, span.of("old"), "after"))
      tracker.clear()

      // -- A new value emitted while unmounted; the remount renders it and drops the old node. --
      unmount()
      nameVar.set("new")
      mount(host)
      expectNode(div.of("before", sentinel, span.of("new"), "after"))
      tracker.assertEvents(
        _.unmounted("old"),
        _.elementCreated("new"),
        _.mounted("new")
      ).clear()
    }
  }

  describe("child.maybe reconciles its content across unmount/remount") {

    // child.maybe delegates to `child <--` by mapping None to a placeholder comment node
    // (ChildOptionReceiver), so it always renders SOMETHING and reconciles on every emission –
    // it has no "render nothing" branch that could rely on stale closure state like text.maybe
    // did. These tests pin that guarantee directly.

    it("clears a child changed to None while unmounted (renders the placeholder)") {
      val tracker = createEventTracker()
      val oldSpan = tracker.createSpan("old")
      val newSpan = tracker.createSpan("new")
      val valueVar = Var[Option[ChildNode.Base]](Some(oldSpan))
      val host = div("before", child.maybe <-- valueVar, "after")
      tracker.clear()

      mount(host)
      expectNode(div.of("before", sentinel, span.of("old"), "after"))
      tracker.assertEvents(_.mounted("old")).clear()

      // -- None while unmounted: the remount must show the placeholder, not the stale span. --
      unmount()
      valueVar.set(None)
      mount(host)
      expectNode(div.of("before", sentinel, emptyCommentNode, "after"))
      tracker.assertEvents(_.unmounted("old")).clear()

      // -- And a value that returns live replaces the placeholder. --
      valueVar.set(Some(newSpan))
      expectNode(div.of("before", sentinel, span.of("new"), "after"))
      tracker.assertEvents(_.mounted("new")).clear()
    }

    it("renders a child that appeared while unmounted (started as None)") {
      val valueVar = Var[Option[ChildNode.Base]](None)
      val host = div("before", child.maybe <-- valueVar, "after")
      mount(host)
      expectNode(div.of("before", sentinel, emptyCommentNode, "after"))

      // -- A value appears while unmounted; the remount must render it over the placeholder. --
      unmount()
      valueVar.set(Some(span("late")))
      mount(host)
      expectNode(div.of("before", sentinel, span.of("late"), "after"))
    }
  }
}
