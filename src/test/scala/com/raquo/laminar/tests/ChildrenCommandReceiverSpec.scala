package com.raquo.laminar.tests

import com.raquo.domtestutils.matching.Rule
import com.raquo.laminar.api.L._
import com.raquo.laminar.domapi.{DomApi, DomError}
import com.raquo.laminar.inserters.CollectionCommand.{Append, Insert, Prepend, Remove, RemoveAll, Replace, ReplaceAll}
import com.raquo.laminar.inserters.{CollectionCommand, DynamicInserter, InsertContext, Inserter}
import com.raquo.laminar.fixtures.TestableOwner
import com.raquo.laminar.utils.UnitSpec

import scala.collection.immutable

class ChildrenCommandReceiverSpec extends UnitSpec {

  private val text0 = randomString("text0_")
  private val text00 = randomString("text00_")
  private val text1 = randomString("text1_")
  private val text2 = randomString("text2_")
  private val text3 = randomString("text3_")
  private val text4 = randomString("text4_")
  private val text5 = randomString("text5_")
//  private val text6 = randomString("text6_")
//  private val text7 = randomString("text7_")

  it("updates a list of children") {
    val commandBus = new EventBus[CollectionCommand[Node]]

    val span0 = span(text0)
    val span1 = span(text1)
    val div2 = div(text2)
    val div3 = div(text3)
    val span4 = span(text4)
    val span5 = span(text5)

    val el = div(
      "Hello",
      children.command <-- commandBus.events,
      div("World")
    )

    mount(el)
    expectChildren("none")

    commandBus.writer.onNext(Append(span0))
    expectChildren("append #1:", span of text0)

    commandBus.writer.onNext(Append(span1))
    expectChildren("append #2:", span of text0, span of text1)

    commandBus.writer.onNext(Prepend(div2))
    expectChildren("prepend:", div of text2, span of text0, span of text1)

    commandBus.writer.onNext(Remove(span0))
    expectChildren("remove:", div of text2, span of text1)

    commandBus.writer.onNext(Replace(span1, div3))
    expectChildren("replace:", div of text2, div of text3)

    commandBus.writer.onNext(Append(span4))
    expectChildren("append #3:", div of text2, div of text3, span of text4)

    commandBus.writer.onNext(Insert(span5, atIndex = 2))
    expectChildren("insert:", div of text2, div of text3, span of text5, span of text4)

    def expectChildren(clue: String, childRules: Rule*): Unit = {
      withClue(clue) {
        val first: Rule = "Hello"
        val last: Rule = div of "World"
        val rules: immutable.Seq[Rule] = first +: (sentinel: Rule) +: childRules :+ (sentinel: Rule) :+ last

        expectNode(div.of(rules: _*))
      }
    }
  }

  it("RemoveAll and ReplaceAll") {
    val commandBus = new EventBus[CollectionCommand[Node]]

    val span0 = span(text0)
    val span1 = span(text1)
    val div2 = div(text2)
    val div3 = div(text3)
    val span4 = span(text4)

    val el = div(
      "Hello",
      children.command <-- commandBus.events,
      div("World")
    )

    mount(el)
    expectChildren("initial:")

    commandBus.writer.onNext(Append(span0))
    commandBus.writer.onNext(Append(span1))
    commandBus.writer.onNext(Prepend(div2))
    expectChildren("built up:", div of text2, span of text0, span of text1)

    // RemoveAll clears all tracked content, but keeps the sentinels in place.
    commandBus.writer.onNext(RemoveAll)
    expectChildren("after RemoveAll:")

    // Commands keep working after a full clear.
    commandBus.writer.onNext(Append(span4))
    expectChildren("append after RemoveAll:", span of text4)

    // ReplaceAll swaps the entire contents, in order.
    commandBus.writer.onNext(ReplaceAll(div2 :: div3 :: Nil))
    expectChildren("after ReplaceAll:", div of text2, div of text3)

    // ReplaceAll with an empty seq is equivalent to RemoveAll.
    commandBus.writer.onNext(ReplaceAll(Nil))
    expectChildren("after empty ReplaceAll:")

    // Still functional after an empty ReplaceAll.
    commandBus.writer.onNext(Append(span0))
    expectChildren("append after empty ReplaceAll:", span of text0)

    commandBus.writer.onNext(Append(span1))
    commandBus.writer.onNext(Append(div2))
    expectChildren("built up again:", span of text0, span of text1, div of text2)

    // ReplaceAll may include nodes that are currently rendered (span0, span1 here): they get
    // torn down along with everything else, then re-inserted in the new order next to the new
    // nodes. So this keeps span1 & span0 (reordered), drops div2, and adds a fresh div3.
    commandBus.writer.onNext(ReplaceAll(span1 :: div3 :: span0 :: Nil))
    expectChildren("after ReplaceAll reusing rendered nodes:", span of text1, div of text3, span of text0)

    def expectChildren(clue: String, childRules: Rule*): Unit = {
      withClue(clue) {
        val first: Rule = "Hello"
        val last: Rule = div of "World"
        val rules: immutable.Seq[Rule] = first +: (sentinel: Rule) +: childRules :+ (sentinel: Rule) :+ last
        expectNode(div.of(rules: _*))
      }
    }
  }

  // https://github.com/raquo/Laminar/issues/195
  it("does not drift the append position when Append fails") {
    withCollectedAirstreamErrors { errors =>
      val commandBus = new EventBus[CollectionCommand[Node]]

      val spanA = span(text0)
      val spanB = span(text1)
      val spanC = div(text2)

      val el = div(
        "Hello",
        children.command <-- commandBus.events,
        div("World")
      )

      mount(el)
      expectChildren(clue = "initial:")()

      commandBus.writer.onNext(Append(spanA))
      expectChildren(clue = "after append A:")(
        span of text0
      )

      commandBus.writer.onNext(Append(spanB))
      expectChildren(clue = "after append B:")(
        span of text0,
        span of text1
      )

      // Appending el to itself throws a HierarchyRequestError DOMException,
      // so insertChildAtIndex returns false and nothing is inserted.
      commandBus.writer.onNext(Append(el))
      expectChildren(clue = "after failed self-append:")(
        span of text0,
        span of text1
      )

      // A failed append must not shift where the NEXT append lands. With the trailing
      // sentinel there is no node count to drift: Append always inserts right before the
      // trailing sentinel, so spanC lands before div("World"), not after it.
      commandBus.writer.onNext(Append(spanC))
      expectChildren(clue = "after append C:")(
        span of text0,
        span of text1,
        div of text2
      )

      assertEquals(errors.size, 1)
      assert(errors.head.isInstanceOf[DomError])

      errors.clear()

      def expectChildren(clue: String)(childRules: Rule*): Unit = {
        withClue(clue) {
          val first: Rule = "Hello"
          val last: Rule = div of "World"
          val rules: immutable.Seq[Rule] = first +: (sentinel: Rule) +: childRules :+ (sentinel: Rule) :+ last
          expectNode(div.of(rules: _*))
        }
      }
    }
  }

  // White-box companion to #195: a command whose DOM op throws or no-ops must not record its
  // node in contentMap, else the map accumulates phantom entries for nodes never in our span.
  it("does not leave phantom contentMap entries after a failed or no-op DOM write") {
    withCollectedAirstreamErrors { errors =>
      val commandBus = new EventBus[CollectionCommand[Node]]
      val owner = new TestableOwner

      val span0 = span(text0)
      val span1 = span(text1)
      val span2 = span(text2)
      val span3 = span(text3)
      val otherParent = div()

      val el = div()
      mount(el)

      // Drive the command inserter over a hand-built context so we can inspect its contentMap.
      val ctx = InsertContext.reserveSpotContext(el)
      val inserter = (children.command <-- commandBus.events).asInstanceOf[DynamicInserter]
      inserter.renderIntoSharedContext(ctx, owner)

      withClue("failed insert (appending el into its own span throws):") {
        commandBus.writer.onNext(Append(span0))
        commandBus.writer.onNext(Append(el))
        assertEquals(errors.size, 1)
        assert(errors.head.isInstanceOf[DomError])
        expectNode(el.ref, div of (sentinel, span of text0, sentinel))
        assert(ctx.contentMap.has(span0.ref)) // accepted node is tracked
        assert(!ctx.contentMap.has(el.ref)) // rejected node is not
        errors.clear()
      }

      withClue("no-op Replace (old node stolen away first):") {
        commandBus.writer.onNext(Append(span1))
        assert(ctx.contentMap.has(span1.ref))
        // Steal span1 into another parent, so replaceChild can no longer find it here.
        DomApi.appendChild(parent = otherParent, child = span1, slotName = ())
        commandBus.writer.onNext(Replace(span1, span2))
        expectNode(el.ref, div of (sentinel, span of text0, sentinel))
        assert(!ctx.contentMap.has(span2.ref)) // never inserted -> never tracked
        assert(!ctx.contentMap.has(span1.ref)) // stolen old node -> we drop our stale claim
        assert(span1.ref.parentNode == otherParent.ref) // thief keeps it
        assertEquals(errors.size, 0)
      }

      withClue("no-op Remove (node stolen away first):") {
        commandBus.writer.onNext(Append(span3))
        assert(ctx.contentMap.has(span3.ref))
        // Steal span3 away; the Remove below then no-ops in the DOM.
        DomApi.appendChild(parent = otherParent, child = span3, slotName = ())
        commandBus.writer.onNext(Remove(span3))
        assert(!ctx.contentMap.has(span3.ref)) // no longer ours -> dropped
        assert(span3.ref.parentNode == otherParent.ref) // thief undisturbed
        assertEquals(errors.size, 0)
      }
    }
  }

  it("Insert resolves a negative index from the end and clamps out-of-range indices into the span") {
    // Insert's index addresses a slot within the command's own span. A negative index counts
    // from the end (-1 = before the last node); any index outside [0, size] is clamped, so the
    // node always lands inside the span (0 prepends, size appends) rather than escaping past a
    // sentinel into a sibling's territory. An out-of-range index also reports an unhandled
    // error, since it signals a likely mistake in the caller's index math.
    val commandBus = new EventBus[CollectionCommand[Node]]

    val spanA = span(text0)
    val spanB = span(text1)
    val spanC = span(text2)
    val x = div(text3)

    val el = div(
      "Hello",
      children.command <-- commandBus.events,
      div("World")
    )

    mount(el)

    commandBus.writer.onNext(Append(spanA))
    commandBus.writer.onNext(Append(spanB))
    commandBus.writer.onNext(Append(spanC))
    expectChildren("built up [a, b, c]:", span of text0, span of text1, span of text2)

    // -- positive indices --

    commandBus.writer.onNext(Insert(x, atIndex = 0))
    expectChildren("index 0 prepends:", div of text3, span of text0, span of text1, span of text2)
    commandBus.writer.onNext(Remove(x))

    commandBus.writer.onNext(Insert(x, atIndex = 1))
    expectChildren("index 1 lands before the 2nd node:", span of text0, div of text3, span of text1, span of text2)
    commandBus.writer.onNext(Remove(x))

    commandBus.writer.onNext(Insert(x, atIndex = 3))
    expectChildren("index 3 (== size) appends:", span of text0, span of text1, span of text2, div of text3)
    commandBus.writer.onNext(Remove(x))

    withCollectedAirstreamErrors { errors =>
      commandBus.writer.onNext(Insert(x, atIndex = 99))
      assert(errors.size == 1, s"out-of-range Insert should report one error, got: ${errors.mkString("; ")}")
      assert(errors.head.isInstanceOf[DomError])
    }
    expectChildren("index past the end clamps to append:", span of text0, span of text1, span of text2, div of text3)
    commandBus.writer.onNext(Remove(x))

    // -- negative indices count from the end --

    commandBus.writer.onNext(Insert(x, atIndex = -1))
    expectChildren("index -1 lands before the last node:", span of text0, span of text1, div of text3, span of text2)
    commandBus.writer.onNext(Remove(x))

    commandBus.writer.onNext(Insert(x, atIndex = -2))
    expectChildren("index -2 lands before the 2nd-to-last node:", span of text0, div of text3, span of text1, span of text2)
    commandBus.writer.onNext(Remove(x))

    commandBus.writer.onNext(Insert(x, atIndex = -3))
    expectChildren("index -3 (== -size) prepends:", div of text3, span of text0, span of text1, span of text2)
    commandBus.writer.onNext(Remove(x))

    withCollectedAirstreamErrors { errors =>
      commandBus.writer.onNext(Insert(x, atIndex = -99))
      assert(errors.size == 1, s"out-of-range Insert should report one error, got: ${errors.mkString("; ")}")
      assert(errors.head.isInstanceOf[DomError])
    }
    expectChildren("index below -size clamps to prepend:", div of text3, span of text0, span of text1, span of text2)
    commandBus.writer.onNext(Remove(x))

    expectChildren("span is back to [a, b, c] after all inserts + removals:", span of text0, span of text1, span of text2)

    def expectChildren(clue: String, childRules: Rule*): Unit = {
      withClue(clue) {
        val first: Rule = "Hello"
        val last: Rule = div of "World"
        val rules: immutable.Seq[Rule] = first +: (sentinel: Rule) +: childRules :+ (sentinel: Rule) :+ last
        expectNode(div.of(rules: _*))
      }
    }
  }

  it("Insert at Int.MinValue clamps to the start of a populated span and reports an error") {
    val commandBus = EventBus[CollectionCommand[Node]]()
    val el = div(
      "Hello",
      children.command <-- commandBus.events,
      div("World")
    )

    mount(el)
    commandBus.writer.onNext(Append(span(text0)))
    commandBus.writer.onNext(Append(span(text1)))

    withCollectedAirstreamErrors { errors =>
      commandBus.writer.onNext(Insert(div(text2), atIndex = Int.MinValue))
      expectNode(div.of("Hello", sentinel, div of text2, span of text0, span of text1, sentinel, div of "World"))
      assert(errors.size == 1, s"out-of-range Insert should report one error, got: ${errors.mkString("; ")}")
      assert(errors.head.isInstanceOf[DomError])
    }
  }

  it("Insert at Int.MinValue stays inside an empty span and reports an error") {
    val commandBus = EventBus[CollectionCommand[Node]]()
    val el = div(
      "Hello",
      children.command <-- commandBus.events,
      div("World")
    )

    mount(el)

    withCollectedAirstreamErrors { errors =>
      commandBus.writer.onNext(Insert(div(text0), atIndex = Int.MinValue))
      expectNode(div.of("Hello", sentinel, div of text0, sentinel, div of "World"))
      assert(errors.size == 1, s"out-of-range Insert should report one error, got: ${errors.mkString("; ")}")
      assert(errors.head.isInstanceOf[DomError])
    }
  }

  it("Insert into an empty command span always lands the single node inside the span") {
    val commandBus = new EventBus[CollectionCommand[Node]]
    val x = div(text0)

    val el = div(
      "Hello",
      children.command <-- commandBus.events,
      div("World")
    )

    mount(el)
    expectChildren("empty span:")

    // A large positive index clamps to append; on an empty span that is also the front.
    withCollectedAirstreamErrors { errors =>
      commandBus.writer.onNext(Insert(x, atIndex = 99))
      assert(errors.size == 1, s"out-of-range Insert should report one error, got: ${errors.mkString("; ")}")
      assert(errors.head.isInstanceOf[DomError])
    }
    expectChildren("index past the end on an empty span:", div of text0)
    commandBus.writer.onNext(Remove(x))
    expectChildren("empty again:")

    // A negative index clamps to prepend on an empty span too.
    withCollectedAirstreamErrors { errors =>
      commandBus.writer.onNext(Insert(x, atIndex = -5))
      assert(errors.size == 1, s"out-of-range Insert should report one error, got: ${errors.mkString("; ")}")
      assert(errors.head.isInstanceOf[DomError])
    }
    expectChildren("negative index on an empty span:", div of text0)

    def expectChildren(clue: String, childRules: Rule*): Unit = {
      withClue(clue) {
        val first: Rule = "Hello"
        val last: Rule = div of "World"
        val rules: immutable.Seq[Rule] = first +: (sentinel: Rule) +: childRules :+ (sentinel: Rule) :+ last
        expectNode(div.of(rules: _*))
      }
    }
  }

  it("an out-of-range command Insert lands inside the span, so RemoveAll reaches it and outer reconciliation is not blocked") {
    // A `children.command <--` nested as an item of an enclosing `children <--` list must keep
    // every node it inserts between its own sentinels, even for an out-of-range Insert index.
    // Otherwise the node would escape into the enclosing list, where RemoveAll couldn't reach it
    // and the enclosing list's own reconciliation would trip over an untracked stray node.
    val tracker = createEventTracker()
    val e = tracker.createSpan("E")
    tracker.clear()
    val bus = EventBus[CollectionCommand[Node]]()
    val cmd: Inserter = children.command <-- bus.events
    val items = Var[List[Inserter]](List(cmd, e))
    val host = div(children <-- items.signal)

    withClue("command span holds `a`, followed by E in the enclosing list:") {
      mount(host)
      bus.emit(CollectionCommand.Append(tracker.createDiv("a")))
      tracker.assertEvents(_.mounted("E"), _.elementCreated("a"), _.mounted("a")).clear()
      expectNode(div.of(sentinel, sentinel, div of "a", sentinel, span of "E", sentinel))
    }

    withClue("Insert at index 3 into a 1-node span clamps to append inside the span, not after E:") {
      withCollectedAirstreamErrors { errors =>
        bus.emit(CollectionCommand.Insert(tracker.createDiv("n"), atIndex = 3))
        assert(errors.size == 1, s"out-of-range Insert should report one error, got: ${errors.mkString("; ")}")
        assert(errors.head.isInstanceOf[DomError])
      }
      tracker.assertEvents(_.elementCreated("n"), _.mounted("n")).clear()
      expectNode(div.of(sentinel, sentinel, div of "a", div of "n", sentinel, span of "E", sentinel))
    }

    withClue("RemoveAll reaches every node the command inserted:") {
      bus.emit(CollectionCommand.RemoveAll)
      tracker.assertEvents(_.unmounted("a"), _.unmounted("n")).clear()
      expectNode(div.of(sentinel, sentinel, sentinel, span of "E", sentinel))
    }

    withClue("the enclosing list keeps reconciling normally afterwards:") {
      withCollectedAirstreamErrors { errors =>
        items.set(List(cmd))
        items.set(Nil)
        assert(errors.isEmpty, s"enclosing list reported: ${errors.mkString("; ")}")
      }
      tracker.assertEvents(_.unmounted("E")).clear()
      expectNode(div.of(sentinel, sentinel))
    }
  }

  it("clearing a command span after an out-of-range Insert leaves the enclosing list able to remove its own items") {
    // Companion to the containment test above: verify the full clear + outer-removal lifecycle,
    // confirming every inserted node (including the clamped one) unmounts and nothing is orphaned.
    val tracker = createEventTracker()
    val e = tracker.createSpan("E")
    tracker.clear()
    val bus = EventBus[CollectionCommand[Node]]()
    val cmd: Inserter = children.command <-- bus.events
    val items = Var[List[Inserter]](List(cmd, e))

    withClue("command span holds `a`, followed by E in the enclosing list:") {
      mount(div(children <-- items.signal))
      bus.emit(CollectionCommand.Append(tracker.createDiv("a")))
      tracker.assertEvents(
        _.mounted("E"),
        _.elementCreated("a"),
        _.mounted("a")
      ).clear()
      expectNode(div.of(sentinel, sentinel, div of "a", sentinel, span of "E", sentinel))
    }

    withClue("Insert at index 2, clear the command span, then drop E:") {
      withCollectedAirstreamErrors { errors =>
        bus.emit(CollectionCommand.Insert(tracker.createDiv("n"), atIndex = 2))
        assert(errors.size == 1, s"out-of-range Insert should report one error, got: ${errors.mkString("; ")}")
        assert(errors.head.isInstanceOf[DomError])
      }
      tracker.assertEvents(
        _.elementCreated("n"),
        _.mounted("n")
      ).clear()
      bus.emit(CollectionCommand.RemoveAll)
      withCollectedAirstreamErrors { errors =>
        items.set(List(cmd))
        assert(errors.isEmpty, s"enclosing list reported: ${errors.mkString("; ")}")
      }
      tracker.assertEvents(
        _.unmounted("a"),
        _.unmounted("n"),
        _.unmounted("E")
      ).clear()
      expectNode(div.of(sentinel, sentinel, sentinel, sentinel))
    }

    withClue("removing the empty command group leaves no orphaned nodes:") {
      items.set(Nil)
      tracker.assertNoEvents.clear()
      expectNode(div.of(sentinel, sentinel))
    }
  }
}
